import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	repositories {
		mavenCentral()
		jcenter()
	}
	dependencies {
		classpath("org.jetbrains.dokka:dokka-gradle-plugin")
	}
}

plugins {
	kotlin("jvm") version "1.2.71"
	id("org.jetbrains.dokka") version "0.9.16"
}

group = "org.mechdancer"
version = "0.1.7-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	compile(kotlin("stdlib-jdk8"))
	testCompile("junit", "junit", "+")
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}
