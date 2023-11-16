package ca.ewert.notarytool.gradle.tasks

import assertk.assertThat
import assertk.assertions.contains
import ca.ewert.notarytool.gradle.readBuildFileContents
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Tests for SubmissionHistoryTask
 *
 * @author Victor Ewert
 */
class SubmissionHistoryTaskTests {
  @field:TempDir
  lateinit var projectDir: File

  private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
  private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

  @Test
  fun test1() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build1.gradle.ktstest")
    println()
    println("---Build File-----------------------------------------")
    println(buildFileContents)
    println("--------------------------------------------")
    buildFile.writeText(buildFileContents)

    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submissionHistory")
    runner.withProjectDir(projectDir)
    val result = runner.build()
//    println("---Output-----------------------------------------")
//    println(result.output)
//    println("--------------------------------------------")

    assertThat(result.output).contains("Starting task: submissionHistory")
  }

  @Test
  fun test2() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build2.gradle.ktstest")
    println()
    println("---Build File-----------------------------------------")
    println(buildFileContents)
    println("--------------------------------------------")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submissionHistory")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    println("---Output-----------------------------------------")
    println(result.output)
    println("--------------------------------------------")
    assertThat(result.output).contains("Submission History (last 100 submission):")
  }
}
