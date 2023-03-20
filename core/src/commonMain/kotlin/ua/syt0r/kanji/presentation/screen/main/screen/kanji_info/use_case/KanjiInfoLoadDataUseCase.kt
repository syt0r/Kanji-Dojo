package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.common.*
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.common.PaginatableJapaneseWordList
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState

class KanjiInfoLoadDataUseCase(
    private val kanjiDataRepository: KanjiDataRepository,
    private val analyticsManager: AnalyticsManager
) : KanjiInfoScreenContract.LoadDataUseCase {

    companion object {
        private const val NoStrokesErrorMessage = "No strokes found"
    }

    override suspend fun load(character: String): ScreenState {
        return kotlin.runCatching {
            val char = character.first()
            when {
                char.isKana() -> getKana(character)
                else -> getKanji(character)
            }
        }.getOrElse {
            analyticsManager.sendEvent("kanji_info_loading_error") {
                put("message", it.message ?: "No message")
            }
            ScreenState.NoData
        }
    }

    private suspend fun getKana(character: String): ScreenState.Loaded.Kana {
        val char = character.first()
        val isHiragana = char.isHiragana()

        val kanaSystem = if (isHiragana) {
            CharactersClassification.Kana.Hiragana
        } else {
            CharactersClassification.Kana.Katakana
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
            words = getWords(character),
            kanaSystem = kanaSystem,
            reading = reading
        )
    }

    private suspend fun getKanji(character: String): ScreenState.Loaded.Kanji {
        val kanjiData = kanjiDataRepository.getData(character)

        val readings = kanjiDataRepository.getReadings(character)
        val onReadings = readings.filter { it.value == KanjiReadingTableSchema.ReadingType.ON }
            .map { it.key }
        val kunReadings = readings.filter { it.value == KanjiReadingTableSchema.ReadingType.KUN }
            .map { it.key }

        val classifications = kanjiDataRepository.getCharacterClassifications(character)

        return ScreenState.Loaded.Kanji(
            character = character,
            strokes = getStrokes(character),
            radicals = getRadicals(character),
            words = getWords(character),
            meanings = kanjiDataRepository.getMeanings(character),
            on = onReadings,
            kun = kunReadings,
            grade = classifications.find { it is CharactersClassification.Grade }
                ?.let { it as CharactersClassification.Grade }
                ?.number,
            jlptLevel = classifications.find { it is CharactersClassification.JLPT }
                ?.let { it as CharactersClassification.JLPT }
                ?.level,
            frequency = kanjiData?.frequency,
            wanikaniLevel = classifications.find { it is CharactersClassification.Wanikani }
                ?.let { it as CharactersClassification.Wanikani }
                ?.level
        )
    }

    private suspend fun getStrokes(character: String) = parseKanjiStrokes(
        kanjiDataRepository.getStrokes(character)
    ).also { require(it.isNotEmpty()) { NoStrokesErrorMessage } }

    private suspend fun getRadicals(character: String) = kanjiDataRepository
        .getRadicalsInCharacter(character)
        .sortedBy { it.strokesCount }

    private suspend fun getWords(character: String): MutableState<PaginatableJapaneseWordList> {
        val totalWordsCount = kanjiDataRepository.getWordsWithTextCount(character)
        val initialList = kanjiDataRepository.getWordsWithText(
            text = character,
            limit = KanjiInfoScreenContract.InitiallyLoadedWordsAmount
        )
        return PaginatableJapaneseWordList(
            totalCount = totalWordsCount,
            items = initialList
        ).let { mutableStateOf(it) }
    }

}