package ua.syt0r.kanji.common.db.entity

import com.google.gson.annotations.SerializedName

data class FuriganaDBEntity(
    @SerializedName("t") val text: String,
    @SerializedName("a") val annotation: String?
)

