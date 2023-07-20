import ca.ewert.notarytool.gradle.extensions.GreetingExtension
import ca.ewert.notarytool.gradle.tasks.HelloTask

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.9.0"
  id("ca.ewert.notarytool.gradle")
}

repositories {
  mavenCentral()
}

//tasks.register<HelloTask>("hello")

configure<GreetingExtension> {
  message.set("Hello Gradle Plugin")
  name.set("Victor")
}

tasks.helloTask {
  middleName = "george"
  lastName.set("ewert")
}

tasks.register<HelloTask>("hello") {
  lastName.set("Ewert")
}
