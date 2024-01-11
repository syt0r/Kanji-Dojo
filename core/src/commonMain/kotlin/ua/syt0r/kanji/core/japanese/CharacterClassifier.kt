package ua.syt0r.kanji.core.japanese

interface CharacterClassifier {
    fun get(character: String): List<CharacterClassification>
}

class DefaultCharacterClassifier : CharacterClassifier {

    private val allClassifications = CharacterClassification.Kana.all +
            CharacterClassification.JLPT.all +
            CharacterClassification.Grade.all +
            CharacterClassification.Wanikani.all

    private val sets by lazy {
        allClassifications.associateBy { it.characters.toSet() }
    }

    override fun get(character: String): List<CharacterClassification> {
        return sets.filter { it.key.contains(character) }.values.toList()
    }

}