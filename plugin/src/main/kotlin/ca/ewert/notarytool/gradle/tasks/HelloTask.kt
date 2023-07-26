package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytool.gradle.extensions.GreetingExtension
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

/**
 * TODO: Add Documentation
 *
 * @author Victor Ewert
 */
abstract class HelloTask() : DefaultTask() {

  @get:Input
  @Option(option = "middleName", description = "Your middle name")
  var middleName: String = "n/a"

  @get:Input
  abstract val lastName: Property<String>

  init {
    this.group = "notarytool"
    lastName.convention("N/A")
  }

  @TaskAction
  fun printHello() {
    val extension: GreetingExtension = project.extensions.getByType(GreetingExtension::class.java)
    logger.quiet("Hello: ${extension.name.get()} $middleName ${lastName.get()}")
  }
}
