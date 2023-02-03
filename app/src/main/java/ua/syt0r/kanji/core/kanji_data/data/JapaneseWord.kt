package ua.syt0r.kanji.core.kanji_data.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JapaneseWord(
    val readings: List<FuriganaString>,
    val meanings: List<String>
) : Parcelable {

    fun preview() = buildFuriganaString {
        append(readings.first())
        append(" - ")
        append(meanings.first())
    }

    fun orderedPreview(index: Int) = buildFuriganaString {
        append("${index + 1}. ")
        append(readings.first())
        append(" - ")
        append(meanings.first())
    }

}