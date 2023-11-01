package ca.ewert.notarytool.gradle.tasks

import assertk.assertThat
import assertk.assertions.contains
import ca.ewert.notarytool.gradle.readBuildFileContents
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class SubmitSoftwareTaskTests {
  @field:TempDir
  lateinit var projectDir: File

  private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
  private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

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
    assertThat(result.output).contains("is not a file.")
  }

  @Test
  fun test2() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build2.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware",  "--fileLocation=abc")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("abc does not exist.")
  }

  @Test
  fun test3() {
    settingsFile.writeText("rootProject.name = \"Test-Project\"")
    val buildFileContents: String = readBuildFileContents("/private/build2.gradle.ktstest")
    buildFile.writeText(buildFileContents)
    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("submitSoftware",  "--fileLocation=/Users/vewert/DevProj/notarytool-gradle/plugin/src/functionalTest/resources/private/pwm_invalid_aarch64.dmg")
    runner.withProjectDir(projectDir)
    val result = runner.build()
    assertThat(result.output).contains("Uploaded file for notarization. Submission ID:")
    assertThat(result.output).contains("Invalid")
  }

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
}
