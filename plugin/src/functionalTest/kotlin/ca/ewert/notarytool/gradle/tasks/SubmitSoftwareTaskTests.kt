package ca.ewert.notarytool.gradle.tasks

import assertk.assertThat
import assertk.assertions.contains
import ca.ewert.notarytool.gradle.readBuildFileContents
import ca.ewert.notarytool.gradle.resourceToPath
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class SubmitSoftwareTaskTests {
  @field:TempDir
  lateinit var projectDir: File

  private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
  private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

  @BeforeEach
  fun before() {
    println("Java version: ${System.getProperty("java.version")}")
    println("Java vendor: ${System.getProperty("java.vendor")}")
  }

  /**
   * Test with no file specified.
   */
  @Test
  fun test1() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build2.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(
      result.output,
    ).contains(
      "No argument was provided for command-line option '--fileLocation' with description: 'Location (path) of the file to be notarized.'",
    )
  }

  /**
   * Test passing a file name argument, file doesn't exist.
   */
  @Test
  fun test2() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build2.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware", "--fileLocation=abc")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("abc does not exist.")
  }

  /**
   * Test passing a file name argument, file exists, but is invalid for notarization.
   */
  @Test
  fun test3() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build2.gradle.ktstest")
    buildFile.writeText(buildFileContents)

    val testFile: Path? = resourceToPath("/private/pwm_invalid_aarch64.dmg")

    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments(
      "submitSoftware",
      "--fileLocation=$testFile",
    )
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("Uploaded file for notarization. Submission ID:")
    assertThat(result.output).contains("Invalid")
  }

  /**
   * Test using file name in gradle file, file does not exist
   */
  @Test
  fun test4() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build3.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("pwm_missing_aarch64.dmg does not exist.")
  }

  /**
   * Test using file name in gradle file, file exists, and can be notarized succesfully
   */
  @Test
  fun test5() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build4.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("Accepted")
  }

  /**
   * Test using the file name in gradle file, the file exists and can be notarized successfully.
   * No polling, `--no-poll` passed in as command argument
   */
  @Test
  fun test6() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build4.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware", "--no-poll")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("Check the submission status using:")
  }

  /**
   * Test using the file name in gradle file, the file exists and can be notarized successfully.
   * No polling, `poll` set to `false` in build script.
   */
  @Test
  fun test7() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build5.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("Check the submission status using:")
  }
}
