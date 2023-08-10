package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytoolkotlin.NotaryToolError
import ca.ewert.notarytoolkotlin.response.SubmissionId
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.time.Duration
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

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
    try {
      val softwareFilePath = Path.of(fileLocation.get())
      if (softwareFilePath.exists()) {
        startSubmission(softwareFile = softwareFilePath)
      } else {
        logger.warn("${softwareFilePath.absolutePathString()} does not exist.")
      }
    } catch (invalidPathException: InvalidPathException) {
      logger.warn("fileLocation is not valid: ${invalidPathException.localizedMessage}")
    }
  }

  private fun startSubmission(softwareFile: Path) {
    val submitAndUploadResult = this.client.submitAndUploadSoftware(softwareFile)
    submitAndUploadResult.onSuccess { awsUploadData ->
      val submissionId = awsUploadData.submissionId
      logger.lifecycle("Uploaded file for notarization. Submission ID: ${submissionId.id}")
    }

    submitAndUploadResult.onFailure { notaryToolError ->
      logger.warn(notaryToolError.longMsg)
    }
  }

  private fun pollStatus(submissionId: SubmissionId) {
    logger.quiet("Polling status for submission: ${submissionId.id}...")
    val maxPollCount = 50
    val result = this.client.pollSubmissionStatus(
      submissionId = submissionId,
      maxPollCount = maxPollCount,
      delayFunction = { _: Int -> Duration.ofSeconds(15) },
      progressCallback = { currentPollCount, submissionStatusResponse ->
        logger.quiet(
          "Checking submission status attempt $currentPollCount of $maxPollCount. Current status: ${submissionStatusResponse.submissionInfo.status}",
        )
      },
    )

    result.onSuccess { submissionStatusResponse ->
      logger.quiet("Status for submission id ${submissionId.id}: ${submissionStatusResponse.submissionInfo.status}")
    }

    result.onFailure { notaryToolError ->
      when (notaryToolError) {
        is NotaryToolError.PollingTimeout -> logger.warn("Polling timed out. Use 'submissionStatus' task to manually check the status.")
        else -> logger.warn(notaryToolError.longMsg)
      }
    }
  }
}
