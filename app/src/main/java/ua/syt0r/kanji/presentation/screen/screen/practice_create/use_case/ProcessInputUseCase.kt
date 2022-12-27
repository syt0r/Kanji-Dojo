package ua.syt0r.kanji.presentation.screen.screen.practice_create.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.InputProcessingResult
import ua.syt0r.kanji.common.*
import javax.inject.Inject

class ProcessInputUseCase @Inject constructor(
    private val kanjiDataRepository: KanjiDataRepository
) {

    fun processInput(input: String): InputProcessingResult {
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

        return InputProcessingResult(
            detectedCharacter = known,
            unknownCharacters = unknown
        )
    }

}