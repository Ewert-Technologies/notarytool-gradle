package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytoolkotlin.NotaryToolError
import ca.ewert.notarytoolkotlin.response.SubmissionInfo
import ca.ewert.notarytoolkotlin.response.SubmissionListResponse
import com.github.michaelbull.result.fold
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
   * Method called when Task is run. Retrieves the list of previous submissions,
   * and displays a summary of each submission via the logger.
   */
  @TaskAction
  override fun taskAction() {
    logger.lifecycle("Starting task: ${this.name}")
    logger.info("User-Agent: ${this.client.userAgent}")

    val formatPattern: String = "%-40s %-15s %-35s %s"

    this.client.getPreviousSubmissions().fold({ submissionListResponse: SubmissionListResponse ->
      logger.quiet("Submission History (last 100 submission):\n")
      logger.quiet(formatPattern.format("Submission ID", "Status", "Upload Date", "Uploaded File Name"))
      submissionListResponse.submissionInfoList.sortedBy { it.createdDate }
        .forEach { submissionInfo: SubmissionInfo ->
          val uploadedDate: Instant? = submissionInfo.createdDate
          val uploadedDateString: String =
            if (uploadedDate != null) {
              uploadedDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.LONG))
            } else {
              submissionInfo.createdDateText
            }
//        logger.quiet("'${submissionInfo.id}'\t'${submissionInfo.name}'\t'${submissionInfo.status}'\t'$uploadedDateString'")
          logger.quiet(
            formatPattern.format(
              submissionInfo.id,
              submissionInfo.status,
              uploadedDateString,
              submissionInfo.name,
            )
          )
        }
    }, { notaryToolError: NotaryToolError ->
      logger.error(notaryToolError.longMsg)
    })
  }
}
