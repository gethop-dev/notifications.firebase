(ns dev.gethop.notifications.firebase.android
  (:import [com.google.firebase.messaging AndroidConfig AndroidConfig$Priority]))

(defn- android-priority->AndroidMessagePriority [priority]
  (case priority
    :high (AndroidConfig$Priority/HIGH)
    (AndroidConfig$Priority/NORMAL)))

(defn android-options->AndroidConfig
  "Sets message configuration options for Android platform

  Sets the message builder configuration options that are specific to
  the Android platform."
  [{:keys [priority]}]
  (cond-> (AndroidConfig/builder)
    priority (.setPriority (android-priority->AndroidMessagePriority priority))
    :always (.build)))
