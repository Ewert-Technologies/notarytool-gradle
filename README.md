[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Static Badge](https://img.shields.io/badge/Status-Beta-orange)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/ca.ewert-technologies.notarytoolgradle?color=blue)](https://plugins.gradle.org/plugin/ca.ewert-technologies.notarytoolgradle)


# notarytool-gradle

## Description
A Gradle Plugin for working with Apple's Notarytool Web API. Allows software files to be notarized as part of the 
gradle build script. Use of this plugin, doesn't depend on the operating system and so can be used on older versions
of macOS, Windows and Linux.

## Background
Apple requires that all software applications for macOS need to be notarized, in order to give "*even more 
confidence in your macOS software*", see
[Notarizing macOS software before distribution](https://developer.apple.com/documentation/security/notarizing_macos_software_before_distribution).
Originally, applications were notarized using a command line tool called `altool`. Apple has now switched to using a
new command line tool called `notarytool` 
(see [TN3147: Migrating to the latest notarization tool](https://developer.apple.com/documentation/technotes/tn3147-migrating-to-the-latest-notarization-tool)),
and as of November 1, 2023 Apple's notary service no longer accepts uploads using `altool`. The `notarytool` utility
requires Xcode 13 and macOS 11.3 or later. This makes notarizing software applications on older versions of macOS a
challenge. In addition, the `notarytool` utility is only available on macOS, so it can't be used on other Operating
Systems. To help with this, Apple supplies the [Notary API Web Service](https://developer.apple.com/documentation/notaryapi),
which allows cross-platform notarization using a REST API.

This plugin uses the Notary API Web API to notarize software applications and so it can be used with older versions of 
macOS, as well as other Operating Systems such as Windows and Linux. The plugin also allows seamless integration with an 
existing gradle build, so applications can be built and notarized in one process.

## Project Status
This project is in development and its usage and API may be unstable. It has been tested and can be considered as being 
beta.

## Versioning
This project adheres to [Semantic Versioning 2.0.0](https://semver.org/).

## Contributing
This project is not currently accepting any pull requests. If you find a problem, or have a suggestion please
log an [issue](https://github.com/Ewert-Technologies/notarytool-kotlin/issues).

## Installation
The plugin in available from the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/ca.ewert-technologies.notarytoolgradle).

To use this plugin, add the following to your build script:
```kotlin
plugins {
  id("ca.ewert-technologies.notarytoolgradle") version "0.1.0"
}
```

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
In order to use the plugin, it must be configured with the Authentication information obtained as above. To
configure the Authentication information, add the following configuration block to your gradle build script: 

```kotlin
configure<NotaryToolGradlePluginExtension> {
  val path: Path = Paths.get("path", "to", "privateKeyFile.p8")
  privateKeyId.set("<Private Key ID>")
  issuerId.set("<Issuer ID here>")
  privateKeyFile.set(path)
}
```
NOTE: The privateKeyId and issuerId should not be stored directly in the build script as this could potentially
expose private information. Use something like the
[gradle-credentials-plugin](https://github.com/etiennestuder/gradle-credentials-plugin) to secure this
information.

If the configuration block has not been added the following error is displayed when applying the plugin:

```
Could not create task ':submissionHistory'.
Could not create task of type 'SubmissionHistoryTask'.
Cannot query the value of extension 'notarytool-gradle-extension' property 'issuerId' because it has no value available.
```


### Tasks
The plugin adds the following tasks to the build, grouped under `notarytool`:

- [`submissionHistory`](#submissionHistory)
- [`submissionStatus`](#submissionStatus)
- [`submitSoftware`](#submitSoftware)

#### submissionHistory
This task retrieves a list, from oldest to newest, of previous notarization submissions along with their status. The 
last 100 submissions are returned. For each item the submission id, status, uploaded date, and uploaded file are 
displayed.

To get help on the task use:
```
./gradlew -q help --task submissionHistory
```

which provides the following:

```
Detailed task information for submissionHistory

Path
     :submissionHistory

Type
     SubmissionHistoryTask (ca.ewert.notarytool.gradle.tasks.SubmissionHistoryTask)

Options
     --rerun     Causes the task to be re-run even if up-to-date.

Description
     Retrieves a list of previous notarization submissions.

Group
     notarytool

```
This task does not require any command-line options

Example, running

```
./gradlew submissionHistory
```

would produce the following sample output:
```
Submission ID                            Status          Upload Date                         Uploaded File Name
a014d72f-1234-45ac-abcd-8f39b9241c58     Accepted        Jun. 12, 2023, 12:27:13 p.m. PDT    pwm_3.3.3.0_aarch64.dmg
b685647e-1234-4343-abcd-1c5786499827     Accepted        Jun. 13, 2023, 2:32:58 p.m. PDT     PWMinder.zip
c6da5f3b-1234-4197-abcd-c83bac3d2953     Accepted        Jul. 13, 2023, 10:35:58 a.m. PDT    pwm_3.3.3.0_aarch64.dmg
d653220d-1234-4682-abcd-9a4fe87ebbff     Invalid         Aug. 22, 2023, 10:43:45 a.m. PDT    pwm_invalid_aarch64.dmg
aaf8606e-1234-4a44-abcd-4c11cb94249e     Invalid         Oct. 31, 2023, 3:26:38 p.m. PDT     pwm_invalid_aarch64.dmg
bdd929bf-1234-4a8e-abcd-4d67d33e6d7f     Invalid         Oct. 31, 2023, 3:51:47 p.m. PDT     pwm_invalid_aarch64.dmg
cc0e5bc0-1234-4d03-abcd-55959c31bad7     Invalid         Oct. 31, 2023, 4:12:21 p.m. PDT     pwm_invalid_aarch64.dmg
d8c05db7-1234-495c-abcd-f9647d16ffcd     Invalid         Nov. 1, 2023, 2:33:08 p.m. PDT      pwm_invalid_aarch64.dmg
ecb300ed-1234-4ebc-abcd-84f07d054663     Accepted        Nov. 1, 2023, 2:38:35 p.m. PDT      pwm_good_aarch64.dmg
```

#### submissionStatus
This task retrieves the status of a notarization submission. If the status is `Accepted` or `Invalid` it also provides
a link to download the associated submission log. 

To get help on the task use:
```
./gradlew -q help --task submissionStatus
```

which provides the following:

```
Detailed task information for submissionStatus

Path
     :submissionStatus

Type
     SubmissionStatusTask (ca.ewert.notarytool.gradle.tasks.SubmissionStatusTask)

Options
     --submissionId     The identifier that you received after submitting the software.

     --rerun     Causes the task to be re-run even if up-to-date.

Description
     Retrieves the status of a notarization submission.

Group
     notarytool
```

The only mandatory option is `--submissionId`, where the argument is the identifier for a previous submission.

Example, running:

```
./gradlew submissionStatus --submissionId b014d72f-17b6-45ac-abcd-8f39b9241c58
```
would produce the following sample output:
```
Starting task: submissionStatus
Status for submission id b014d72f-17b6-45ac-abcd-8f39b9241c58: Accepted
Submission Log: https://notary-artifacts-prod.s3.amazonaws.com/...
```

#### submitSoftware
This task submits software to be notarized, and then repeatedly checks on the status of the submission
until it is either `Accepted`, `Rejected`, or `Invalid`.


To get help on the task use:
```
./gradlew -q help --task submitSoftware
```
which provides the following:

```
Detailed task information for submitSoftware

Path
     :submitSoftware

Type
     SubmitSoftwareTask (ca.ewert.notarytool.gradle.tasks.SubmitSoftwareTask)

Options
     --fileLocation     Location (path) of the file to be notarized.

     --rerun     Causes the task to be re-run even if up-to-date.

Description
     Submits software to be notarized.

Group
     notarytool

```
The only option required is `fileLocation`. This can be passed in from the command line, but can also be
set within the build script, which is likely the more common way to set it.

Example:

```kotlin
tasks.submitSoftware {
  fileLocation = "/Path/to/uploadFile.dmg"
}
```
Note that the fileLocation parameter is a `String`. It would typically be set based on a value from a task that
produces the file to be notarized (typically a `.dmg` or `.zip` file), for example the `jpackage` task.

As described above, after submitting the software to Apple to be notarized, it continues to check on the status of 
the submission. It polls for the status every 15 seconds, until the status is either `Accepted`, `Rejected` 
or `Invalid`. This typically takes less than 5 minutes, but can take longer. After 50 attempts, if the status is 
still `In Progress`, the task will end. You can then manually check the status of the submission
using the `submissionStatus` task, passing in the `submissionId`.

## Helpful Resources
The following is a list of references that may be helpful when creating and releasing a software application
for macOS.

- [badass-jlink-plugin](https://github.com/beryx/badass-jlink-plugin) - Gradle plugin that using jlink and 
jpackage to produce a dmg file, for modular applications.
- [badass-runtime-plugin](https://github.com/beryx/badass-runtime-plugin) - Gradle plugin that using jlink and
  jpackage to produce a dmg file, for non-modular applications.
- [Infinitekind appbundler](https://github.com/TheInfiniteKind/appbundler) - Create a macOS app bundle (`.app` file),
with an optional bundles jre
- [DMG Canvas](https://www.araelium.com/dmgcanvas) - Application used to create disk images (i.e. `.dmg` files)
- [gradle-credentials-plugin](https://github.com/etiennestuder/gradle-credentials-plugin) -  Allows you to encrypt and
 store credentials and other private information used within the build script.


## Electron, Unity, etc.
While plugin was designed primarily with notarizing application developed using Java in mind, it can be used for 
notarizing any file, for example those developed with Electron, Unity, etc.

## Support
For any issues or suggestions please use github [issues](https://github.com/Ewert-Technologies/notarytool-gradle/issues).

## License
This project is licensed under the [MIT License](https://mit-license.org/).
