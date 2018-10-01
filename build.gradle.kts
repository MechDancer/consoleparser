import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    dependencies {
        classpath("com.novoda:bintray-release:0.8.1")
    }
}

plugins {
    kotlin("jvm") version "1.2.71"
}

apply {
    plugin("com.novoda.bintray-release")
}
group = "org.mechdancer"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configure<PublishExtension> {
    userOrg = 'mechdancer'
    groupId = 'org.mechdancer'
    artifactId = 'consoleparser'
    publishVersion = '0.1.0'
    desc = 'cli parser'
    website = 'https://github.com/MechDancer/consoleparser'
}
