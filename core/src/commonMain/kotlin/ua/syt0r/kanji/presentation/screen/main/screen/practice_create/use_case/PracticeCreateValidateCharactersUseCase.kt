package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case

import ua.syt0r.kanji.common.*
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.ValidationResult

class PracticeCreateValidateCharactersUseCase(
    private val kanjiDataRepository: KanjiDataRepository
) : PracticeCreateScreenContract.ValidateCharactersUseCase {

    override suspend fun processInput(input: String): ValidationResult {
        val parsedCharacters = input.toCharArray()
            .filter { it.isKanji() || it.isKana() }

        val known = sortedSetOf<String>()
        val unknown = sortedSetOf<String>()

        parsedCharacters.forEach { character ->

            val strokes = kanjiDataRepository.getStrokes(character.toString())

            val isKnown = strokes.isNotEmpty() && character.let {
                when {
                    it.isHiragana() -> Hiragana.contains(it)
                    it.isKatakana() -> Katakana.contains(it)
                    it.isKanji() -> {
                        kanjiDataRepository.getReadings(character.toString()).isNotEmpty() &&
                                kanjiDataRepository.getMeanings(character.toString()).isNotEmpty()
                    }
                    else -> false
                }
            }

            if (isKnown) {
                known.add(character.toString())
            } else {
                unknown.add(character.toString())
            }

        }

        return ValidationResult(
            detectedCharacter = known,
            unknownCharacters = unknown
        )
    }

}