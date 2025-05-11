package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import ua.syt0r.kanji.core.app_data.data.VocabReading


sealed interface TextAnalysisInputState {

    object NotEligible : TextAnalysisInputState

    data class Typing(
        val input: MutableState<String>,
        val isInputValid: State<Boolean>,
        val submit: () -> Unit
    ) : TextAnalysisInputState

    data class Loading(
        val input: String
    ) : TextAnalysisInputState

}

sealed interface TextAnalysisContentState {

    object Empty : TextAnalysisContentState

    data class Loaded(
        val contentMode: State<TextAnalysisContentMode>,
        val result: TextAnalysisResult,
    ) : TextAnalysisContentState

}

sealed interface TextAnalysisContentMode {

    sealed interface WordsDisplay

    data class Browse(
        val furigana: MutableState<Boolean>,
        val highlight: MutableState<Boolean>,
        val switchToSaveWordsMode: () -> Unit
    ) : TextAnalysisContentMode, WordsDisplay

    data class SaveWords(
        val selected: State<Set<TextAnalysisNode.Word>>,
        val toggleSelection: (TextAnalysisNode.Word) -> Unit,
        val selectAll: () -> Unit,
        val selectNone: () -> Unit,
        val switchToBrowseMode: () -> Unit
    ) : TextAnalysisContentMode, WordsDisplay

    data class SaveLetters(
        val letters: List<String>,
        val selected: State<Set<String>>,
        val toggleSelection: (String) -> Unit,
        val selectAll: () -> Unit,
        val selectNone: () -> Unit,
        val selectAllKanji: () -> Unit,
        val switchToBrowseMode: () -> Unit
    ) : TextAnalysisContentMode

}

sealed interface TextAnalysisResult {

    data class Success(
        val text: String,
        val translation: String,
        val nodeList: List<TextAnalysisNode>
    ) : TextAnalysisResult

    data class Error(
        val message: String
    ) : TextAnalysisResult

}

sealed interface TextAnalysisNode {

    data class Text(
        val value: String
    ) : TextAnalysisNode

    data class Word(
        val sequence: Long?,
        val text: String,
        val reading: VocabReading,
        val dictionaryReading: VocabReading?,
        val glossary: List<Glossary>,
        val combinedPartOfSpeechList: List<PartOfSpeech>,
        val highlightPartOfSpeech: PartOfSpeech?
    ) : TextAnalysisNode

    data class Compound(
        val words: List<TextAnalysisNode>
    ) : TextAnalysisNode

    data class Error(
        val text: String?
    ) : TextAnalysisNode

    data class AlternativeGroup(
        val nodeList: List<TextAnalysisNode>
    ) : TextAnalysisNode

    data class Glossary(
        val definition: String,
        val partOfSpeech: Set<PartOfSpeech> = emptySet()
    )

    enum class PartOfSpeech(regexPattern: String) {
        Noun("n"),
        Verb("v.*"),
        Adj("adj.*"),
        Prt("prt"),
        Suf("suf"),
        Exp("exp");

        val regex = Regex(regexPattern)
    }

}