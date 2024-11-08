# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [UNRELEASED]

## [0.1.14] - 2024-11-04
### Changed
- Upgrade dependencies: 
  - `com.google.firebase/firebase-admin:9.4.1`
  - `org.clojure/clojure:1.12.0`
- Replace `.sendMulticast()` due to deprecation with `.sendEachForMulticast()` as the firebase-admin documentation suggests

## [0.1.13] - 2023-12-04
### Fixed
- Modify recipient specs to avoid blank strings

## [0.1.12] - 2022-08-31
### Added
- Add debug level logging to firebase functions

## [0.1.11] - 2022-08-28
### Added
- Facilitate using the library without Integrant

## [0.1.10] - 2022-10-06
### Fixed
- Integrant keys references not following dev.gethop pattern

## [0.1.9] - 2022-10-06
### Changed
- Moving the repository to [gethop-dev](https://github.com/gethop-dev) organization
- CI/CD solution switch from [TravisCI](https://travis-ci.org/) to [GitHub Actions](Ihttps://github.com/features/actions)
- `lein` dependency bump
- This Changelog file update

## [0.1.8] - 2022-04-03
### Added
- Add an additional internal implementation error that also produces a `:dev.gethop.notifications.core/invalid-recipient` error.

## [0.1.7] - 2022-04-25
- Return implementation agnostic errors. We now provide a spec for the errors returned from the library functions. The spec includes both the mandatory implementation-agnostic error values, and the optional implementation-specific error details.
- Make internal implementation details private
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

[UNRELEASED]: https://github.com/gethop-dev/notifications.firebase/compare/v0.1.14...HEAD
[0.1.14]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.13...0.1.14
[0.1.13]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.12...0.1.13
[0.1.12]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.11...0.1.12
[0.1.11]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.10...0.1.11
[0.1.10]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.9...0.1.10
[0.1.9]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.8...0.1.9
[0.1.8]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.7...0.1.8
[0.1.7]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.6...0.1.7
[0.1.6]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.5...0.1.6
[0.1.5]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.4...0.1.5
[0.1.4]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.3...0.1.4
[0.1.3]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/gethop-dev/notifications.firebase/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/gethop-dev/notifications.firebase/releases/tag/0.1.0
