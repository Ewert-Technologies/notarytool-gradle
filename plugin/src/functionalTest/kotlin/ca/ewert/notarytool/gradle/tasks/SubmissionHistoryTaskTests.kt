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
class SubmissionHistoryTaskTests {

  @field:TempDir
  lateinit var projectDir: File

  private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
  private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

  @Test
  fun test1() {
    settingsFile.writeText("")
    val buildFileContents: String =
      this::class.java.getResource("/private/build1.gradle.ktstest")?.readText(Charsets.UTF_8) ?: ""
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

    assertThat(result.output).contains("Private Key id: Victor")
    assertThat(result.output).contains("Issuer id: 12345")
    assertThat(result.output).contains("Private Key File: D:\\users\\vewert\\DevProj\\notarytool-gradle\\plugin\\src\\functionalTest\\resources\\private\\AuthKey_Test.p8")
  }
}
