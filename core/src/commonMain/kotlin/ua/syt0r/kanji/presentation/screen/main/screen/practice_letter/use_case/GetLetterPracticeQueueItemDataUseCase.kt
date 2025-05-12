package ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.use_case

import kotlinx.coroutines.CoroutineScope
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.ReadingType
import ua.syt0r.kanji.core.japanese.getKanaInfo
import ua.syt0r.kanji.core.japanese.isKana
import ua.syt0r.kanji.core.japanese.kanaToRomaji
import ua.syt0r.kanji.presentation.common.Paginateable
import ua.syt0r.kanji.presentation.common.paginateable
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeExampleWord
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeItemData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeQueueItemDescriptor

interface GetLetterPracticeQueueItemDataUseCase {
    suspend operator fun invoke(
        descriptor: LetterPracticeQueueItemDescriptor,
        coroutineScope: CoroutineScope
    ): LetterPracticeItemData
}

class DefaultGetLetterPracticeQueueItemDataUseCase(
    private val appDataRepository: AppDataRepository
) : GetLetterPracticeQueueItemDataUseCase {

    override suspend fun invoke(
        descriptor: LetterPracticeQueueItemDescriptor,
        coroutineScope: CoroutineScope
    ): LetterPracticeItemData {

        val isKana = descriptor.character.first().isKana()

        val examples: Paginateable<LetterPracticeExampleWord> = when {
            isKana -> getExamples(
                coroutineScope = coroutineScope,
                descriptor = descriptor,
                countProvider = { appDataRepository.getKanaWordsWithTextCount(descriptor.character) },
                wordsProvider = { offset ->
                    appDataRepository.getKanaWords(
                        char = descriptor.character,
                        offset = offset,
                        limit = LetterPracticeScreenContract.EXAMPLES_LOAD_PAGE_SIZE
                    )
                },
                mapper = { it.toExample(romajiReading = descriptor.romajiReading) }
            )

            else -> getExamples(
                coroutineScope = coroutineScope,
                descriptor = descriptor,
                countProvider = { appDataRepository.getWordsWithTextCount(descriptor.character) },
                wordsProvider = { offset ->
                    appDataRepository.getWordsWithText(
                        text = descriptor.character,
                        offset = offset,
                        limit = LetterPracticeScreenContract.EXAMPLES_LOAD_PAGE_SIZE
                    )
                },
                mapper = { it.toExample(romajiReading = false) }
            )
        }

        return when (descriptor) {
            is LetterPracticeQueueItemDescriptor.Writing -> {
                getWritingItemData(
                    character = descriptor.character,
                    isKana = isKana,
                    examples = examples
                )
            }

            is LetterPracticeQueueItemDescriptor.Reading -> {
                getReadingItemData(
                    character = descriptor.character,
                    isKana = isKana,
                    examples = examples
                )
            }
        }
    }

    private suspend fun getExamples(
        coroutineScope: CoroutineScope,
        descriptor: LetterPracticeQueueItemDescriptor,
        countProvider: suspend () -> Int,
        wordsProvider: suspend (offset: Int) -> List<JapaneseWord>,
        mapper: (JapaneseWord) -> LetterPracticeExampleWord
    ): Paginateable<LetterPracticeExampleWord> {
        val primaryExamples = appDataRepository
            .getWordExamples(descriptor.character)
            .map { mapper(it) }

        val primaryExamplesIds = primaryExamples
            .map { it.word.id }
            .toSet()

        var extraOffset = 0

        val paginateable = paginateable(
            coroutineScope = coroutineScope,
            limit = countProvider(),
            initial = primaryExamples,
            loadMoreImmediately = true
        ) { offset ->
            val newWords = wordsProvider(offset + extraOffset)
            val filteredNewWords = newWords.filter { !primaryExamplesIds.contains(it.id) }
            extraOffset += LetterPracticeScreenContract.EXAMPLES_LOAD_PAGE_SIZE - filteredNewWords.size

            filteredNewWords.map { mapper(it) }
        }

        return paginateable
    }

    private fun JapaneseWord.toExample(romajiReading: Boolean): LetterPracticeExampleWord {
        return LetterPracticeExampleWord(
            word = this,
            romaji = when {
                romajiReading -> reading.kanaReading.kanaToRomaji()
                else -> null
            }
        )
    }

    private suspend fun getWritingItemData(
        character: String,
        isKana: Boolean,
        examples: Paginateable<LetterPracticeExampleWord>
    ): LetterPracticeItemData {
        val strokes = parseKanjiStrokes(appDataRepository.getStrokes(character))
        return when {
            isKana -> {
                val kanaInfo = getKanaInfo(character.first())

                LetterPracticeItemData.KanaWritingData(
                    character = character,
                    strokes = strokes,
                    examples = examples,
                    kanaSystem = kanaInfo.classification,
                    reading = kanaInfo.reading
                )
            }

            else -> {
                val readings = appDataRepository.getReadings(character)
                LetterPracticeItemData.KanjiWritingData(
                    character = character,
                    strokes = strokes,
                    radicals = appDataRepository.getRadicalsInCharacter(character),
                    examples = examples,
                    on = readings.filter { it.value == ReadingType.ON }
                        .keys
                        .toList(),
                    kun = readings.filter { it.value == ReadingType.KUN }
                        .keys
                        .toList(),
                    meanings = appDataRepository.getMeanings(character),
                    variants = appDataRepository.getData(character)
                        ?.variantFamily
                        ?.replace(character, "")
                )
            }
        }
    }

    private suspend fun getReadingItemData(
        character: String,
        isKana: Boolean,
        examples: Paginateable<LetterPracticeExampleWord>
    ): LetterPracticeItemData.ReadingData {
        return when {
            isKana -> {
                val kanaInfo = getKanaInfo(character.first())
                LetterPracticeItemData.KanaReadingData(
                    character = character,
                    examples = examples,
                    kanaSystem = kanaInfo.classification,
                    reading = kanaInfo.reading
                )
            }

            else -> {
                val readings = appDataRepository.getReadings(character)
                LetterPracticeItemData.KanjiReadingData(
                    character = character,
                    examples = examples,
                    radicals = appDataRepository.getRadicalsInCharacter(character),
                    on = readings.filter { it.value == ReadingType.ON }
                        .keys
                        .toList(),
                    kun = readings.filter { it.value == ReadingType.KUN }
                        .keys
                        .toList(),
                    meanings = appDataRepository.getMeanings(character),
                    variants = appDataRepository.getData(character)
                        ?.variantFamily
                        ?.replace(character, "")
                )
            }
        }
    }

}