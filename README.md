# KMP XCFramework Sample 

A simple Kotlin Multiplatform sample project to show how to build an [XCFramework](https://help.apple.com/xcode/mac/11.4/#/dev544efab96)

There are three branches:

- [kotlin-1.5.30](https://github.com/prof18/kmp-xcframework-sample/tree/kotlin-1.5.30): uses the official support for XCFramework that is landed with Kotlin 1.5.30
- [pre-kotlin-1.5.30](https://github.com/prof18/kmp-xcframework-sample/tree/pre-kotlin-1.5.30): uses a version of Kotlin prior to 1.5.30. To build and publish the XCFramework, there are some custom Gradle tasks defined in the [build.gradle.kts](https://github.com/prof18/kmp-xcframework-sample/blob/main/build.gradle.kts) file.
- [pre-kotlin-1.5.30-with-plugin](https://github.com/prof18/kmp-xcframework-sample/tree/pre-kotlin-1.5.30-with-plugin): uses a version of Kotlin prior to 1.5.30 and shows how the custom gradle tasks can be replaced by the [KMP FatFramework Cocoa](https://github.com/prof18/kmp-fatframework-cocoa) Gradle plugin. 

For all the details, please refer to these articles: 

- [How to build an XCFramework on Kotlin Multiplatform](https://www.marcogomiero.com/posts/2021/build-xcframework-kmp/)
- [Building an XCFramework on Kotlin Multiplatform from Kotlin 1.5.30](https://www.marcogomiero.com/posts/2021/kmp-xcframework-official-support/)


