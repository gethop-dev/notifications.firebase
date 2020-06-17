(ns magnet.notifications.firebase
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [duct.logger :refer [log]]
            [integrant.core :as ig]
            [magnet.notifications.core :as core]
            [org.httpkit.client :as http]
            [clojure.spec.alpha :as s])
  (:import [com.google.auth.oauth2 ServiceAccountCredentials ServiceAccountCredentials$Builder AccessToken]))

(def ^:const base-url "https://fcm.googleapis.com/v1/projects/")
(def ^:const firebase-scope "https://www.googleapis.com/auth/firebase.messaging")

(defn ^ServiceAccountCredentials construct-service-credentials
  [{:keys [client-id client-email private-key-id private-key project-id]}]
  (let [credentials (ServiceAccountCredentials/fromPkcs8
                     client-id client-email (str/replace private-key "\\n" "\n") private-key-id [firebase-scope])]
    (-> (^ServiceAccountCredentials$Builder .toBuilder credentials)
        (.setProjectId project-id)
        (.build))))

(defn- get-access-token [^ServiceAccountCredentials service-credentials]
  (let [access-token (.refreshAccessToken service-credentials)]
    (^AccessToken .getTokenValue access-token)))

(defn- message->firebase-message [message]
  (reduce-kv (fn [m k v]
               (if (keyword? v)
                 (assoc m k (name v))
                 (assoc m k (str v))))
             {} message))

(defn send-notification* [logger access-token project-id message opts registration-token]
  (try
    (let [{:keys [status error]}
          @(http/request {:url (str base-url project-id "/messages:send")
                          :method :post
                          :oauth-token access-token
                          :headers {"Content-Type" "application/json"}
                          :body (json/generate-string
                                 {:message (merge
                                            {:token registration-token
                                             :data (message->firebase-message message)}
                                            opts)})})]
      (if (and (not error) (<= 200 status 299))
        {:success? true}
        (let [error {:status status
                     :error error
                     :recipient registration-token}]
          (log logger :error :firebase-notification (assoc error :message message))
          {:success? false
           :error-details error})))
    (catch Exception e
      (let [error {:reason (class e)
                   :error-details (.getMessage e)
                   :recipient registration-token}]
        (log logger :error :firebase-notification error)
        {:success? false
         :error-details error}))))

(defn send-notification [^ServiceAccountCredentials service-credentials logger recipient message opts]
  {:pre [(s/valid? ::core/logger logger)
         (s/valid? ::core/recipient recipient)
         (s/valid? ::core/message message)
         (s/valid? ::core/opts opts)]}
  (let [access-token (get-access-token service-credentials)
        project-id (.getProjectId service-credentials)
        send-fn (partial send-notification* logger access-token project-id message opts)
        results (pmap send-fn (flatten (vector recipient)))]
    (if (every? :success? results)
      {:success? true}
      {:success? false
       :errors (keep #(when-not (:success? %)
                        (:error-details %)) results)})))

(s/def ::service-credentials #(instance? ServiceAccountCredentials %))
(s/def ::send-notification-args (s/cat :service-credentials ::service-credentials
                                       :logger ::core/logger
                                       :recipient ::core/recipient
                                       :message ::core/message
                                       :opts ::core/opts))

(s/fdef send-notification
  :args ::send-notification-args
  :ret ::core/send-notification-ret)

(defrecord Firebase [^ServiceAccountCredentials service-credentials]
  core/Notifications
  (send-notification [this logger recipient message]
    (send-notification service-credentials logger recipient message {}))
  (send-notification [this logger recipient message opts]
    (send-notification service-credentials logger recipient message opts)))

(defmethod ig/init-key :magnet.notifications/firebase [_ {:keys [google-credentials]}]
  (let [service-credentials (construct-service-credentials google-credentials)]
    (->Firebase service-credentials)))
