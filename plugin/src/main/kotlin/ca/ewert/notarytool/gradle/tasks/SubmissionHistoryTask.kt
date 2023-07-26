package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytool.gradle.extensions.NotaryToolGradlePluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import kotlin.io.path.readText

/**
 * TODO: Add Documentation
 *
 * @author Victor Ewert
 */
abstract class SubmissionHistoryTask : DefaultTask() {
  init {
    this.group = "notarytool"
    this.description = "Retrieves a list of previous notarization submissions."
  }

  @TaskAction
  fun retrieveSubmissionHistory() {
    val pluginExtension: NotaryToolGradlePluginExtension =
      project.extensions.getByType(NotaryToolGradlePluginExtension::class.java)
    logger.quiet("Starting task: ${this.name}")
    logger.quiet("Private Key id: ${pluginExtension.privateKeyId.get()}")
    logger.quiet("Issuer id: ${pluginExtension.issuerId.get()}")
    logger.quiet("Private Key File: ${pluginExtension.privateKeyFile.get()}")
    val privateKeyFile: Path = (pluginExtension.privateKeyFile.get())
    logger.quiet("file location: $privateKeyFile")
    logger.quiet("file contents: \n${privateKeyFile.readText(charset = Charsets.UTF_8)}")
  }
}
