(ns magnet.notifications.core
  (:require [clojure.spec.alpha :as s]))

(s/def ::logger any?)
(s/def ::recipients (s/coll-of string? :kind seqable?))
(s/def ::recipient (s/or :single string?
                         :multiple ::recipients))
(s/def ::message-values (s/or :string string?
                              :number number?
                              :keyword simple-keyword?
                              :uuid uuid?))
(s/def ::message (s/map-of keyword? ::message-values))
(s/def ::opts map?)

(s/def ::send-notification-args (s/cat :this record?
                                       :logger ::logger
                                       :recipient ::recipient
                                       :message ::message
                                       :opts ::opts))

(s/def ::success boolean?)
(s/def ::errors (s/coll-of map? :kind seqable?))
(s/def ::send-notification-ret (s/keys :req-un [::success]
                                       :opt-un [::errors]))

(defprotocol Notifications
  (send-notification
    [this logger recipient message]
    [this logger recipient message opts]))
