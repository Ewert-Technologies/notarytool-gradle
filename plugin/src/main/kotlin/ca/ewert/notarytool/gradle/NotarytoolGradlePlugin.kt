/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package ca.ewert.notarytool.gradle

import ca.ewert.notarytool.gradle.extensions.GreetingExtension
import ca.ewert.notarytool.gradle.extensions.NotaryToolGradlePluginExtension
import ca.ewert.notarytool.gradle.tasks.HelloTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A Gradle Plugin used to interact with the Apple's Notarytool Web API
 */
class NotarytoolGradlePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val notarytoolGradlePluginExtension =
      project.extensions.create("notarytool-gradle-extension", NotaryToolGradlePluginExtension::class.java)

    notarytoolGradlePluginExtension.issuerId.convention("")
    notarytoolGradlePluginExtension.privateKeyId.convention("")

    val greetingExtension = project.extensions.create("greeting", GreetingExtension::class.java)
    greetingExtension.message.convention("N/A")
    project.tasks.register("greetingTask") { task ->
      task.group = "notarytool"
      task.description = "Greet task, used only for hacking around"
      task.doLast {
        println("Hello from plugin 'ca.ewert.notarytool.gradle.greeting', message: ${greetingExtension.message.get()}")
      }
    }

    project.tasks.register("helloTask", HelloTask::class.java)
  }
}
