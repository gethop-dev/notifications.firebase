(ns magnet.notifications.firebase.android
  (:import [com.google.firebase.messaging AndroidConfig AndroidConfig$Priority]))

(defn- android-priority->AndroidMessagePriority [priority]
  (case priority
    :high (AndroidConfig$Priority/HIGH)
    (AndroidConfig$Priority/NORMAL)))

(defn android-options->AndroidConfig
  [{:keys [priority] :as android-options}]
  (cond-> (AndroidConfig/builder)
    priority (.setPriority (android-priority->AndroidMessagePriority priority))
    :always (.build)))
