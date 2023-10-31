package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytoolkotlin.NotaryToolError
import ca.ewert.notarytoolkotlin.response.Status
import ca.ewert.notarytoolkotlin.response.SubmissionId
import ca.ewert.notarytoolkotlin.response.SubmissionStatusResponse
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option

/**
 * Retrieve the status of a notarization submission.
 *
 * @author Victor Ewert
 */
abstract class SubmissionStatusTask : NotaryToolTask() {

  @get:Input
  @get:Option(
    option = "submissionId",
    description = "The identifier that you receive from the notary service when you post to Submit Software to start a new submission.",
  )
  abstract val submissionId: Property<String>

  init {
    this.description = "Retrieve the status of a notarization submission."
    submissionId.convention("")
  }

  override fun taskAction() {
    SubmissionId.of(submissionId.get()).mapEither({ submissionIdWrapper: SubmissionId ->
      logger.info("Valid submissionId: ${submissionIdWrapper.id}")
      retrieveStatus(submissionIdWrapper)
    }, { malformedSubmissionIdError ->
      logger.warn(malformedSubmissionIdError.longMsg)
    })
  }

  /**
   * Retrieves and logs the submission status.
   */
  private fun retrieveStatus(submissionIdWrapper: SubmissionId) {
    this.client.getSubmissionStatus(submissionIdWrapper)
      .mapEither({ submissionStatusResponse: SubmissionStatusResponse ->
        logger.quiet("Status for submission id ${submissionIdWrapper.id}: ${submissionStatusResponse.submissionInfo.status}")
        when (submissionStatusResponse.submissionInfo.status) {
          Status.ACCEPTED, Status.REJECTED, Status.INVALID -> retrieveSubmissionLogUrl(submissionIdWrapper)
          else -> logger.info("No log file")
        }
      }, { notaryToolError: NotaryToolError ->
        logger.warn(notaryToolError.longMsg)
      })
  }

  /**
   * Retrieves and logs the submission log (if available).
   */
  private fun retrieveSubmissionLogUrl(submissionIdWrapper: SubmissionId) {
    val logResult = this.client.getSubmissionLog(submissionIdWrapper)
    logResult.onSuccess { submissionLogUrlResponse ->
      logger.quiet("Submission Log: ${submissionLogUrlResponse.developerLogUrlString}")
    }

    logResult.onFailure { notaryToolError ->
      logger.info("Error getting log: ${notaryToolError.longMsg}")
    }
  }
}
