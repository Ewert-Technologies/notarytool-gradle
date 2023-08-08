package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytool.gradle.extensions.NotaryToolGradlePluginExtension
import ca.ewert.notarytoolkotlin.NotaryToolClient
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Parent type for Notary Tool tasks.
 *
 * @author Victor Ewert
 */
abstract class NotaryToolTask : DefaultTask() {

  /** Client used to make calls to the notary api */
  @Internal
  protected val client: NotaryToolClient

  init {
    logger.info("Inside init parent")
    this.group = "notarytool"
    val pluginExtension: NotaryToolGradlePluginExtension =
      project.extensions.getByType(NotaryToolGradlePluginExtension::class.java)
    client = NotaryToolClient(
      issuerId = pluginExtension.issuerId.get(),
      privateKeyId = pluginExtension.privateKeyId.get(),
      privateKeyFile = pluginExtension.privateKeyFile.get(),
      userAgent = "${project.name}/${project.version}",
    )
  }

  /**
   * Function run when the task in executed.
   */
  @TaskAction
  abstract fun taskAction()
}
