package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytool.gradle.TASK_GROUP_NAME
import ca.ewert.notarytool.gradle.extensions.NotaryToolGradlePluginExtension
import ca.ewert.notarytoolkotlin.NotaryToolClient
import org.gradle.api.DefaultTask
import org.gradle.api.internal.provider.MissingValueException
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
    logger.lifecycle("Inside init parent")
    this.group = TASK_GROUP_NAME
    val pluginExtension: NotaryToolGradlePluginExtension =
      project.extensions.getByType(NotaryToolGradlePluginExtension::class.java)

    try {
      client =
        NotaryToolClient(
          issuerId = pluginExtension.issuerId.get(),
          privateKeyId = pluginExtension.privateKeyId.get(),
          privateKeyFile = pluginExtension.privateKeyFile.get(),
          userAgent = "${project.name}/${project.version}",
        )
    } catch (mse: MissingValueException) {
      logger.error("notarytool plugin is missing Notary Credentials are missing\n" +
        "Please make sure the issuerId, privateKeyId and privateKey file values have been set in the configuration block.")
      throw mse
    }
  }

  /**
   * Function run when the task in executed.
   */
  @TaskAction
  abstract fun taskAction()
}
