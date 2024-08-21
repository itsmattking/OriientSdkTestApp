import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

fun credentialsProperties(): Properties {
    val credentialsProperties = Properties()
    val secretsPropertiesFiles = listOf("credentials.properties")
    val secretsPropertiesFile =
        secretsPropertiesFiles.map { file(it) }.firstOrNull { it.exists() }
            ?: throw Exception("Unable to find credentials.properties!")
    credentialsProperties.load(secretsPropertiesFile.inputStream())
    return credentialsProperties
}