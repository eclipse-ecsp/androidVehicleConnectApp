plugins {
    id("org.cyclonedx.bom") version ("1.9.0")
    id("com.google.gms.google-services") version "4.4.2" apply false
}

subprojects {
    apply(plugin = "org.cyclonedx.bom")
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("org.cyclonedx:cyclonedx-gradle-plugin:1.9.0")
    }
}

group = "org.eclipse.ecsp"
version = 1.0
project.allprojects {
    tasks.cyclonedxBom {
        outputs.cacheIf { true }
        setIncludeConfigs(listOf("debugCompileClasspath"))
        setSkipConfigs(
            listOf(
                "debugAndroidTestCompileClasspath",
                "debugUnitTestCompileClasspath",
                "releaseUnitTestCompileClasspath",
                "debugUnitTestRuntimeClasspath",
                "releaseUnitTestRuntimeClasspath",
                "androidVehicleConnectSDK:debugApiElements",
                "app:debugApiElements",
            ),
        )
        setProjectType("application")
        outputFormat = "xml"
    }
}
