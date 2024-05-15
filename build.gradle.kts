plugins {
  id("org.jetbrains.kotlin.jvm") version "1.9.24"
  id("org.jetbrains.dokka") version "1.9.20"
  id("org.jmailen.kotlinter") version "4.0.0" apply false
}

subprojects {
  apply(plugin = "org.jetbrains.dokka")
}

repositories {
  mavenCentral()
}
