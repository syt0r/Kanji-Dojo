package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case

import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.japanese.isKana
import ua.syt0r.kanji.core.japanese.isKanji
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.ValidationResult

class PracticeCreateValidateCharactersUseCase(
    private val appDataRepository: AppDataRepository
) : PracticeCreateScreenContract.ValidateCharactersUseCase {

    override suspend fun processInput(input: String): ValidationResult {
        val parsedCharacters = input.toCharArray()
            .filter { it.isKanji() || it.isKana() }

        val known = mutableListOf<String>()
        val unknown = mutableListOf<String>()

        parsedCharacters.forEach { character ->

            val strokes = appDataRepository.getStrokes(character.toString())

            val isKnown = strokes.isNotEmpty() && character.let {
                when {
                    it.isKana() -> true
                    it.isKanji() -> {
                        appDataRepository.getReadings(character.toString()).isNotEmpty() &&
                                appDataRepository.getMeanings(character.toString()).isNotEmpty()
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