# KMP XCFramework Sample 

A simple Kotlin Multiplatform sample project to show how to build an [XCFramework](https://help.apple.com/xcode/mac/11.4/#/dev544efab96)

To build and publish the XCFramework, there are some custom Gradle tasks defined in the [build.gradle.kts](https://github.com/prof18/kmp-xcframework-sample/blob/main/build.gradle.kts) file.

The tasks can be replaced by the [KMP FatFramework Cocoa](https://github.com/prof18/kmp-fatframework-cocoa) Gradle plugin.
An example of the usage can be found in the [build.gradle.kts](https://github.com/prof18/kmp-xcframework-sample/blob/with-plugin/build.gradle.kts)
file of the [with-plugin branch](https://github.com/prof18/kmp-xcframework-sample/tree/with-plugin).

For all the details, please refer to these articles: 

- [How to build an XCFramework on Kotlin Multiplatform](https://www.marcogomiero.com/posts/2021/build-xcframework-kmp/)
- [Building an XCFramework on Kotlin Multiplatform from Kotlin 1.5.30](https://www.marcogomiero.com/posts/2021/kmp-xcframework-official-support/)
