# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [UNRELEASED]

### Added
- Add an additional internal implementation error that also produces a `:magnet.notifications.core/invalid-recipient` error.

## [0.1.7] - 2022-04-25
- Return implementation agnostic errors. We now provide a spec for the errors returned from the library functions. The spec includes both the mandatory implementation-agnostic error values, and the optional implementation-specific error details.
- Make internal implementatino details private
- Upgrade to the latest version of Firebase library

## [0.1.6] - 2021-02-24
- Add the option to configure the APs badge key. Although it's not
  officially documented, according to our tests, it is mandatory to
  set the badge if the content-available is specified. Note that 0
  is also a valid value, and it's used to disable the badge.

## [0.1.5] - 2020-09-02
- Add option to configure the APs content-available key

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

[UNRELEASED]: https://github.com/magnetcoop/notifications.firebase/compare/v0.1.7...HEAD
[0.1.7] https://github.com/magnetcoop/notifications.firebase/compare/0.1.6...0.1.7
[0.1.6] https://github.com/magnetcoop/notifications.firebase/compare/0.1.5...0.1.6
[0.1.5] https://github.com/magnetcoop/notifications.firebase/compare/0.1.4...0.1.5
[0.1.4] https://github.com/magnetcoop/notifications.firebase/compare/0.1.3...0.1.4
[0.1.3] https://github.com/magnetcoop/notifications.firebase/compare/0.1.2...0.1.3
[0.1.2] https://github.com/magnetcoop/notifications.firebase/compare/0.1.1...0.1.2
[0.1.1] https://github.com/magnetcoop/notifications.firebase/compare/0.1.0...0.1.1
[0.1.0] https://github.com/magnetcoop/notifications.firebase/releases/tag/0.1.0
