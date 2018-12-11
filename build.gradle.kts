import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.novoda.gradle.release.PublishExtension

buildscript {
	repositories {
		mavenCentral()
		jcenter()
	}
	dependencies {
		classpath("org.jetbrains.dokka:dokka-gradle-plugin")
		classpath("com.novoda:bintray-release:+")
	}
}

plugins {
	kotlin("jvm") version "1.3.11"
	id("org.jetbrains.dokka") version "0.9.16"
}

apply {
	plugin("com.novoda.bintray-release")
}

group = "org.mechdancer"
version = "0.1.8"

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

configure<PublishExtension> {
	userOrg = "mechdancer"
	groupId = "org.mechdancer"
	artifactId = "consoleparser"
	publishVersion = version.toString()
	desc = "an idiot lexer"
	website = "https://github.com/MechDancer/consoleparsere"
}
