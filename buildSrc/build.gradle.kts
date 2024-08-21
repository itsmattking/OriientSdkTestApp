import java.util.Properties

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
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