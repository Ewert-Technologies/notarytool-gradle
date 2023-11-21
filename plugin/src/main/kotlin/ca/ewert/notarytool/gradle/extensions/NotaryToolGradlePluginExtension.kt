package ca.ewert.notarytool.gradle.extensions

import org.gradle.api.provider.Property
import java.nio.file.Path

/**
 * Extension used to configure the notarytool-gradle plugin.
 * Contains the credentials required to interact with the
 * Notary Web API.
 *
 * Created: 2023-07-21
 * @author Victor Ewert
 */
interface NotaryToolGradlePluginExtension {

  /** Issuer ID from the API Keys page in App Store Connect  */
  val issuerId: Property<String>

  /** The private key ID from App Store Connect */
  val privateKeyId: Property<String>

  /** Private key file downloaded after creating the App Store Connect API Key */
  val privateKeyFile: Property<Path>
}
