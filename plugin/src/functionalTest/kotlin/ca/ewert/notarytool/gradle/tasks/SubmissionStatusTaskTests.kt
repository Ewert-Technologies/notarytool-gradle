package ca.ewert.notarytool.gradle.tasks

import assertk.assertThat
import assertk.assertions.contains
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * TODO: Add Documentation
 *
 * @author Victor Ewert
 */
class SubmissionStatusTaskTests {

  @field:TempDir
  lateinit var projectDir: File

  private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
  private val settingsFile by lazy { projectDir.resolve("settings.gradle") }


  @Test
  fun test1() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String =
      this::class.java.getResource("/private/build2.gradle.ktstest")?.readText(Charsets.UTF_8) ?: ""

    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submissionStatus")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("Starting task: submissionStatus")
    assertThat(result.output).contains("The String passed in is not a valid submission id. Invalid String: ")
  }

  @Test
  fun test2() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String =
      this::class.java.getResource("/private/build2.gradle.ktstest")?.readText(Charsets.UTF_8) ?: ""

    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submissionStatus", "--submissionId=12345")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("Starting task: submissionStatus")
    assertThat(result.output).contains("The String passed in is not a valid submission id. Invalid String: 12345")
  }

  @Test
  fun test3() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String =
      this::class.java.getResource("/private/build2.gradle.ktstest")?.readText(Charsets.UTF_8) ?: ""

    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submissionStatus", "--submissionId=2efe2717-52ef-43a5-96dc-0797e4ca1041")
    runner.withProjectDir(projectDir)
    val result = runner.build()
  }

  @Test
  fun test4() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String =
      this::class.java.getResource("/private/build2.gradle.ktstest")?.readText(Charsets.UTF_8) ?: ""

    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submissionStatus", "--submissionId=c6da5f3b-e467-4197-98fa-c83bac3d2953")
    runner.withProjectDir(projectDir)
    val result = runner.build()
  }
}