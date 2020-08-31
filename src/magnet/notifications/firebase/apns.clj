(ns magnet.notifications.firebase.apns
  (:require [clojure.walk :as walk])
  (:import [com.google.firebase.messaging ApnsConfig Aps]))

(defn apns-options->ApnsConfig
  [{:keys [headers]}]
  (cond-> (ApnsConfig/builder)
    headers (.putAllHeaders (walk/stringify-keys headers))
    :always (.setAps (.build (Aps/builder)))
    :always (.build)))
