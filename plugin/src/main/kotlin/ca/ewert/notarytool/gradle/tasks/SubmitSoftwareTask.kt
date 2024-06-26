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
 * Name of the `fileLocation` command-line option
 */
private const val FILE_LOCATION_NAME: String = "fileLocation"

/**
 * Description of the `fileLocation` command-line option
 */
private const val FILE_LOCATION_DESCRIPTION: String = "Location (path) of the file to be notarized."

/**
 * Name of the `poll` command-line option
 */
private const val POLL_OPTION_NAME: String = "poll"

/**
 * Description of the `poll` command-line option
 */
private const val POLL_OPTION_DESCRIPTION: String =
  "Repeatedly poll for submission status until Accepted, Rejected or Invalid."

/**
 * Task for submitting software to be notarized.
 *
 * @author Victor Ewert
 */
abstract class SubmitSoftwareTask : NotaryToolTask() {
  /**
   * File Location property.
   */
  @get:Input
  @get:Option(
    option = FILE_LOCATION_NAME,
    description = FILE_LOCATION_DESCRIPTION,
  )
  abstract val fileLocation: Property<String>

  @get:Input
  @get:Option(
    option = POLL_OPTION_NAME,
    description = POLL_OPTION_DESCRIPTION,
  )
  abstract val poll: Property<Boolean>

  /**
   * Polling flag.
   */
  init {
    logger.info("Inside init ${this.name} task")
    this.description = "Submits software to be notarized."
    fileLocation.convention("")
    poll.convention(true)
  }

  /**
   * Method called when Task is run. Submits software to be notarized.
   */
  override fun taskAction() {
    logger.lifecycle("Starting task: ${this.name}")
    logger.info("User-Agent: ${this.client.userAgent}")
    logger.info("'fileLocation' value: ${fileLocation.get()}")
    logger.info(("'poll' value: ${poll.get()}"))
    if (fileLocation.get().isBlank()) {
      logger.error(
        "No argument was provided for command-line option '--$FILE_LOCATION_NAME' with description: '$FILE_LOCATION_DESCRIPTION'",
      )
    } else {
      try {
        val softwareFilePath: Path = Path.of(fileLocation.get())
        logger.info("Path of file to be submitted: ${softwareFilePath.absolutePathString()}")
        if (softwareFilePath.exists()) {
          if (softwareFilePath.isRegularFile()) {
            startSubmission(softwareFile = softwareFilePath)
          } else {
            logger.error("${softwareFilePath.absolutePathString()} is not a file.")
          }
        } else {
          logger.error("${softwareFilePath.absolutePathString()} does not exist.")
        }
      } catch (invalidPathException: InvalidPathException) {
        logger.error("'fileLocation' is not valid: ${invalidPathException.localizedMessage}")
      } catch (illegalStateException: IllegalStateException) {
        logger.error("Invalid value for 'softwareFilepath': ${illegalStateException.localizedMessage}")
      }
    }
  }

  /**
   * Starts the submission process by uploading the file to the AWS Servers and then uses
   * the returned submissionId to poll for a status Result.
   */
  private fun startSubmission(softwareFile: Path) {
    logger.quiet("Submitting file: ${softwareFile.fileName}, please wait ...")
    this.client.submitAndUploadSoftware(softwareFile).fold({ awsUploadData: AwsUploadData ->
      val submissionId: SubmissionId = awsUploadData.submissionId
      logger.lifecycle("Uploaded file for notarization. Submission ID: ${submissionId.id}")
      if (poll.get()) {
        pollForStatus(submissionId)
      } else {
        logger.lifecycle("Check the submission status using: './gradlew submissionStatus --submissionId ${submissionId.id}'")
      }
    }, { notaryToolError: NotaryToolError ->
      logger.error(notaryToolError.longMsg)
    })
  }

  /**
   * Polls the status, by checking the status every 15 seconds until the status is either
   * Accepted or Invalid. Check for a maximum of 50 times.
   */
  private fun pollForStatus(submissionId: SubmissionId) {
    logger.quiet("Polling status, for submission: ${submissionId.id}...")
    val maxPollCount = 50
    val result: Result<SubmissionStatusResponse, NotaryToolError> =
      this.client.pollSubmissionStatus(
        submissionId = submissionId,
        maxPollCount = maxPollCount,
        delayFunction = { _: Int -> Duration.ofSeconds(15) },
        progressCallback = { currentPollCount, submissionStatusResponse ->
          logger.quiet(
            "Checking submission status, attempt $currentPollCount of $maxPollCount. " +
              "Current status: ${submissionStatusResponse.submissionInfo.status}",
          )
        },
      )

    result.fold({ submissionStatusResponse: SubmissionStatusResponse ->
      logger.quiet("Final status for submission id ${submissionId.id}: ${submissionStatusResponse.submissionInfo.status}")
      when (submissionStatusResponse.submissionInfo.status) {
        Status.ACCEPTED, Status.REJECTED, Status.INVALID -> retrieveSubmissionLogUrl(submissionId)
        else -> logger.info("No log file")
      }
    }, { notaryToolError: NotaryToolError ->
      when (notaryToolError) {
        is NotaryToolError.PollingTimeout ->
          logger.warn(
            "Polling timed out. Use 'submissionStatus' task to manually check the status for t submission with id: ${submissionId.id}.",
          )

        else -> logger.error(notaryToolError.longMsg)
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
      logger.warn("Error getting log: ${notaryToolError.longMsg}")
    })
  }
}
