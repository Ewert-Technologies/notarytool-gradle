package ca.ewert.notarytool.gradle.extensions

import org.gradle.api.provider.Property

/**
 * TODO: Add Comments
 *
 * Created: 2023-07-20
 * @author Victor Ewert
 */
interface GreetingExtension {
  val message: Property<String>
  val name: Property<String>
}
