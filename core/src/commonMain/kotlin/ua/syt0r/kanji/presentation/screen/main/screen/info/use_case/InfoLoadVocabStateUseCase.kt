package ua.syt0r.kanji.presentation.screen.main.screen.info.use_case

import kotlinx.coroutines.CoroutineScope
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.DetailedVocabReading
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.VocabReading
import ua.syt0r.kanji.presentation.common.paginateable
import ua.syt0r.kanji.presentation.screen.main.screen.info.InfoScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.info.InfoScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.info.InfoScreenData
import ua.syt0r.kanji.presentation.screen.main.screen.info.VocabInfoData

class InfoLoadVocabStateUseCase(
    private val appDataRepository: AppDataRepository
) : InfoScreenContract.LoadVocabStateUseCase {

    override suspend fun invoke(
        data: InfoScreenData.Vocab,
        coroutineScope: CoroutineScope
    ): ScreenState {

        val targetWord: JapaneseWord = when {
            data.id == null || data.kanaReading == null -> appDataRepository
                .findWords(null, data.kanjiReading, data.kanaReading)
                .firstOrNull()

            else -> appDataRepository.getWord(data.id, data.kanjiReading, data.kanaReading)
        } ?: return ScreenState.NoData

        val detailedWord = appDataRepository.getDetailedWord(targetWord.id)
            ?: return ScreenState.NoData

        val reading = targetWord.reading
        val matchingSenses = detailedWord.senseList
            .filter { vocabSense -> vocabSense.readings.any { it.matches(reading) } }
            .map {
                VocabInfoData.Sense(
                    glossary = it.glossary.joinToString(),
                    otherReadings = it.readings.filterNot { it.matches(reading) },
                    pos = it.partOfSpeechList.joinToString().takeIf { it.isNotEmpty() }
                )
            }

        val sentenceSearchReading = reading.run { kanjiReading ?: kanaReading }
        val sentencePaginateable = paginateable(
            coroutineScope = coroutineScope,
            limit = appDataRepository.getSentencesWithTextCount(sentenceSearchReading),
            load = { offset ->
                appDataRepository.getSentencesWithText(
                    text = sentenceSearchReading,
                    offset = offset,
                    limit = InfoScreenContract.ListPageItemsCount
                )
            }
        )

        val vocabInfoData = VocabInfoData(
            word = targetWord,
            senseList = matchingSenses,
            detailedJapaneseWord = detailedWord,
            sentences = sentencePaginateable
        )

        return ScreenState.Loaded.Vocab(vocabInfoData)
    }

    private fun DetailedVocabReading.matches(vocabReading: VocabReading): Boolean {
        return kanji == vocabReading.kanjiReading && kana == vocabReading.kanaReading
    }

}