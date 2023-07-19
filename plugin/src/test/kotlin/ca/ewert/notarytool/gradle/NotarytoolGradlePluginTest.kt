/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package ca.ewert.notarytool.gradle

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * A simple unit test for the 'ca.ewert.notarytool.gradle.greeting' plugin.
 */
class NotarytoolGradlePluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("ca.ewert.notarytool.gradle.greeting")

        // Verify the result
        assertNotNull(project.tasks.findByName("greetingTask"))
    }
}
