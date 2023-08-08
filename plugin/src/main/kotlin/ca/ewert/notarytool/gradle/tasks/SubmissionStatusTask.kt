package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytoolkotlin.response.Status
import ca.ewert.notarytoolkotlin.response.SubmissionId
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option

/**
 * Retrieve the status of a notarization submission.
 *
 * @author Victor Ewert
 */
abstract class SubmissionStatusTask : NotaryToolTask() {

  @get:Input
  @Option(
    option = "submissionId",
    description = "The identifier that you receive from the notary service when you post to Submit Software to start a new submission.",
  )
  var submissionIdString: String = ""

  init {
    this.description = "Retrieve the status of a notarization submission."
  }

  override fun taskAction() {
    val submissionIdResult = SubmissionId.of(submissionIdString)
    submissionIdResult.onSuccess { submissionId: SubmissionId ->
      logger.info("Valid submissionId: ${submissionId.id}")
      retrieveStatus(submissionId)
    }

    submissionIdResult.onFailure { malformedSubmissionIdError ->
      logger.warn(malformedSubmissionIdError.longMsg)
    }
  }

  /**
   * Retrieves and logs the submission status.
   */
  private fun retrieveStatus(submissionId: SubmissionId) {
    val statusResult = this.client.getSubmissionStatus(submissionId)

    statusResult.onSuccess { submissionStatusResponse ->
      logger.quiet("Status for submission id ${submissionId.id}: ${submissionStatusResponse.submissionInfo.status}")
      when (submissionStatusResponse.submissionInfo.status) {
        Status.ACCEPTED, Status.REJECTED, Status.INVALID -> retrieveSubmissionLogUrl(submissionId)
        else -> logger.info("No log file")
      }
    }

    statusResult.onFailure { notaryToolError ->
      logger.warn(notaryToolError.longMsg)
    }
  }

  /**
   * Retries and logs the submission log (if available).
   */
  private fun retrieveSubmissionLogUrl(submissionId: SubmissionId) {
    val logResult = this.client.getSubmissionLog(submissionId)
    logResult.onSuccess { submissionLogUrlResponse ->
      logger.quiet("Submission Log: ${submissionLogUrlResponse.developerLogUrlString}")
    }

    logResult.onFailure { notaryToolError ->
      logger.info("Error getting log: ${notaryToolError.longMsg}")
    }
  }
}
