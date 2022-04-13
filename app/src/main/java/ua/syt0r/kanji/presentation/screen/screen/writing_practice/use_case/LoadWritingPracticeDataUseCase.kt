package ua.syt0r.kanji.presentation.screen.screen.writing_practice.use_case

import kotlinx.coroutines.delay
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.KanjiData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import ua.syt0r.kanji_db_model.db.KanjiReadingTable
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class LoadWritingPracticeDataUseCase @Inject constructor(
    private val kanjiRepository: KanjiDataContract.Repository
) {

    companion object {
        private const val MINIMAL_LOADING_TIME = 600L
    }

    suspend fun load(practiceConfiguration: PracticeConfiguration): List<KanjiData> {

        val loadingStartTime = System.currentTimeMillis()

        val kanjiDataList = practiceConfiguration.kanjiList.map { kanji ->
            val readings = kanjiRepository.getReadings(kanji)
            KanjiData(
                kanji = kanji,
                on = readings.filter { it.value == KanjiReadingTable.ReadingType.ON }
                    .keys
                    .toList(),
                kun = readings.filter { it.value == KanjiReadingTable.ReadingType.KUN }
                    .keys
                    .toList(),
                meanings = kanjiRepository.getMeanings(kanji),
                strokes = parseKanjiStrokes(
                    strokes = kanjiRepository.getStrokes(kanji)
                )
            )
        }

        val timeToMinimalLoadingLeft = MINIMAL_LOADING_TIME - System.currentTimeMillis() +
                loadingStartTime
        val delayTime = max(0, min(MINIMAL_LOADING_TIME, timeToMinimalLoadingLeft))

        delay(delayTime)

        return kanjiDataList
    }

}