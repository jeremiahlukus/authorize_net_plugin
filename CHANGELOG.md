## 0.2.0
- Fixed the Android build: migrated the plugin and the example app off the imperative Flutter Gradle apply (removed in current Flutter) to the declarative plugins block, moved to AGP 9 / Gradle 9, replaced dead `jcenter()` with `mavenCentral()`, and moved the namespace out of the manifests into `build.gradle`
- The AuthorizeNet Android SDK is now exposed as an `api` dependency, so apps no longer need to declare it themselves — only the jitpack repository is required
- `cardCvv`, `zipCode` and `cardHolderName` are now optional, matching the underlying Android and iOS SDKs where only the card number and expiration date are required. Empty strings are treated as omitted so the SDKs don't reject them during validation
- Annotated the `authorizeNetToken` parameters as `String` instead of leaving them `dynamic`

## 0.1.5
- Fixed build error on Flutter 3.x: removed unused `PluginRegistry.Registrar` import that no longer exists in the v2 embedding API

## 0.1.4
- Updated Dart SDK constraints to support latest versions
- Updated Android compileSdkVersion and added namespace for compatibility with newer Android Gradle plugin
- Modernized test suite with improved coverage and updated to use `TestDefaultBinaryMessengerBinding` API

## 0.1.3

## 0.1.2
Fixed error handling

## 0.1.1
Up pub score


## 0.1.0
Migrate to null saftey

## 0.0.3
Update readme

## 0.0.2
This time it works...


## 0.0.1

* TODO: Describe initial release.
