pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "1.9.22" apply false
        kotlin("plugin.spring") version "1.9.22" apply false
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "faculty_day"
include("lesson2", "lesson7", "lesson8", "lesson10", "untitled1", "demo")
//include("lesson3", "lesson1", "fd-hibernate-jpa-student")