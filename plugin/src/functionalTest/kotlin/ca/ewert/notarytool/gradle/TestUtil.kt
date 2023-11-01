package ca.ewert.notarytool.gradle

/**
 * Reads the text from the resource file and returns it.
 *
 * @param resource location of the resource file, e.g. `/private/build1.gradle.ktstest"`
 */
public fun readBuildFileContents(resource: String): String {
  return object {}.javaClass.getResource(resource)?.readText(Charsets.UTF_8) ?: ""
}
