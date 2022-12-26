package ua.syt0r.kanji.core.kanji_data.data

data class FuriganaString(
    val compounds: List<FuriganaAnnotatedCharacter>
) {

    operator fun plus(string: String): FuriganaString {
        return FuriganaString(compounds.plus(FuriganaAnnotatedCharacter(string)))
    }

}

data class FuriganaAnnotatedCharacter(
    val character: String,
    val annotation: String? = null
)

class FuriganaStringBuilder {

    private val list = mutableListOf<FuriganaAnnotatedCharacter>()

    fun append(character: String, annotation: String? = null) =
        list.add(FuriganaAnnotatedCharacter(character, annotation))

    fun append(furiganaString: FuriganaString) {
        list.addAll(furiganaString.compounds)
    }

    fun build() = FuriganaString(list)

}

fun buildFuriganaString(scope: FuriganaStringBuilder.() -> Unit): FuriganaString {
    val builder = FuriganaStringBuilder()
    builder.scope()
    return builder.build()
}