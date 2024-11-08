(ns dev.gethop.notifications.firebase
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [dev.gethop.notifications.core :as core]
            [dev.gethop.notifications.firebase.config :as config]
            [duct.logger :refer [log]]
            [integrant.core :as ig])
  (:import [com.google.auth.oauth2 ServiceAccountCredentials]
           [com.google.firebase FirebaseApp FirebaseOptions]
           [com.google.firebase.messaging
            FirebaseMessaging FirebaseMessagingException MulticastMessage
            MulticastMessage$Builder SendResponse]
           [java.util UUID]))

(def ^:const ^:private firebase-scope
  "https://www.googleapis.com/auth/firebase.messaging")

(def ^:const ^:private multicast-recipient-limit
  500)

(defn- construct-service-credentials
  ^ServiceAccountCredentials
  [{:keys [client-id client-email private-key-id private-key project-id]}]
  (let [credentials (ServiceAccountCredentials/fromPkcs8
                     client-id client-email
                     (str/replace private-key "\\n" "\n")
                     private-key-id
                     [firebase-scope])]
    (-> (.toBuilder credentials)
        (.setProjectId project-id)
        (.build))))

(defn- random-firebase-app-name
  "Firebase Application names must be unique, so we create a random name
  to avoid collisions. This is important if any other Firebase library
  is being used, or multiple records of this one are initialized."
  []
  (str (UUID/randomUUID)))

(defn- init-firebase-app! ^FirebaseApp [google-credentials]
  (let [serviceCredentials (construct-service-credentials google-credentials)
        firebaseOptions (-> (FirebaseOptions/builder)
                            (.setCredentials serviceCredentials)
                            (.build))
        firebase-app-name (random-firebase-app-name)]
    (FirebaseApp/initializeApp firebaseOptions firebase-app-name)))

(defn- message->firebase-message [message]
  (reduce-kv (fn [m k v]
               (if (keyword? v)
                 (assoc m (name k) (name v))
                 (assoc m (name k) (str v))))
             {} message))

(defn- firebase-responses->errors [recipient firebase-responses]
  (reduce-kv
   (fn [errors k ^SendResponse v]
     (if (.isSuccessful v)
       errors
       (let [exception ^FirebaseMessagingException (.getException v)
             error-class (class exception)
             error-message (.getMessage exception)
             error-type (if (and (= error-class FirebaseMessagingException)
                                 (or (= error-message "Requested entity was not found.")
                                     (= error-message "The registration token is not a valid FCM registration token")))
                          ::core/invalid-recipient
                          ::core/other-error)]
         (conj errors {:recipient k
                       :error-type error-type
                       :implementation-error-details {:error-class error-class
                                                      :error-message error-message}}))))
   []
   (zipmap recipient firebase-responses)))

(defn- send-notification* [firebaseApp logger recipient firebase-message opts]
  (log logger :debug ::firebase-send-notification* {:firebaseApp firebaseApp
                                                    :recipient recipient
                                                    :firebase-message firebase-message
                                                    :opts opts})
  (let [multicast-message-builder (-> (MulticastMessage/builder)
                                      (.putAllData firebase-message)
                                      (.addAllTokens recipient)
                                      (config/set-message-config opts))
        multicast-message (.build ^MulticastMessage$Builder multicast-message-builder)
        response (-> (FirebaseMessaging/getInstance firebaseApp)
                     (.sendEachForMulticast multicast-message))]
    (if (= (.getSuccessCount response) (count recipient))
      {:success? true}
      (let [errors (firebase-responses->errors recipient (.getResponses response))]
        (log logger :error ::firebase-notification {:success-count (.getSuccessCount response)
                                                    :error-count (.getFailureCount response)
                                                    :errors errors})
        {:success? false :errors errors}))))

(defn- send-notification
  [firebaseApp logger recipient message opts]
  {:pre [(s/valid? ::core/logger logger)
         (s/valid? ::core/recipient recipient)
         (s/valid? ::core/message message)
         (s/valid? ::core/opts opts)]}
  (log logger :debug ::pre-send-notification {:message message
                                              :opts opts
                                              :recipient recipient})
  (let [firebase-message (message->firebase-message message)
        results (->> (flatten (vector recipient))
                     (partition-all multicast-recipient-limit)
                     (pmap #(send-notification* firebaseApp logger % firebase-message opts)))]
    (if (every? :success? results)
      {:success? true}
      (let [errors (reduce concat (keep :errors results))]
        (log logger :error ::pre-send-notification-error {:errors errors})
        {:success? false
         :errors errors}))))

(s/def ::firebaseApp #(instance? FirebaseApp %))
(s/def ::send-notification-args (s/cat :firebaseApp ::firebaseApp
                                       :logger ::core/logger
                                       :recipient ::core/recipient
                                       :message ::core/message
                                       :opts ::core/opts))

(s/fdef send-notification
  :args ::send-notification-args
  :ret ::core/send-notification-ret)

(defn- send-notification-async [firebaseApp logger recipient message opts]
  (log logger :debug ::firebase-notification-async)
  (future
    (send-notification firebaseApp logger recipient message opts)))

(s/fdef send-notification-async
  :args ::send-notification-args
  :ret ::core/send-notification-async-ret)

(defrecord Firebase [^FirebaseApp firebaseApp]
  core/Notifications
  (send-notification [_ logger recipient message]
    (send-notification firebaseApp logger recipient message {}))
  (send-notification [_ logger recipient message opts]
    (send-notification firebaseApp logger recipient message opts))
  (send-notification-async [_ logger recipient message]
    (send-notification-async firebaseApp logger recipient message {}))
  (send-notification-async [_ logger recipient message opts]
    (send-notification-async firebaseApp logger recipient message opts)))

(defn init-record
  "Initiates the Firebase record"
  [{:keys [google-credentials] :as _config}]
  (let [firebaseApp (init-firebase-app! google-credentials)]
    (->Firebase firebaseApp)))

(defn halt-record
  "Halts the Firebase record"
  [{:keys [^FirebaseApp firebaseApp] :as _record}]
  (.delete firebaseApp))

(defmethod ig/init-key :dev.gethop.notifications/firebase [_ config]
  (init-record config))

(defmethod ig/halt-key! :dev.gethop.notifications/firebase [_ record]
  (halt-record record))
