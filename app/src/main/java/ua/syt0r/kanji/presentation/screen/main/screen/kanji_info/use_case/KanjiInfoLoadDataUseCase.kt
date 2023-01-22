package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case

import ua.syt0r.kanji.common.*
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState
import javax.inject.Inject

class KanjiInfoLoadDataUseCase @Inject constructor(
    private val kanjiDataRepository: KanjiDataRepository,
    private val analyticsManager: AnalyticsManager
) : KanjiInfoScreenContract.LoadDataUseCase {

    companion object {
        private const val NoStrokesErrorMessage = "No strokes found"
    }

    override fun load(character: String): ScreenState {
        return kotlin.runCatching {
            val char = character.first()
            when {
                char.isKana() -> getKana(character)
                else -> getKanji(character)
            }
        }.getOrElse {
            analyticsManager.sendEvent("kanji_info_loading_error") {
                putString("message", it.message)
            }
            ScreenState.NoData
        }
    }

    private fun getKana(character: String): ScreenState.Loaded.Kana {
        val char = character.first()
        val isHiragana = char.isHiragana()

        val kanaSystem = if (isHiragana) {
            CharactersClassification.Kana.HIRAGANA
        } else {
            CharactersClassification.Kana.KATAKANA
        }

        val reading = if (isHiragana) {
            hiraganaToRomaji(char)
        } else {
            hiraganaToRomaji(katakanaToHiragana(char))
        }

        return ScreenState.Loaded.Kana(
            character = character,
            strokes = getStrokes(character),
            radicals = getRadicals(character),
            words = kanjiDataRepository.getWordsWithCharacter(character),
            kanaSystem = kanaSystem,
            reading = reading
        )
    }

    private fun getKanji(character: String): ScreenState.Loaded.Kanji {
        val kanjiData = kanjiDataRepository.getData(character)

        val readings = kanjiDataRepository.getReadings(character)
        val onReadings = readings.filter { it.value == KanjiReadingTableSchema.ReadingType.ON }
            .map { it.key }
        val kunReadings = readings.filter { it.value == KanjiReadingTableSchema.ReadingType.KUN }
            .map { it.key }

        return ScreenState.Loaded.Kanji(
            character = character,
            strokes = getStrokes(character),
            radicals = getRadicals(character),
            words = kanjiDataRepository.getWordsWithCharacter(character),
            meanings = kanjiDataRepository.getMeanings(character),
            on = onReadings,
            kun = kunReadings,
            grade = kanjiData?.grade,
            jlpt = kanjiData?.jlpt,
            frequency = kanjiData?.frequency
        )
    }

    private fun getStrokes(character: String) = parseKanjiStrokes(
        kanjiDataRepository.getStrokes(character)
    ).also { require(it.isNotEmpty()) { NoStrokesErrorMessage } }

    private fun getRadicals(character: String) = kanjiDataRepository
        .getRadicalsInCharacter(character)
        .sortedBy { it.strokesCount }

}