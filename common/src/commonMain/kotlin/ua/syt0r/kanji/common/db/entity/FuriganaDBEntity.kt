package ua.syt0r.kanji.common.db.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
data class FuriganaDBEntity(
    @SerialName("t") val text: String,
    @SerialName("a") val annotation: String? = null
)

object FuriganaDBEntityCreator {
    fun fromJsonString(string: String): List<FuriganaDBEntity> = Json.decodeFromString(string)
}

fun List<FuriganaDBEntity>.asJsonString(): String = Json.encodeToString(this)
