package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytool.gradle.extensions.NotaryToolGradlePluginExtension
import ca.ewert.notarytoolkotlin.NotaryToolClient
import ca.ewert.notarytoolkotlin.response.SubmissionId
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

/**
 * Retrieve the status of a notarization submission.
 *
 * @author Victor Ewert
 */
abstract class SubmissionStatusTask : DefaultTask() {

  @get:Input
  @Option(
    option = "submissionId",
    description = "The identifier that you receive from the notary service when you post to Submit Software to start a new submission."
  )
  var submissionId: String = ""

  init {
    this.group = "notarytool"
    this.description = "Retrieve the status of a notarization submission."
  }

  @TaskAction
  fun retrieveSubmissionStatus() {
    val pluginExtension: NotaryToolGradlePluginExtension =
      project.extensions.getByType(NotaryToolGradlePluginExtension::class.java)
    logger.lifecycle("Starting task: ${this.name}")

    val client = NotaryToolClient(
      issuerId = pluginExtension.issuerId.get(),
      privateKeyId = pluginExtension.privateKeyId.get(),
      privateKeyFile = pluginExtension.privateKeyFile.get(),
      userAgent = "${project.name}/${project.version}",
    )

    val submissionIdResult = SubmissionId.of(submissionId)
    submissionIdResult.onSuccess { submissionId: SubmissionId ->
      logger.lifecycle("Valid submissionId: ${submissionId.id}")
      retrieveStatus(client, submissionId)
    }

    submissionIdResult.onFailure { malformedSubmissionIdError ->
      logger.warn(malformedSubmissionIdError.longMsg)
    }
  }

  private fun retrieveStatus(client: NotaryToolClient, submissionId: SubmissionId) {
    val statusResult = client.getSubmissionStatus(submissionId)

    statusResult.onSuccess { submissionStatusResponse ->
      logger.quiet("Status for submission id ${submissionId.id}: ${submissionStatusResponse.submissionInfo.status}")
    }

    statusResult.onFailure { notaryToolError ->
      logger.warn(notaryToolError.longMsg)
    }
  }
}
