/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package ca.ewert.notarytool.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

/**
 * A simple unit test for the 'ca.ewert.notarytool.gradle.greeting' plugin.
 */
class NotarytoolGradlePluginTest {
  @Test
  fun pluginRegistersTask() {
    // Create a test project and apply the plugin
    val project = ProjectBuilder.builder().build()
    project.plugins.apply("ca.ewert-technologies.notarytoolgradle")
  }
}
