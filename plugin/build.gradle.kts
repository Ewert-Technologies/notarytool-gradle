/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Gradle plugin project to get you started.
 * For more details take a look at the Writing Custom Plugins chapter in the Gradle
 * User Manual available at https://docs.gradle.org/8.1.1/userguide/custom_plugins.html
 * This project uses @Incubating APIs which are subject to change.
 */

@file:Suppress("UnstableApiUsage")


//
// Plugins
//
plugins {
  `java-gradle-plugin`
  `maven-publish`
  id("org.jetbrains.kotlin.jvm") version "1.9.20"
  id("com.github.ben-manes.versions") version "0.49.0"
  id("org.jmailen.kotlinter") version "4.0.0"
}

//
// Repositories for Plugin Dependencies
//
repositories {
  mavenCentral()
  mavenLocal()
}

//
// Plugin Dependencies
//
dependencies {
  implementation(group = "ca.ewert-technologies.notarytoolkotlin", name = "notarytool-kotlin", "0.0.3")

  // Testing
  testImplementation(gradleTestKit())
  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.10.0")
  testImplementation(group = "com.willowtreeapps.assertk", name = "assertk", version = "0.27.0")
}

//
// Apply a specific Java toolchain to ease working on different environments.
//
java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
    vendor.set(JvmVendorSpec.ADOPTIUM)
  }
}

kotlin {
  jvmToolchain {
    this.languageVersion.set(JavaLanguageVersion.of(11))
  }
}

//
// Set up various Properties and Constants used by the build scr
//

// Application Properties
val longName: String by project
val author: String by project
val projectUrl: String by project
val authorEmail: String by project
val company: String by project
val companyUrl: String by project
val group: String by project
val copyrightYear: String by project

//
// Set up plugin metadata
//
gradlePlugin {

  website.set("https://www.ewert-technologies.ca")
  vcsUrl.set("https://www.ewert-technologies.ca")

  //
  // Define the plugin
  //
  plugins {
    create(project.name) {
      id = project.group.toString()
      displayName = longName
      description = project.description
      tags.set(listOf("deployment", "notarytool", "apple", "macOS"))
      implementationClass = "ca.ewert.notarytool.gradle.NotarytoolGradlePlugin"
    }
  }
}

/**
 * Displays general build info, such as versions, key directory locations, etc.
 */
tasks.register("buildInfo") {
  group = "help"
  description = "Displays general build info, such as versions, etc."

  logger.quiet("Project: ${project.name} - ${project.description}")
  logger.quiet("Project version: ${project.version}")
  logger.quiet("Group:  ${project.group}")
//  logger.quiet("Author: $author")
//  logger.quiet("Company: $company")
  logger.quiet("Gradle Version: ${gradle.gradleVersion}")
//  logger.quiet("Java Toolchain: Version ${java.toolchain.languageVersion.get()} (${java.toolchain.vendor.get()})")
  logger.quiet("build dir: ${project.layout.buildDirectory.get()}")
}

//
// Configure Testing
//
tasks.named<Test>("test") {
  useJUnitPlatform()
}

testing {
  suites {
    // Configure the built-in test suite
    val test by getting(JvmTestSuite::class) {
      // Use Kotlin Test test framework
//      useJUnitPlatform()
    }

    // Create a new test suite
    val functionalTest by registering(JvmTestSuite::class) {
      // Use Kotlin Test test framework
//      useJUnitPlatform()

      dependencies {
        // functionalTest test suite depends on the production code in tests
        implementation(project())
        implementation("com.willowtreeapps.assertk:assertk:0.26.1")
      }

      targets {
        all {
          // This test suite should run after the built-in test suite has run its tests
          testTask.configure { shouldRunAfter(test) }
        }
      }
    }
  }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

tasks.named<Task>("check") {
  // Include functionalTest as part of the check lifecycle
  dependsOn(testing.suites.named("functionalTest"))
}
