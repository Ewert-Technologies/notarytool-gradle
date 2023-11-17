[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Static Badge](https://img.shields.io/badge/Status-In%20Development-orange)

# notarytool-gradle

## Description
A Gradle Plugin for working with Apple's Notarytool Web API. Allows software files to be notarized as part of the 
gradle build script. Use of this plugin, doesn't depend on the operating system and so can be using on older versions
of macOS, Windows and Linux.

## Background
Apples requires that all software application for macOS need to be notarized, in order to give "even more confidence in 
your macOS software", see
[Notarizing macOS software before distribution](https://developer.apple.com/documentation/security/notarizing_macos_software_before_distribution).
Originally applications were notarized using a command line tool called `altool`. Apple has now switched to using a
new command line tool called `notarytool` (see [TN3147: Migrating to the latest notarization tool](https://developer.apple.com/documentation/technotes/tn3147-migrating-to-the-latest-notarization-tool),
and as of November 1, 2023 Apples notary service no longer accepts uploads using `altool`. The `notarytool` utility
requires Xcode 13 and macOS 11.3 or later. This makes notarizing software applications on older versions of macOS a
challenge. In addition, the `notarytool` utility is only available on macOS, so it can't be used on other Operating
Systems. To help with this Apple supplies the [Notary API Web Service](https://developer.apple.com/documentation/notaryapi)
which allows cross-platform notarization using a REST api.

This plugin uses the Notary API to notarize software applications and so it can be used with older versions of macOS as
well as other Operating Systems such as Windows and Linux. The plugin also allows seamless integration with an existing
gradle build, so applications can be built and notarized in one process.

## Project Status
This project is in development, no release are publicly available yet.

## Versioning
This project adheres to [Semantic Versioning 2.0.0](https://semver.org/).

## Contributing
This project is not currently accepting any pull requests. If you find a problem, or have a suggestion please
log an [issue](https://github.com/Ewert-Technologies/notarytool-kotlin/issues).

## Installation
The plugin in available from the Gradle Plugin Portal.

TODO: add examples of how to include into build script

## Usage

### Pre-Requisites

To be able to notarize an application for macOS, you need to have an Apple Developer account, with access to App Store
Connect. In order to make calls to the Notary API, you first need to create an API Key,
see: [Creating API Keys for App Store Connect API](https://developer.apple.com/documentation/appstoreconnectapi/creating_api_keys_for_app_store_connect_api)
for instructions. After creating an API key, you should have the following information:

| Item           | Description                                                |
|----------------|------------------------------------------------------------|
| Issuer ID      | Your issuer ID from the API Keys page in App Store Connect |
| Private Key ID | Your Private key ID from App Store Connect                 |
| Private Key    | The `.p8` file downloaded when creating the API Key        |

These items are used to generate a [JSON Web Token (JWT)](https://jwt.io/), which is used for authentication when making
calls to the Notary API
(see
[Generating Tokens for API Requests](https://developer.apple.com/documentation/appstoreconnectapi/generating_tokens_for_api_requests)
for more information). Note: the JWT is generated for you automatically by this library.

### Configuration
In order to use the plugin, it must be configured with the Authentication information obtained as above.

```kotlin
configure<NotaryToolGradlePluginExtension> {
  val path: Path = Paths.get("path", "to", "privateKeyFile.p8")
  privateKeyId.set("<Private Key ID>")
  issuerId.set("<Issuer ID here>")
  privateKeyFile.set(path)
}
```

### Tasks
The plugin adds the following tasks to the build:

#### Submission History
This task Retrieves a list of previous notarization submissions along with their status. The last 100 submissions
are returned.

TODO give example

#### Submission Status
Retrieves the status of a notarization submission.

TODO give example

#### Submit Software
Submits software to be notarized.

TODO give example


## Support
For any issues or suggestions please use github [issues](https://github.com/Ewert-Technologies/notarytool-gradle/issues).

## License
This project is licensed under the [MIT License](https://mit-license.org/).
