package ua.syt0r.kanji.presentation.common.resources.string

val Int.withLeading0: String
    get() = if (this >= 10) toString() else "0$this"