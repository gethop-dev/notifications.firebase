(ns magnet.notifications.firebase.config
  (:require [magnet.notifications.firebase.android :as android]
            [magnet.notifications.firebase.apns :as apns])
  (:import [com.google.firebase.messaging MulticastMessage$Builder]))

(defn set-message-config
  [^MulticastMessage$Builder builder {:keys [android apns]}]
  (cond-> builder
    android (.setAndroidConfig (android/android-options->AndroidConfig android))
    apns (.setApnsConfig (apns/apns-options->ApnsConfig apns))))
