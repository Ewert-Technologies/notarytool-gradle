/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package ca.ewert.notarytool.gradle

import ca.ewert.notarytool.gradle.extensions.NotaryToolGradlePluginExtension
import ca.ewert.notarytool.gradle.tasks.SubmissionHistoryTask
import ca.ewert.notarytool.gradle.tasks.SubmissionStatusTask
import ca.ewert.notarytool.gradle.tasks.SubmitSoftwareTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Constant for the group name for the plugin's tasks */
internal const val TASK_GROUP_NAME = "notarytool"

/** Constant for the name of the SubmissionHistory task. */
private const val SUBMISSION_HISTORY_TASK_NAME = "submissionHistory"

/** Constant for the name of the SubmissionStatus task. */
private const val SUBMISSION_STATUS_TASK_NAME = "submissionStatus"

/** Constant for the name of the SubmitSoftware task. */
private const val SUBMIT_SOFTWARE_TASK_NAME = "submitSoftware"

/**
 * A Gradle Plugin used to interact with the Apple's Notarytool Web API
 */
class NotarytoolGradlePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.logger.lifecycle("Inside notarytool plugin apply()")
    project.extensions.create("notarytool-gradle-extension", NotaryToolGradlePluginExtension::class.java)

    project.tasks.register(SUBMIT_SOFTWARE_TASK_NAME, SubmitSoftwareTask::class.java)
    project.tasks.register(SUBMISSION_STATUS_TASK_NAME, SubmissionStatusTask::class.java)
    project.tasks.register(SUBMISSION_HISTORY_TASK_NAME, SubmissionHistoryTask::class.java)
  }
}
