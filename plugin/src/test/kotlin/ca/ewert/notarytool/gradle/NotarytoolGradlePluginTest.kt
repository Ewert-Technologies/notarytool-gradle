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
    project.plugins.apply("ca.ewert.notarytoolgradle")
//    println("Hello Test")
//
//    // Verify the result
//    val submissionHistoryTask: Task? = project.tasks.findByName("submissionHistory")
//
//    assertThat(submissionHistoryTask).isNotNull()
//    assertThat(submissionHistoryTask?.name).isEqualTo("submissionHistory")
//    assertThat(submissionHistoryTask?.group).isEqualTo("notarytool")
//    assertThat(submissionHistoryTask?.description).isNotNull()
  }
}
