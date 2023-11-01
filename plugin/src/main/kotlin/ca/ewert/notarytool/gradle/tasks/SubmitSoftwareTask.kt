package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytoolkotlin.NotaryToolError
import ca.ewert.notarytoolkotlin.response.AwsUploadData
import ca.ewert.notarytoolkotlin.response.Status
import ca.ewert.notarytoolkotlin.response.SubmissionId
import ca.ewert.notarytoolkotlin.response.SubmissionLogUrlResponse
import ca.ewert.notarytoolkotlin.response.SubmissionStatusResponse
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.time.Duration
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

/**
 * Task for submitting software to be notarized.
 *
 * @author Victor Ewert
 */
abstract class SubmitSoftwareTask : NotaryToolTask() {
  @get:Input
  @get:Option(
    option = "fileLocation",
    description = "Location (path) of the file to be notarized.",
  )
  abstract val fileLocation: Property<String>

  init {
    this.description = "Submits software to be notarized."
    fileLocation.convention("")
  }

  /**
   * Function run when the task in executed.
   */
  override fun taskAction() {
    logger.lifecycle("Starting task: ${this.name}")
    logger.debug("fileLocation: ${fileLocation.get()}")
    try {
      val softwareFilePath: Path = Path.of(fileLocation.get())
      logger.debug("softwareFilePath: ${softwareFilePath.absolutePathString()}")
      if (softwareFilePath.exists()) {
        if (softwareFilePath.isRegularFile()) {
          startSubmission(softwareFile = softwareFilePath)
        } else {
          logger.warn("${softwareFilePath.absolutePathString()} is not a file.")
        }
      } else {
        logger.warn("${softwareFilePath.absolutePathString()} does not exist.")
      }
    } catch (invalidPathException: InvalidPathException) {
      logger.warn("fileLocation is not valid: ${invalidPathException.localizedMessage}")
    } catch (illegalStateException: IllegalStateException) {
      logger.warn("Invalid value for 'softwareFilepath'")
    }
  }

  /**
   * Starts the submission process by uploading the file to the AWS Servers and then uses
   * the returned submissionId to poll for a status Result.
   */
  private fun startSubmission(softwareFile: Path) {
    logger.quiet("Submitting file: ${softwareFile.fileName} ...")
    this.client.submitAndUploadSoftware(softwareFile).fold({ awsUploadData: AwsUploadData ->
      val submissionId: SubmissionId = awsUploadData.submissionId
      logger.lifecycle("Uploaded file for notarization. Submission ID: ${submissionId.id}")
      pollStatus(submissionId)
    }, { notaryToolError: NotaryToolError ->
      logger.warn(notaryToolError.longMsg)
    })
  }

  private fun pollStatus(submissionId: SubmissionId) {
    logger.quiet("Polling status for submission: ${submissionId.id}...")
    val maxPollCount = 50
    val result: Result<SubmissionStatusResponse, NotaryToolError> =
      this.client.pollSubmissionStatus(
        submissionId = submissionId,
        maxPollCount = maxPollCount,
        delayFunction = { _: Int -> Duration.ofSeconds(15) },
        progressCallback = { currentPollCount, submissionStatusResponse ->
          logger.quiet(
            "Checking submission status, attempt $currentPollCount of $maxPollCount. Current status: ${submissionStatusResponse.submissionInfo.status}",
          )
        },
      )

    result.fold({ submissionStatusResponse: SubmissionStatusResponse ->
      logger.quiet("Status for submission id ${submissionId.id}: ${submissionStatusResponse.submissionInfo.status}")
      when (submissionStatusResponse.submissionInfo.status) {
        Status.ACCEPTED, Status.REJECTED, Status.INVALID -> retrieveSubmissionLogUrl(submissionId)
        else -> logger.info("No log file")
      }
    }, { notaryToolError: NotaryToolError ->
      when (notaryToolError) {
        is NotaryToolError.PollingTimeout ->
          logger.warn(
            "Polling timed out. Use 'submissionStatus' task to manually check the status for submission with id: ${submissionId.id}.",
          )
        else -> logger.warn(notaryToolError.longMsg)
      }
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
