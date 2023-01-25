package ua.syt0r.kanji.core.kanji_data.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JapaneseWord(
    val furiganaString: FuriganaString,
    val meanings: List<String>
) : Parcelable {

    fun preview() = buildFuriganaString {
        append(furiganaString)
        append(" - ")
        append(meanings.first())
    }

    fun orderedPreview(index: Int) = buildFuriganaString {
        append("${index + 1}. ")
        append(furiganaString)
        append(" - ")
        append(meanings.first())
    }

}