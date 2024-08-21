import java.io.File
import java.util.Properties

object Credentials {
    fun credentialsProperties(): Properties {
        val credentialsProperties = Properties()
        val secretsPropertiesFiles = listOf("credentials.properties")
        val secretsPropertiesFile =
            secretsPropertiesFiles.map { File(it) }.firstOrNull { it.exists() }
                ?: throw Exception("Unable to find credentials.properties!")
        credentialsProperties.load(secretsPropertiesFile.inputStream())
        return credentialsProperties
    }
}