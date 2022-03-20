package ua.syt0r.kanji.core.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.KanjiData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import ua.syt0r.kanji_db_model.db.KanjiReadingTable
import javax.inject.Inject

class LoadWritingPracticeDataUseCase @Inject constructor(
    private val kanjiRepository: KanjiDataContract.Repository
) {

    fun load(practiceConfiguration: PracticeConfiguration): Flow<List<KanjiData>> = flow {

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

        emit(kanjiDataList)

    }

}