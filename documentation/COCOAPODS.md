CocoaPods overview and setup﻿
Edit pageLast modified: 15 October 2025
This is a local integration method. It can work for you if:


You have a mono repository setup with an iOS project that uses CocoaPods.

Your Kotlin Multiplatform project has CocoaPods dependencies.

Choose the integration method that suits you best

Kotlin/Native provides integration with the CocoaPods dependency manager. You can add dependencies on Pod libraries as well as use a Kotlin project as a CocoaPods dependency.

You can manage Pod dependencies directly in IntelliJ IDEA or Android Studio and enjoy all the additional features such as code highlighting and completion. You can build an entire Kotlin project with Gradle without switching to Xcode.

You only need Xcode if you want to change Swift/Objective-C code or run your application on an Apple simulator or device. To work with Xcode, update your Podfile first.

Set up an environment to work with CocoaPods﻿
Install the CocoaPods dependency manager using the installation tool of your choice:

RVM
Rbenv
Default Ruby
Homebrew
Install RVM in case you don't have it yet.

Install Ruby. You can choose a specific version:

rvm install ruby 3.4.7
Install CocoaPods:

sudo gem install -n /usr/local/bin cocoapods
If you encounter problems during the installation, check the Possible issues and solutions section.

Create a project﻿
When your CocoaPods environment is set up, you can configure your Kotlin Multiplatform project to work with Pods. The following steps show the configuration on a freshly generated project:

Generate a new project for Android and iOS using the Kotlin Multiplatform IDE plugin (on macOS) or the Kotlin Multiplatform web wizard. If using the web wizard, unpack the archive and import the project in your IDE.

In the gradle/libs.versions.toml file, add the Kotlin CocoaPods Gradle plugin to the [plugins] block:

kotlinCocoapods = { id = "org.jetbrains.kotlin.native.cocoapods", version.ref = "kotlin" }
Navigate to the root build.gradle.kts file of your project and add the following alias to the plugins {} block:

alias(libs.plugins.kotlinCocoapods) apply false
Open the module where you want to integrate CocoaPods, for example the composeApp module, and add the following alias to the plugins {} block of the build.gradle.kts file:

alias(libs.plugins.kotlinCocoapods)
Now you are ready to configure CocoaPods in your Kotlin Multiplatform project.

Configure the project﻿
To configure the Kotlin CocoaPods Gradle plugin in your multiplatform project:

In the shared module's build.gradle(.kts) of your project, apply the CocoaPods plugin as well as the Kotlin Multiplatform plugin.

Skip this step if you've created your project with the IDE plugin or the web wizard.

plugins {
kotlin("multiplatform") version "2.2.21"
kotlin("native.cocoapods") version "2.2.21"
}
Configure version, summary, homepage, and baseName of the Podspec file in the cocoapods block:

plugins {
kotlin("multiplatform") version "2.2.21"
kotlin("native.cocoapods") version "2.2.21"
}

kotlin {
cocoapods {
// Required properties
// Specify the required Pod version here
// Otherwise, the Gradle project version is used
version = "1.0"
summary = "Some description for a Kotlin/Native module"
homepage = "Link to a Kotlin/Native module homepage"

        // Optional properties
        // Configure the Pod name here instead of changing the Gradle project name
        name = "MyCocoaPod"

        framework {
            // Required properties
            // Framework name configuration. Use this property instead of deprecated 'frameworkName'
            baseName = "MyFramework"

            // Optional properties
            // Specify the framework linking type. It's dynamic by default.
            isStatic = false
            // Dependency export
            // Uncomment and specify another project module if you have one:
            // export(project(":<your other KMP module>"))
            transitiveExport = false // This is default.
        }

        // Maps custom Xcode configuration to NativeBuildType
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }
}
See the full syntax of Kotlin DSL in the Kotlin Gradle plugin repository.

Run Build | Reload All Gradle Projects in IntelliJ IDEA (or File | Sync Project with Gradle Files in Android Studio) to re-import the project.

Generate the Gradle wrapper to avoid compatibility issues during an Xcode build.

When applied, the CocoaPods plugin does the following:

Adds both debug and release frameworks as output binaries for all macOS, iOS, tvOS, and watchOS targets.

Creates a podspec task which generates a Podspec file for the project.

The Podspec file includes a path to an output framework and script phases that automate building this framework during the build process of an Xcode project.

Update Podfile for Xcode﻿
If you want to import your Kotlin project to an Xcode project:

In the iOS part of your Kotlin project, make changes to the Podfile:

If your project has any Git, HTTP, or custom Podspec repository dependencies, specify the path to the Podspec in the Podfile.

For example, if you add a dependency on podspecWithFilesExample, declare the path to the Podspec in the Podfile:

target 'ios-app' do
# ... other dependencies ...
pod 'podspecWithFilesExample', :path => 'cocoapods/externalSources/url/podspecWithFilesExample'
end
The :path should contain the filepath to the Pod.

If you add a library from the custom Podspec repository, specify the location of specs at the beginning of your Podfile:

source 'https://github.com/Kotlin/kotlin-cocoapods-spec.git'

target 'kotlin-cocoapods-xcproj' do
# ... other dependencies ...
pod 'example'
end
Run pod install in your project directory.

When you run pod install for the first time, it creates the .xcworkspace file. This file includes your original .xcodeproj and the CocoaPods project.

Close your .xcodeproj and open the new .xcworkspace file instead. This way you avoid issues with project dependencies.

Run Build | Reload All Gradle Projects in IntelliJ IDEA (or File | Sync Project with Gradle Files in Android Studio) to re-import the project.

If you don't make these changes in the Podfile, the podInstall task will fail, and the CocoaPods plugin will show an error message in the log.