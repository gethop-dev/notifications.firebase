(ns dev.gethop.notifications.firebase.apns
  (:require [clojure.walk :as walk])
  (:import [com.google.firebase.messaging ApnsConfig Aps]))

(defn- payload->Aps [{:keys [content-available badge]}]
  (cond-> (Aps/builder)
    content-available (.setContentAvailable content-available)
    badge (.setBadge badge)
    :always (.build)))

(defn apns-options->ApnsConfig
  "Sets message configuration options for Apple platform

  Sets the message builder configuration options that are specific to
  the Apple platform (APNS)."
  [{:keys [headers payload]}]
  (cond-> (ApnsConfig/builder)
    headers (.putAllHeaders (walk/stringify-keys headers))
    :always (.setAps (payload->Aps payload))
    :always (.build)))
