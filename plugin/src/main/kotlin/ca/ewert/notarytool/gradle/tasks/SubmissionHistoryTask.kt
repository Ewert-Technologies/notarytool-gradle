package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytoolkotlin.NotaryToolError
import ca.ewert.notarytoolkotlin.response.SubmissionInfo
import ca.ewert.notarytoolkotlin.response.SubmissionListResponse
import com.github.michaelbull.result.mapEither
import org.gradle.api.tasks.TaskAction
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * A Task that Retrieves a list of previous notarization submissions.
 *
 * @author Victor Ewert
 */
abstract class SubmissionHistoryTask : NotaryToolTask() {
  init {
    this.description = "Retrieves a list of previous notarization submissions."
  }

  /**
   * Retrieves the list of previous submissions, and displays a summary of each
   * submission via the logger.
   */
  @TaskAction
  override fun taskAction() {
    logger.lifecycle("Starting task: ${this.name}")
    logger.lifecycle("User-Agent: ${this.client.userAgent}")

    this.client.getPreviousSubmissions().mapEither({ submissionListResponse: SubmissionListResponse ->
      logger.quiet("Submission History (last 100 submission):")
      submissionListResponse.submissionInfoList.forEach { submissionInfo: SubmissionInfo ->
        val createdDate: Instant? = submissionInfo.createdDate
        val createdDateString: String = if (createdDate != null) {
          createdDate.atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.LONG))
        } else {
          submissionInfo.createdDateText
        }
        logger.quiet("'${submissionInfo.id}'\t'${submissionInfo.name}'\t'${submissionInfo.status}'\t'$createdDateString'")
      }
    }, { notaryToolError: NotaryToolError ->
      logger.warn(notaryToolError.longMsg)
    })
  }
}
