package ua.syt0r.kanji_dojo.shared.db

import com.google.gson.annotations.SerializedName

data class FuriganaDBEntity(
    @SerializedName("text") val text: String,
    @SerializedName("annotation") val annotation: String?
)

