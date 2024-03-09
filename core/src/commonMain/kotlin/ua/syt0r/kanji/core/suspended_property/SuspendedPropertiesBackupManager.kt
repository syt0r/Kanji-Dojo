package ua.syt0r.kanji.core.suspended_property

import kotlinx.serialization.json.JsonObject

interface SuspendedPropertiesBackupManager {
    suspend fun exportModifiedProperties(): JsonObject
    suspend fun importProperties(jsonObject: JsonObject)
}

class DefaultSuspendedPropertiesBackupManager(
    private val registryList: List<SuspendedPropertyRegistry>
) : SuspendedPropertiesBackupManager {

    override suspend fun exportModifiedProperties(): JsonObject {
        return registryList.flatMap { it.properties }
            .filter { it.isModified() }
            .associate { it.key to it.backup() }
            .let { JsonObject(it) }
    }

    override suspend fun importProperties(jsonObject: JsonObject) {
        val importedPropertiesMap = jsonObject.entries.associate { it.key to it.value }
        registryList.flatMap { it.properties }.forEach { property ->
            val value = importedPropertiesMap[property.key]
            if (value != null) property.restore(value)
        }
    }

}