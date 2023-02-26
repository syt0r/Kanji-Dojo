package ua.syt0r.kanji.parser

import org.jetbrains.exposed.sql.Database
import ua.syt0r.kanji.common.*
import ua.syt0r.kanji.common.db.entity.FuriganaDBEntity
import ua.syt0r.kanji.common.db.entity.Radical
import ua.syt0r.kanji.parser.converter.KanjiDicEntryConverter
import ua.syt0r.kanji.parser.converter.KanjiVGConverter
import ua.syt0r.kanji.parser.model.CharacterClass
import ua.syt0r.kanji.parser.model.Expression
import ua.syt0r.kanji.parser.model.Word
import ua.syt0r.kanji.parser.parsers.*
import java.io.File

private val DataFolder = File("parser_data/")
private val KanjiVGFolder = File(DataFolder, "kanji-vg/")
private val KanjiDicFile = File(DataFolder, "kanjidic2.xml")
private val JMDictFile = File(DataFolder, "JMdict_e")
private val JMDictFuriganaFile = File(DataFolder, "JmdictFurigana.json")
private val CorpusLeedsFrequenciesFile = File(DataFolder, "internet-jp-forms.num")

fun main(args: Array<String>) {

    println("Parsing character strokes (KanjiVG) ...")
    val kanjiVGData = KanjiVGParser.parse(KanjiVGFolder)
    println("Done, ${kanjiVGData.size} items found")

    println("Filtering kana and kanji...")
    val charactersWithWritingDataMap = kanjiVGData.asSequence()
        .filter { it.character.isKana() || it.character.isKanji() }
        .associate { it.character.toString() to KanjiVGConverter.toCharacterWritingData(it) }
    println("Done, characters with strokes: ${charactersWithWritingDataMap.size}")

    println("Searching for common radicals...")
    val standardRadicals: List<Radical> = charactersWithWritingDataMap
        .flatMap { (char, writingData) -> writingData.standardRadicals.map { it to char } }
        .groupBy { (radicalItem, char) -> radicalItem.radical }
        .flatMap { (radical, radicalVariants) ->

            val radicalVariantStrokesCountToListOjKanji = radicalVariants
                .groupBy { (radicalItem, kanji) -> radicalItem.strokesCount }

            if (radicalVariantStrokesCountToListOjKanji.size > 1) {
                val message = radicalVariantStrokesCountToListOjKanji
                    .map { (strokesCount, radicalItemToKanji) ->
                        "$strokesCount strokes in characters[${radicalItemToKanji.joinToString("") { it.second }}]"
                    }
                    .joinToString()
                println("Attention! Radical $radical has multiple variants: $message")
            }

            radicalVariants.map { it.first }.distinct()
        }
    println("Done, ${standardRadicals.size} radicals found")

    println("Parsing character info (KanjiDic) ...")
    val kanjiDicData = KanjiDicParser.parse(KanjiDicFile)
    println("Done, ${kanjiDicData.size} items found")

    println("Filtering out characters without strokes...")
    val charactersWithInfo = kanjiDicData.asSequence()
        .filter { charactersWithWritingDataMap.contains(it.character.toString()) }
        .associate { it.character.toString() to KanjiDicEntryConverter.toKanjiInfoData(it) }
    println("Done, characters with info: ${charactersWithInfo.size}")

    println("Parsing dictionary (JMDict) ...")
    val jmDictItems = JMdictParser.parse(JMDictFile)
    println("Done, ${jmDictItems.size} items found")

    println("Parsing dictionary furigana (JMDictFurigana) ...")
    val jmDictFuriganaItems = JMdictFuriganaParser.parse(JMDictFuriganaFile)
    val furiganaGroupedByExpressions = jmDictFuriganaItems.groupBy {
        it.kanjiExpression to it.kanaExpression
    }
    println("Done, ${jmDictFuriganaItems.size}")

    println("Parsing words frequencies...")
    val wordRanks = CorpusLeedsParser.parse(CorpusLeedsFrequenciesFile).associateBy { it.word }
    println("Done, words with frequencies: ${wordRanks.size}")

    println("Looking for expressions with known characters")
    val words: List<Word> = jmDictItems.asSequence()
        .map { jmDictItem ->

            val isSingleKanjiDictionaryEntry = jmDictItem.elements.any {
                it.type == JMDictElementType.Kanji && it.expression.length == 1
            }
            if (isSingleKanjiDictionaryEntry) return@map null

            val currentExpressionReadings = jmDictItem.elements
                .filter { it.type == JMDictElementType.Reading }
                .map { it.expression }

            val expressions = jmDictItem.elements
                .flatMap { expressionElement ->

                    val containsJapaneseChars = expressionElement.expression.any {
                        it.isKana() || it.isKanji()
                    }
                    if (!containsJapaneseChars) return@flatMap emptyList()

                    val rank = wordRanks[expressionElement.expression]?.wordRank ?: Int.MAX_VALUE

                    when (expressionElement.type) {
                        JMDictElementType.Kanji -> {

                            currentExpressionReadings.map { expressionElement.expression to it }
                                .flatMap { furiganaGroupedByExpressions[it] ?: emptyList() }
                                .map {
                                    Expression.KanjiExpression(
                                        expression = expressionElement.expression,
                                        furigana = it.furigana.map { rubyItem ->
                                            FuriganaDBEntity(rubyItem.ruby, rubyItem.rt)
                                        },
                                        reading = it.kanaExpression,
                                        rank = rank
                                    )
                                }

                        }
                        JMDictElementType.Reading -> {
                            Expression.KanaExpression(
                                reading = expressionElement.expression,
                                rank = rank
                            ).let { listOf(it) }
                        }
                    }
                }
                .let { expressions ->
                    val kanjiItemReadings = expressions
                        .filterIsInstance<Expression.KanjiExpression>()
                        .map { it.reading }
                        .toSet()

                    expressions.filterNot {
                        it is Expression.KanaExpression && kanjiItemReadings.contains(it.reading)
                    }
                }
                .takeIf { it.isNotEmpty() && it.any { it.rank != Int.MAX_VALUE } }
                ?: return@map null

            Word(expressions, jmDictItem.meanings)
        }
        .filterNotNull()
        .toList()
    println("Done, ${words.size} words with complete data")

    val shouldCreateDB = args.firstOrNull()?.toBoolean() ?: true

    if (!shouldCreateDB) {
        println("Should not create DB, exiting...")
        return
    }

    println("Creating DB...")

    val outputDatabaseFile = File("app/src/main/assets/kanji_data.sqlite")

    if (outputDatabaseFile.exists()) {
        println("Existing DB found, deleting...")
        outputDatabaseFile.delete()
        outputDatabaseFile.createNewFile()
    }

    val database = Database.connect("jdbc:sqlite:${outputDatabaseFile.absolutePath}")
    val exporter = DataExporter(database)

    println("Writing strokes data...")
    exporter.writeKanjiStrokes(
        characterToStrokes = charactersWithWritingDataMap.mapValues { it.value.strokes }
    )

    println("Writing radicals...")
    exporter.writeRadicals(standardRadicals)

    println("Writing character radicals...")
    exporter.writeCharacterRadicals(
        data = charactersWithWritingDataMap.values.flatMap { it.allRadicals }
    )

    println("Writing characters info data...")
    exporter.writeKanjiData(kanjiDataList = charactersWithInfo.values.toList())


    println("Writing classification data...")
    val hiragana = Hiragana.map {
        CharacterClass(it.toString(), CharactersClassification.Kana.Hiragana)
    }
    val katakana = Katakana.map {
        CharacterClass(it.toString(), CharactersClassification.Kana.Katakana)
    }
    val jlpt = JLPTLevels.flatMap { (clazz, chars) ->
        chars.map { CharacterClass(it, clazz) }
    }
    val grade = charactersWithInfo.values
        .filter { it.grade != null }
        .map { CharacterClass(it.kanji.toString(), CharactersClassification.Grade(it.grade!!)) }
    val wanikani = WanikaniLevels.flatMap { (clazz, chars) ->
        chars.map { CharacterClass(it, clazz) }
    }
    val allClassifications = hiragana + katakana + jlpt + grade + wanikani
    exporter.writeClassifications(allClassifications)

    println("Writing characters words data...")
    exporter.writeWords(words = words)
    println("Done")

}
