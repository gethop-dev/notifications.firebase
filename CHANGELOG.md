# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.1.4] - 2020-08-31
- Implement notification priority configuration for Android and APN messages. The priority can now be set using the opts argument of the send-notification and send-notification-async methods.
## [0.1.3] - 2020-08-18
- Switch from the [Firebase REST API](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages) to the [Firebase Admin Java SDK](https://github.com/firebase/firebase-admin-java).
- Reimplement send-notification method to send notifications to multiple recipients in batches. This will significantly improve performance.
- Add send-notification-async method that returns a Future without waiting to the request to end.

## [0.1.2] - 2020-06-17
- Allow UUID's in the notification message

## [0.1.1] - 2020-06-17
- Downgrade http-kit dependency to stable version

## [0.1.0] - 2020-06-17
- First release
