import java.util.*
import java.text.SimpleDateFormat

plugins {
    kotlin("multiplatform") version "1.5.10"
    id("com.android.library")
}

val libName = "LibraryName"
val libVersionName = "1.0.1"
group = "com.prof18"
version = libVersionName

repositories {
    google()
    jcenter()
    mavenCentral()
}

kotlin {
    android()
    ios {
        binaries.framework(libName)
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.material:material:1.2.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
            }
        }
        val iosMain by getting
        val iosTest by getting
    }

    tasks {
        register("buildDebugXCFramework", Exec::class.java) {
            description = "Create a Debug XCFramework"

            dependsOn("link${libName}DebugFrameworkIosArm64")
            dependsOn("link${libName}DebugFrameworkIosX64")

            val arm64FrameworkPath = "$rootDir/build/bin/iosArm64/${libName}DebugFramework/${libName}.framework"
            val arm64DebugSymbolsPath = "$rootDir/build/bin/iosArm64/${libName}DebugFramework/${libName}.framework.dSYM"

            val x64FrameworkPath = "$rootDir/build/bin/iosX64/${libName}DebugFramework/${libName}.framework"
            val x64DebugSymbolsPath = "$rootDir/build/bin/iosX64/${libName}DebugFramework/${libName}.framework.dSYM"

            val xcFrameworkDest = File("$rootDir/../kmp-xcframework-dest/$libName.xcframework")
            executable = "xcodebuild"
            args(mutableListOf<String>().apply {
                add("-create-xcframework")
                add("-output")
                add(xcFrameworkDest.path)

                // Real Device
                add("-framework")
                add(arm64FrameworkPath)
                add("-debug-symbols")
                add(arm64DebugSymbolsPath)

                // Simulator
                add("-framework")
                add(x64FrameworkPath)
                add("-debug-symbols")
                add(x64DebugSymbolsPath)
            })

            doFirst {
                xcFrameworkDest.deleteRecursively()
            }
        }

        register("buildReleaseXCFramework", Exec::class.java) {
            description = "Create a Release XCFramework"

            dependsOn("link${libName}ReleaseFrameworkIosArm64")
            dependsOn("link${libName}ReleaseFrameworkIosX64")

            val arm64FrameworkPath = "$rootDir/build/bin/iosArm64/${libName}ReleaseFramework/${libName}.framework"
            val arm64DebugSymbolsPath =
                "$rootDir/build/bin/iosArm64/${libName}ReleaseFramework/${libName}.framework.dSYM"

            val x64FrameworkPath = "$rootDir/build/bin/iosX64/${libName}ReleaseFramework/${libName}.framework"
            val x64DebugSymbolsPath = "$rootDir/build/bin/iosX64/${libName}ReleaseFramework/${libName}.framework.dSYM"

            val xcFrameworkDest = File("$rootDir/../kmp-xcframework-dest/$libName.xcframework")
            executable = "xcodebuild"
            args(mutableListOf<String>().apply {
                add("-create-xcframework")
                add("-output")
                add(xcFrameworkDest.path)

                // Real Device
                add("-framework")
                add(arm64FrameworkPath)
                add("-debug-symbols")
                add(arm64DebugSymbolsPath)

                // Simulator
                add("-framework")
                add(x64FrameworkPath)
                add("-debug-symbols")
                add(x64DebugSymbolsPath)
            })

            doFirst {
                xcFrameworkDest.deleteRecursively()
            }
        }

        register("publishDevFramework") {
            description = "Publish iOs framework to the Cocoa Repo"

            project.exec {
                workingDir = File("$rootDir/../kmp-xcframework-dest")
                commandLine("git", "checkout", "develop").standardOutput
            }

            dependsOn("buildDebugXCFramework")

            // Replace
            doLast {
                val dir = File("$rootDir/../kmp-xcframework-dest/$libName.podspec")
                val tempFile = File("$rootDir/../kmp-xcframework-dest/$libName.podspec.new")

                val reader = dir.bufferedReader()
                val writer = tempFile.bufferedWriter()
                var currentLine: String?

                while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
                    if (currentLine?.startsWith("s.version") == true) {
                        writer.write("s.version       = \"${libVersionName}\"" + System.lineSeparator())
                    } else {
                        writer.write(currentLine + System.lineSeparator())
                    }
                }
                writer.close()
                reader.close()
                val successful = tempFile.renameTo(dir)

                if (successful) {

                    project.exec {
                        workingDir = File("$rootDir/../kmp-xcframework-dest")
                        commandLine(
                            "git",
                            "add",
                            "."
                        ).standardOutput
                    }

                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                    project.exec {
                        workingDir = File("$rootDir/../kmp-xcframework-dest")
                        commandLine(
                            "git",
                            "commit",
                            "-m",
                            "\"New dev release: ${libVersionName}-${dateFormatter.format(Date())}\""
                        ).standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../kmp-xcframework-dest")
                        commandLine("git", "push", "origin", "develop").standardOutput
                    }
                }
            }
        }

        register("publishFramework") {
            description = "Publish iOs framework to the Cocoa Repo"

            project.exec {
                workingDir = File("$rootDir/../kmp-xcframework-dest")
                commandLine("git", "checkout", "master").standardOutput
            }

            // Create Release Framework for Xcode
            dependsOn("buildReleaseXCFramework")

            // Replace
            doLast {
                val dir = File("$rootDir/../kmp-xcframework-dest/$libName.podspec")
                val tempFile = File("$rootDir/../kmp-xcframework-dest/$libName.podspec.new")

                val reader = dir.bufferedReader()
                val writer = tempFile.bufferedWriter()
                var currentLine: String?

                while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
                    if (currentLine?.startsWith("s.version") == true) {
                        writer.write("s.version       = \"${libVersionName}\"" + System.lineSeparator())
                    } else {
                        writer.write(currentLine + System.lineSeparator())
                    }
                }
                writer.close()
                reader.close()
                val successful = tempFile.renameTo(dir)

                if (successful) {

                    project.exec {
                        workingDir = File("$rootDir/../kmp-xcframework-dest")
                        commandLine(
                            "git",
                            "add",
                            "."
                        ).standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../kmp-xcframework-dest")
                        commandLine("git", "commit", "-m", "\"New release: ${libVersionName}\"").standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../kmp-xcframework-dest")
                        commandLine("git", "tag", libVersionName).standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../kmp-xcframework-dest")
                        commandLine("git", "push", "origin", "master", "--tags").standardOutput
                    }
                }
            }
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }
}

