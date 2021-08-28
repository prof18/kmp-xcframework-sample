import java.util.*
import java.text.SimpleDateFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework


plugins {
    kotlin("multiplatform") version "1.5.30"
    id("com.android.library")
}

val libName = "LibraryName"
val libVersionName = "1.0.2"
group = "com.prof18"
version = libVersionName

repositories {
    google()
    mavenCentral()
}

kotlin {
    val xcFramework = XCFramework(libName)

    android()
    ios {
        binaries.framework(libName) {
            xcFramework.add(this)
        }
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

        register("publishDevFramework") {
            description = "Publish iOs framework to the Cocoa Repo"

            project.exec {
                workingDir = File("$rootDir/../kmp-xcframework-dest")
                commandLine("git", "checkout", "develop").standardOutput
            }

            dependsOn("assemble${libName}DebugXCFramework")

            doLast {

                copy {
                    from("$buildDir/XCFrameworks/debug")
                    into("$rootDir/../kmp-xcframework-dest")
                }

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
            dependsOn("assemble${libName}ReleaseXCFramework")

            // Replace
            doLast {

                copy {
                    from("$buildDir/XCFrameworks/release")
                    into("$rootDir/../kmp-xcframework-dest")
                }

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

