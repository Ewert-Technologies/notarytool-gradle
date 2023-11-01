package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytoolkotlin.NotaryToolError
import ca.ewert.notarytoolkotlin.response.Status
import ca.ewert.notarytoolkotlin.response.SubmissionId
import ca.ewert.notarytoolkotlin.response.SubmissionLogUrlResponse
import ca.ewert.notarytoolkotlin.response.SubmissionStatusResponse
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapEither
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option

/**
 * A Task that retrieves the status of a notarization submission.
 *
 * @author Victor Ewert
 */
abstract class SubmissionStatusTask : NotaryToolTask() {

  @get:Input
  @get:Option(
    option = "submissionId",
    description = "The identifier that you received from the notary service when you post to Submit Software to start a new submission.",
  )
  abstract val submissionId: Property<String>

  init {
    this.description = "Retrieves the status of a notarization submission."
    this.submissionId.convention("")
  }

  /**
   * Uses the submissionId argument to retrieve the submission status.
   */
  override fun taskAction() {
    SubmissionId.of(submissionId.get()).fold({ submissionIdWrapper: SubmissionId ->
      logger.info("Valid submissionId: ${submissionIdWrapper.id}")
      retrieveStatus(submissionIdWrapper)
    }, { malformedSubmissionIdError: NotaryToolError.UserInputError.MalformedSubmissionIdError ->
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
    this.client.getSubmissionLog(submissionIdWrapper).fold({ submissionLogUrlResponse: SubmissionLogUrlResponse ->
      logger.quiet("Submission Log: ${submissionLogUrlResponse.developerLogUrlString}")
    }, { notaryToolError: NotaryToolError ->
      logger.info("Error getting log: ${notaryToolError.longMsg}")
    })
  }
}
