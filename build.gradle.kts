//import org.web3j.solidity.gradle.plugin.EVMVersion

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
//    id("org.web3j") version "4.12.2"
}
//web3j {
//    generatedPackageName = "com.copsiitbhu.web3japp"
//}
////
//solidity {
//    evmVersion = EVMVersion.ISTANBUL
//}
//
//node {
//    nodeProjectDir.set(file("$projectDir"))
//}

repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}
