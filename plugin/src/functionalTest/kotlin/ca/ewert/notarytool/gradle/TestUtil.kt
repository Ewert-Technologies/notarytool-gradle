package ca.ewert.notarytool.gradle

import java.nio.file.Path

/**
 * Reads the text from the resource file and returns it.
 *
 * @param resource location of the resource file, e.g. `/private/build1.gradle.ktstest"`
 */
public fun readBuildFileContents(resource: String): String {
  return object {}.javaClass.getResource(resource)?.readText(Charsets.UTF_8) ?: ""
}

/**
 * Convenience Method to get a [Path] from a resource location.
 *
 * @param resource Name of the resource (not must start with `/`
 */
internal fun resourceToPath(resource: String): Path? {
  return object {}.javaClass.getResource(resource)?.toURI()?.let { Path.of(it) }
}
