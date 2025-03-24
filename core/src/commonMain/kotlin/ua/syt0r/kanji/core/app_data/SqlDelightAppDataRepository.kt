package ua.syt0r.kanji.core.app_data

import kotlinx.coroutines.Deferred
import ua.syt0r.kanji.core.app_data.data.CharacterRadical
import ua.syt0r.kanji.core.app_data.data.DetailedJapaneseWord
import ua.syt0r.kanji.core.app_data.data.DetailedVocabReading
import ua.syt0r.kanji.core.app_data.data.DetailedVocabSense
import ua.syt0r.kanji.core.app_data.data.FuriganaDBEntityCreator
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.app_data.data.FuriganaStringCompound
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.KanjiData
import ua.syt0r.kanji.core.app_data.data.RadicalData
import ua.syt0r.kanji.core.app_data.data.ReadingType
import ua.syt0r.kanji.core.app_data.data.VocabReading
import ua.syt0r.kanji.core.app_data.data.VocabReadingInfo
import ua.syt0r.kanji.core.app_data.db.AppDataDatabase
import ua.syt0r.kanji.core.appdata.db.LettersQueries
import ua.syt0r.kanji.core.appdata.db.VocabQueries

class SqlDelightAppDataRepository(
    private val deferredDatabase: Deferred<AppDataDatabase>
) : AppDataRepository {

    private suspend fun <T> lettersQuery(
        transactionScope: LettersQueries.() -> T
    ): T {
        val queries = deferredDatabase.await().lettersQueries
        return queries.transactionWithResult { queries.transactionScope() }
    }

    private suspend fun <T> vocabQuery(
        transactionScope: VocabQueries.() -> T
    ): T {
        val queries = deferredDatabase.await().vocabQueries
        return queries.transactionWithResult { queries.transactionScope() }
    }

    override suspend fun getStrokes(character: String): List<String> = lettersQuery {
        getStrokes(character).executeAsList()
    }

    override suspend fun getRadicalsInCharacter(
        character: String
    ): List<CharacterRadical> = lettersQuery {
        getCharacterRadicals(character).executeAsList().map {
            it.run {
                CharacterRadical(
                    character = character,
                    radical = radical,
                    startPosition = start_stroke.toInt(),
                    strokesCount = strokes_count.toInt()
                )
            }
        }
    }

    override suspend fun getMeanings(kanji: String): List<String> = lettersQuery {
        getKanjiMeanings(kanji).executeAsList()
    }

    override suspend fun getReadings(
        kanji: String
    ): Map<String, ReadingType> = lettersQuery {
        getKanjiReadings(kanji).executeAsList().associate { readingData ->
            readingData.reading to ReadingType.entries
                .find { it.value == readingData.reading_type }!!
        }
    }

    override suspend fun getClassificationsForKanji(kanji: String): List<String> = lettersQuery {
        getClassificationsForKanji(kanji).executeAsList()
    }

    override suspend fun getKanjiForClassification(
        classification: String
    ): List<String> = lettersQuery {
        getKanjiWithClassification(classification).executeAsList()
    }

    override suspend fun getCharacterReadingsOfLength(
        length: Int, limit: Int
    ): List<String> = vocabQuery {
        getVocabKanaReadingsOfLength(length.toLong(), limit.toLong()).executeAsList()
    }

    override suspend fun getData(kanji: String): KanjiData? = lettersQuery {
        getKanjiData(kanji).executeAsOneOrNull()?.run {
            KanjiData(
                kanji = kanji, frequency = frequency?.toInt(), variantFamily = variantFamily
            )
        }
    }

    override suspend fun getRadicals(): List<RadicalData> = lettersQuery {
        getRadicals().executeAsList().map { RadicalData(it.radical, it.strokesCount.toInt()) }
    }

    override suspend fun getCharactersWithRadicals(
        radicals: List<String>
    ): List<String> = lettersQuery {
        getCharsWithRadicals(radicals, radicals.size.toLong()).executeAsList()
    }

    override suspend fun getAllRadicalsInCharactersWithSelectedRadicals(
        radicals: Set<String>
    ): List<String> = lettersQuery {
        getAllRadicalsInCharactersWithSelectedRadicals(
            radicals,
            radicals.size.toLong()
        ).executeAsList()
    }


    override suspend fun getWordsWithTextCount(text: String): Int = vocabQuery {
        getCountOfVocabReadingsWithText(text = text, includeKanjiReadings = true).executeAsOne()
            .toInt()
    }

    override suspend fun getWordsWithText(
        text: String, offset: Int, limit: Int
    ): List<JapaneseWord> = vocabQuery {
        getVocabReadingsWithText(text, true, offset.toLong(), limit.toLong())
            .executeAsList()
            .map { element ->
                getWord(
                    id = element.entry_id,
                    kanaReading = element.reading.takeIf { element.isKana == 1L },
                    kanjiReading = element.reading.takeIf { element.isKana == 0L }
                )
            }
    }

    override suspend fun getWordExamples(letter: String): List<JapaneseWord> {
        val entries = lettersQuery { getVocabExamplesForLetter(letter).executeAsList() }

        val wordIdList = entries.map { it.vocab_id }.toSet()
        val vocabSenses = getWordSenses(wordIdList).associateBy { it.wordId }

        return vocabQuery {
            entries.map { entry ->
                val sense = vocabSenses.getValue(entry.vocab_id)
                    .senseList.first()

                JapaneseWord(
                    id = entry.vocab_id,
                    reading = VocabReading(
                        kanjiReading = entry.kanji,
                        kanaReading = entry.kana,
                        furigana = entry.kanji?.let { searchFurigana(entry.kanji, entry.kana) }
                            ?.executeAsOneOrNull()
                            ?.parseAsFurigana()
                    ),
                    glossary = sense.glossary,
                    partOfSpeechList = emptyList()
                )
            }
        }
    }

    override suspend fun getWord(
        id: Long,
        kanjiReading: String?,
        kanaReading: String
    ): JapaneseWord = vocabQuery {
        getWord(
            id = id,
            kanaReading = kanaReading,
            kanjiReading = kanjiReading
        )
    }

    override suspend fun findWords(
        id: Long?,
        kanjiReading: String?,
        kanaReading: String?
    ): List<JapaneseWord> = vocabQuery {
        val elements = findVocabElementsByIdOrReading(
            entryId = id ?: -1,
            kanjiReading = kanjiReading ?: "",
            kanaReading = kanaReading ?: ""
        ).executeAsList()

        elements.groupBy { it.entry_id }
            .filter { id == null || id == it.key }
            .mapNotNull { (wordId, elements) ->

                when {
                    kanjiReading != null && kanaReading != null -> {
                        getWord(
                            id = wordId,
                            kanaReading = kanaReading,
                            kanjiReading = kanjiReading
                        )
                    }

                    kanjiReading == null && kanaReading == null -> {
                        val element = elements.first()
                        getWord(
                            id = wordId,
                            kanaReading = element.reading.takeIf { element.isKana == 1L },
                            kanjiReading = element.reading.takeIf { element.isKana == 0L }
                        )
                    }

                    else -> {
                        val element = elements
                            .firstOrNull { it.reading == kanjiReading || it.reading == kanaReading }
                            ?: return@mapNotNull null
                        getWord(
                            id = wordId,
                            kanaReading = element.reading.takeIf { element.isKana == 1L },
                            kanjiReading = element.reading.takeIf { element.isKana == 0L }
                        )
                    }
                }
            }
    }

    override suspend fun getKanaWords(
        char: String, limit: Int
    ): List<JapaneseWord> = vocabQuery {
        getVocabReadingsWithText(
            text = char,
            includeKanjiReadings = false,
            offset = 0,
            limit = limit.toLong()
        )
            .executeAsList()
            .map { getWord(it.entry_id, it.reading, null) }
    }

    override suspend fun getSentencesWithTextCount(text: String): Int = vocabQuery {
        getSenseExamplesWithTextCount(text).executeAsOne().toInt()
    }

    override suspend fun getSentencesWithText(
        text: String,
        offset: Int,
        limit: Int
    ): List<Sentence> = vocabQuery {
        getSenseExamplesWithText(text = text, offset = offset.toLong(), limit = limit.toLong())
            .executeAsList()
            .map { Sentence(it.sentence, it.translation) }
    }

    override suspend fun getWordSenses(idList: Set<Long>): List<VocabSenseGroup> = vocabQuery {
        idList.asSequence()
            .chunked(100)
            .flatMap { getVocabSensesWithDetails(it, DELIMITER).executeAsList() }
            .groupBy { it.entry_id }
            .map { (wordId, senseItems) ->
                VocabSenseGroup(
                    wordId = wordId,
                    senseList = senseItems.map {
                        VocabSenseGroup.Sense(
                            glossary = it.glosses!!.split(DELIMITER),
                            kanjiRestrictions = it.kanji_restrictions
                                ?.split(DELIMITER) ?: emptyList(),
                            kanaRestrictions = it.kana_restrictions
                                ?.split(DELIMITER) ?: emptyList()
                        )
                    }
                )
            }
    }

    override suspend fun getDetailedWord(id: Long): DetailedJapaneseWord = vocabQuery {
        getDetailedWordInternal(id)
    }

    override suspend fun getImportDeckWordsCount(classification: String): Int = vocabQuery {
        getVocabDeckCardsCount(classification).executeAsOne().toInt()
    }

    override suspend fun getImportDeckWords(
        classification: String
    ): List<ImportDeckWord> = vocabQuery {
        getVocabDeckCards(classification)
            .executeAsList()
            .map {
                ImportDeckWord(
                    id = it.jmdict_seq,
                    kanji = it.kanji,
                    kana = it.kana,
                    meaning = it.definition
                )
            }
    }

    private fun VocabQueries.getDetailedWordInternal(id: Long): DetailedJapaneseWord {
        val senseElements = getVocabSensesWithDetails(listOf(id), DELIMITER).executeAsList()

        val kanjiElements = getVocabKanjiElementsWithDetails(id, DELIMITER).executeAsList()
        val kanaElements = getVocabKanaElementsWithDetails(id, DELIMITER).executeAsList()

        val kanaElementsWithReadings = kanaElements.associateWith {
            DetailedVocabReading(
                elementId = it.element_id,
                kanji = null,
                kana = it.reading,
                furigana = null,
                info = it.informations.parseAsVocabReadingInfoSet(),
                noKanji = it.no_kanji == 1L
            )
        }

        val kanjiReadings = kanjiElements.flatMap { kanjiElement ->
            val kanjiReadingInfo = kanjiElement.informations.parseAsVocabReadingInfoSet()

            val matchingKanaReadings = kanaElementsWithReadings.filter { (kanaElement, _) ->
                val restrictedKanji = kanaElement.restricted_kanji?.split(DELIMITER)
                    ?: emptyList()
                kanjiReadingInfo.contains(VocabReadingInfo.SearchOnlyKanjiForm) ||
                        restrictedKanji.isEmpty() ||
                        restrictedKanji.contains(kanjiElement.reading)
            }

            matchingKanaReadings.map { (kanaElement, kanaReading) ->
                val kanji = kanjiElement.reading
                val kana = kanaElement.reading
                DetailedVocabReading(
                    elementId = kanjiElement.element_id,
                    kanji = kanji,
                    kana = kana,
                    furigana = searchFurigana(kanji, kana).executeAsOneOrNull()?.parseAsFurigana(),
                    info = kanjiReadingInfo.plus(kanaReading.info),
                    noKanji = kanaReading.noKanji
                )
            }
        }

        val kanaReadings = kanaElementsWithReadings.values

        val allReadings = kanjiReadings.plus(kanaReadings).sortedWith(vocabReadingsComparator)

        val senseList = senseElements.map { senseElement ->
            val senseKanjiRestrictions = senseElement.kanji_restrictions?.split(DELIMITER)
                ?.toSet()
                ?: emptySet()

            val senseKanaRestrictions = senseElement.kana_restrictions?.split(DELIMITER)
                ?.toSet()
                ?: emptySet()

            val filteredReadings = allReadings.filter {
                val matchesKanjiRestrictions = senseKanjiRestrictions.isEmpty() ||
                        senseKanjiRestrictions.contains(it.kanji)

                val matchesKanaRestrictions = senseKanaRestrictions.isEmpty() ||
                        senseKanaRestrictions.contains(it.kana)

                matchesKanjiRestrictions && matchesKanaRestrictions
            }

            DetailedVocabSense(
                glossary = senseElement.glosses?.split(DELIMITER) ?: emptyList(),
                partOfSpeechList = senseElement.explanations?.split(DELIMITER) ?: emptyList(),
                readings = filteredReadings
            )
        }

        return DetailedJapaneseWord(
            id = id,
            senseList = senseList
        )
    }

    private fun VocabQueries.getWord(
        id: Long,
        kanaReading: String?,
        kanjiReading: String?,
    ): JapaneseWord {
        val detailedWord = getDetailedWordInternal(id)
        for (sense in detailedWord.senseList) {

            for (reading in sense.readings) {

                val matchesKanjiConstraint = kanjiReading == null || reading.kanji == kanjiReading
                val matchesKanaConstraint = kanaReading == null || reading.kana == kanaReading
                val matches = matchesKanjiConstraint && matchesKanaConstraint

                if (matches) {
                    return JapaneseWord(
                        id = id,
                        reading = VocabReading(
                            kanjiReading = reading.kanji,
                            kanaReading = reading.kana,
                            furigana = reading.furigana
                        ),
                        glossary = sense.glossary,
                        partOfSpeechList = sense.partOfSpeechList
                    )
                }

            }

        }
        error("Word not found, id[$id], kanaReading[$kanaReading], kanjiReading[$kanjiReading]")
    }

    private fun String.parseAsFurigana(): FuriganaString = FuriganaDBEntityCreator
        .fromJsonString(this)
        .map { FuriganaStringCompound(it.text, it.annotation) }
        .let { FuriganaString(it) }

    private fun String?.parseAsVocabReadingInfoSet(): Set<VocabReadingInfo> {
        return this?.split(DELIMITER)
            ?.map { jmDictInfoValue ->
                VocabReadingInfo.entries.firstOrNull { it.jmDictValue == jmDictInfoValue }
                    ?: error("No info with value[$jmDictInfoValue]")
            }
            ?.toSet()
            ?: emptySet()
    }

    companion object {

        private const val DELIMITER = "|||"

        private val readingInfoSetWithLowerPriority = setOf(
            VocabReadingInfo.IrregularKanaUsage,
            VocabReadingInfo.IrregularKanjiUsage,
            VocabReadingInfo.OutdatedKana,
            VocabReadingInfo.OutdatedKanji,
            VocabReadingInfo.SearchOnlyKanaForm,
            VocabReadingInfo.SearchOnlyKanjiForm,
            VocabReadingInfo.RarelyUsedKanjiForm,
            VocabReadingInfo.RarelyUsedKanaForm
        )

        // asc order -> false - 0, true - 1
        private val vocabReadingsComparator = compareBy<DetailedVocabReading>(
            { it.noKanji },
            { it.kanji == null || it.info.intersect(readingInfoSetWithLowerPriority).isNotEmpty() },
            { it.elementId }
        )

    }

}
