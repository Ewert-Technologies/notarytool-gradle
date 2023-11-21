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
 * Name of the `submissionId` command-line option
 */
private const val SUBMISSION_ID_NAME: String = "submissionId"

/**
 * Description of the `submissionId` command-line option
 */
private const val SUBMISSION_ID_DESCRIPTION: String = "The identifier that you received after submitting the software."

/**
 * Retrieves the status of an individual notarization submission.
 *
 * @author Victor Ewert
 */
abstract class SubmissionStatusTask : NotaryToolTask() {
  @get:Input
  @get:Option(
    option = SUBMISSION_ID_NAME,
    description = SUBMISSION_ID_DESCRIPTION,
  )
  abstract val submissionId: Property<String>

  init {
    logger.lifecycle("Inside init ${this.name} task")
    this.description = "Retrieves the status of a notarization submission."
    this.submissionId.convention("")
  }

  /**
   * Method called when Task is run. Uses the submissionId argument to retrieve the submission status.
   */
  override fun taskAction() {
    logger.lifecycle("Starting task: ${this.name}")
    logger.info("User-Agent: ${this.client.userAgent}")
    logger.info("'submissionId' parameter value: ${submissionId.get()}")

    if (submissionId.get().isBlank()) {
      logger.error(
        "No argument was provided for command-line option '--$SUBMISSION_ID_NAME' with description: '$SUBMISSION_ID_DESCRIPTION'",
      )
    } else {
      SubmissionId.of(submissionId.get()).fold({ submissionIdWrapper: SubmissionId ->
        logger.info("Valid submissionId: ${submissionIdWrapper.id}")
        retrieveStatus(submissionIdWrapper)
      }, { malformedSubmissionIdError: NotaryToolError.UserInputError.MalformedSubmissionIdError ->
        logger.error(malformedSubmissionIdError.longMsg)
      })
    }
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
        logger.error(notaryToolError.longMsg)
      })
  }

  /**
   * Retrieves and logs the submission log (if available).
   */
  private fun retrieveSubmissionLogUrl(submissionIdWrapper: SubmissionId) {
    this.client.getSubmissionLog(submissionIdWrapper).fold({ submissionLogUrlResponse: SubmissionLogUrlResponse ->
      logger.quiet("Submission Log: ${submissionLogUrlResponse.developerLogUrlString}")
    }, { notaryToolError: NotaryToolError ->
      logger.warn("Error getting log: ${notaryToolError.longMsg}")
    })
  }
}
