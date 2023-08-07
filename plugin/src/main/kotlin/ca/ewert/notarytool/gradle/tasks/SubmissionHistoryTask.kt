package ca.ewert.notarytool.gradle.tasks

import ca.ewert.notarytool.gradle.extensions.NotaryToolGradlePluginExtension
import ca.ewert.notarytoolkotlin.NotaryToolClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * A Task that Retrieves a list of previous notarization submissions.
 *
 * @author Victor Ewert
 */
abstract class SubmissionHistoryTask : DefaultTask() {
  init {
    this.group = "notarytool"
    this.description = "Retrieves a list of previous notarization submissions."
  }

  /**
   * Retrieves the list of previous submissions, and displays a summary of each
   * submission via the logger.
   */
  @TaskAction
  fun retrieveSubmissionHistory() {
    val pluginExtension: NotaryToolGradlePluginExtension =
      project.extensions.getByType(NotaryToolGradlePluginExtension::class.java)
    logger.lifecycle("Starting task: ${this.name}")

    val client = NotaryToolClient(
      issuerId = pluginExtension.issuerId.get(),
      privateKeyId = pluginExtension.privateKeyId.get(),
      privateKeyFile = pluginExtension.privateKeyFile.get(),
      userAgent = "${project.name}/${project.version}",
    )

    logger.info("User-Agent: ${client.userAgent}")

    when (val result = client.getPreviousSubmissions()) {
      is Ok -> {
        logger.quiet("Submission History (last 100 submission):")
        val submissionListResponse = result.value
        submissionListResponse.submissionInfoList.forEach { submissionInfo ->
          val createdDate = submissionInfo.createdDate
          val createdDateString: String = if (createdDate != null) {
            createdDate.atZone(ZoneId.systemDefault())
              .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.LONG))
          } else {
            submissionInfo.createdDateText
          }
          logger.quiet("'${submissionInfo.id}'\t'${submissionInfo.name}'\t'${submissionInfo.status}'\t'$createdDateString'")
        }
      }

      is Err -> {
        val notaryToolError = result.error
        logger.warn(notaryToolError.longMsg)
      }
    }
    logger.lifecycle("Completed task: ${this.name}")
  }
}
