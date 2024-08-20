[![ci-cd](https://github.com/gethop-dev/notifications.firebase/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/gethop-dev/notifications.firebase/actions/workflows/ci-cd.yml)
[![Clojars Project](https://img.shields.io/clojars/v/dev.gethop/notifications.firebase.svg)](https://clojars.org/dev.gethop/notifications.firebase)

# Firebase notifications

A [Duct](https://github.com/duct-framework/duct) library that
provides [Integrant](https://github.com/weavejester/integrant) keys
for managing notifications in [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/ ).

## Installation

[![Clojars Project](https://clojars.org/dev.gethop/notifications.firebase/latest-version.svg)](https://clojars.org/dev.gethop/notifications.firebase)

## Usage

### Configuration

To use this library add the following key to your configuration:

`:dev.gethop.notifications/firebase`

This key expects a configuration map with one mandatory key:

* `:google-credentials`: A map with five elements:
  * project-id
  * private-key-id
  * private-key
  * client-id
  * client-email

Key initialization returns a `Firebase` record that can be used to perform the Firebase operations described below.

#### Configuration example
```edn
   :dev.gethop.notifications/firebase
   {:google-credentials {:project-id #duct/env ["G_CREDENTIALS_PROJECT_ID" Str]
                         :private-key-id #duct/env ["G_CREDENTIALS_PRIVATE_KEY_ID" Str]
                         :private-key #duct/env ["G_CREDENTIALS_PRIVATE_KEY" Str]
                         :client-id #duct/env ["G_CREDENTIALS_CLIENT_ID" Str]
                         :client-email #duct/env ["G_CREDENTIALS_CLIENT_EMAIL" Str]}}
```

### Obtaining a `Firebase` record

If you are using the library as part of a [Duct](https://github.com/duct-framework/duct)-based project, adding any of the previous configurations to your `config.edn` file will perform all the steps necessary to initialize the key and return a `Firebase` record for the associated configuration. In order to show a few interactive usages of the library, we will do all the steps manually in the REPL.

First we require the relevant namespaces:

```clj
user> (require '[integrant.core :as ig]
               '[dev.gethop.notifications.core :as core])
nil
user>
```

Next we create the configuration var holding the Firebase integration configuration details:

```clj
user> (def config {:google-credentials {:project-id (System/getEnv "G_CREDENTIALS_PROJECT_ID")
                                        :private-key-id (System/getEnv "G_CREDENTIALS_PRIVATE_KEY_ID")
                                        :private-key (System/getEnv "G_CREDENTIALS_PRIVATE_KEY")
                                        :client-id (System/getEnv "G_CREDENTIALS_CLIENT_ID")
                                        :client-email (System/getEnv "G_CREDENTIALS_CLIENT_EMAIL")}})
#'user/config
user>
```

Now that we have all pieces in place, we can initialize the `:dev.gethop.notifications/firebase` Integrant key to get a `Firebase` record. As we are doing all this from the REPL, we have to manually require `dev.gethop.notifications.firebase` namespace, where the `init-key` multimethod for that key is defined (this is not needed when Duct takes care of initializing the key as part of the application start up):

``` clj
user> (require '[dev.gethop.notifications.firebase :as firebase])
nil
user>
```

And we finally initialize the key with the configuration defined above, to get our `Firebase` record:

``` clj
user> (def fb-record (->
                       config
                       (->> (ig/init-key :dev.gethop.notifications/firebase))))
#'user/fb-record
user> fb-record
#dev.gethop.notifications.firebase.Firebase{:service-credentials #object[com.google.auth.oauth2.ServiceAccountCredentials
                                                              0x307d05cf
                                                              "ServiceAccountCredentials{...}"}
user>
```
Now that we have our `Firebase` record, we are ready to use the methods defined by the protocols defined in `dev.gethop.notifications.core` namespace.

### Usage
#### `send-notification`
* parameters:
  - A `Firebase` record.
  - logger: usually a reference to `:duct/logger` key. But you can use any Integrant key derived from `:duct/logger` (such as `:duct.logger/timbre`).
  - recipient: registration token to send a message to. It can be a single value or a collection of multiple tokens.
  - message: a map with the information that will be send as a notification. The values can be strings, numbers, simple keywords or UUID's. They will be converted to strings due to a Firebase limitation.
  - options: optional configuration parameters that will be send in the [Firebase message object](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages#Message ). See the [docs](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages#Message ) for available keys.
* returning value:
  - `:success?` `true` if the notification was successfully sent to all the recipients. `false` if sending the notification failed for at least one recipient.
  - `:errors` list of errors that happened when sending the notification. It's one error for each recipient.
* Example:
```clj
user> (core/send-notification fb-record logger "token1" {:header "Hello"} {})
{:success? true}

user> (core/send-notification fb-record logger ["token1" "invalid-token"] {:header "Hello"} {})
{:success? false
 :errors [{:recipient "invalid-token"
           :error com.google.firebase.messaging.FirebaseMessagingException
           :error-details "The registration token is not a valid FCM registration token"}]}
```
* Example with options:

```clj
user> (core/send-notification fb-record logger "token1" {:header "Hello"} {:android {:priority :high}
                                                                           :apns {:headers {:apns-priority "10"}
                                                                                  :payload {:content-available true
                                                                                            :badge 0}})
{:success? true}

```
#### `send-notification-async`

Same as `send-notification`, but returns a Clojure
[Future](https://clojuredocs.org/clojure.core/future) object. The
return value can be obtained by derefing the object. Note that the
notification will be sent no matter if it was derefed or not. This can
be useful if we are not interested in the answer and we don't want to
block the main thread.

```clj
user> (def result (core/send-notification fb-record logger "token1" {:header "Hello"} {}))
#'dev.gethop.notifications.firebase/a

user> result
#<Future@4413771e: :pending>

user> @result
(wait until the request is ended)
{:success? true}

```

## License

Copyright (c) 2024 Biotz, SL.

The source code for the library is subject to the terms of the
Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
