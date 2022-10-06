(ns dev.gethop.notifications.firebase.config
  (:require [dev.gethop.notifications.firebase.android :as android]
            [dev.gethop.notifications.firebase.apns :as apns])
  (:import [com.google.firebase.messaging MulticastMessage$Builder]))

(defn set-message-config
  "Set message configuration depending on mobile platform

  Sets the message builder configuration options that are specific to
  each kind of supported mobile platform. It expects a map with
  `:android` (for Android deviceS) and/or `:apns` (for Apple devices)
  keys. Their values are the configuration options to set."
  [^MulticastMessage$Builder builder {:keys [android apns]}]
  (cond-> builder
    android (.setAndroidConfig (android/android-options->AndroidConfig android))
    apns (.setApnsConfig (apns/apns-options->ApnsConfig apns))))
