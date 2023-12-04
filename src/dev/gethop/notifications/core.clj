(ns dev.gethop.notifications.core
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::logger any?)
(s/def ::recipients (s/coll-of string? :kind seqable?))
(s/def ::recipient (s/or :single (s/and string?
                                        (complement str/blank?))
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

(s/def ::success? boolean?)
(s/def ::error-type #{::invalid-recipient ::other-error})
(s/def ::implementation-error-details (s/keys :req-un [::error-class ::error-message]))
(s/def ::error (s/keys :req-un [::recipient ::error-type]
                       :opt-un [::implementation-error-details]))
(s/def ::errors (s/coll-of ::error :kind seqable?))
(s/def ::send-notification-ret (s/keys :req-un [::success?]
                                       :opt-un [::errors]))

(s/def ::send-notification-async-args ::send-notification-args)
(s/def ::send-notification-async-ret future?)

(defprotocol Notifications
  (send-notification
    [this logger recipient message]
    [this logger recipient message opts])
  (send-notification-async
    [this logger recipient message]
    [this logger recipient message opts]))
