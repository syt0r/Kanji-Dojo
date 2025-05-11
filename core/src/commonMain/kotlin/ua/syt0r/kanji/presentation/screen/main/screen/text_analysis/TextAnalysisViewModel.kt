package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.AccountManager
import ua.syt0r.kanji.core.AccountState
import ua.syt0r.kanji.core.ApiTextAnalysisRequest
import ua.syt0r.kanji.core.NetworkApi
import ua.syt0r.kanji.core.japanese.isKanji
import ua.syt0r.kanji.core.launcherLambda
import ua.syt0r.kanji.core.user_data.database.TextAnalysisData
import ua.syt0r.kanji.core.user_data.database.TextAnalysisRepository
import ua.syt0r.kanji.error_unknown
import ua.syt0r.kanji.presentation.common.paginateable
import ua.syt0r.kanji.presentation.screen.main.screen.text_analysis.TextAnalysisContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.text_analysis.parser.IchiranParser
import ua.syt0r.kanji.text_analysis_preview_text
import ua.syt0r.kanji.text_analysis_preview_translation

@OptIn(ExperimentalCoroutinesApi::class)
class TextAnalysisViewModel(
    private val viewModelScope: CoroutineScope,
    private val accountManager: AccountManager,
    private val repository: TextAnalysisRepository,
    private val networkApi: NetworkApi
) {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state

    private val parser = IchiranParser()

    init {

        viewModelScope.launch {

            accountManager.state
                .onEach {
                    when (it) {
                        AccountState.Loading -> {
                            _state.value = ScreenState.Loading
                        }

                        AccountState.LoggedOut,
                        is AccountState.Error -> {
                            _state.value = ScreenState.Loading
                            _state.value = createPreviewState()
                        }

                        is AccountState.LoggedIn -> {
                            _state.value = ScreenState.Loading
                            _state.value = createNormalState()
                        }
                    }
                }
                .launchIn(this)

        }

    }

    private suspend fun createPreviewState(): ScreenState.Loaded {

        val inputState: StateFlow<TextAnalysisInputState> = MutableStateFlow(
            TextAnalysisInputState.NotEligible
        )

        val historyState = repository.changesFlow
            .onStart { emit(Unit) }
            .map { getLatestHistory() }
            .stateIn(viewModelScope)

        historyState.value.loadMoreBlocking()

        val displayedResultInitial = if (historyState.value.total != 0) {
            null
        } else {
            TextAnalysisResult.Success(
                text = getString(Res.string.text_analysis_preview_text),
                translation = getString(Res.string.text_analysis_preview_translation),
                nodeList = Res.readBytes(TextAnalysisContract.PREVIEW_RES_PATH)
                    .decodeToString()
                    .let { parser.parseIchiranJson(Json.decodeFromString(it)) }
            )
        }

        val displayedResult = MutableStateFlow<TextAnalysisResult?>(displayedResultInitial)

        val contentState: StateFlow<TextAnalysisContentState> = displayedResult
            .flatMapLatest { createContentStateFlow(it) }
            .stateIn(viewModelScope)

        return ScreenState.Loaded(
            contentState = contentState,
            inputState = inputState,
            history = historyState,
            setContent = { displayedResult.value = it }
        )
    }

    private suspend fun createNormalState(): ScreenState.Loaded {
        val displayedResult = MutableStateFlow<TextAnalysisResult?>(null)

        val contentState: StateFlow<TextAnalysisContentState> = displayedResult
            .flatMapLatest { createContentStateFlow(it) }
            .stateIn(viewModelScope)

        val requestsChannel = Channel<String>()

        val requestInputFlow = requestsChannel.consumeAsFlow().transform { input ->
            emit(TextAnalysisInputState.Loading(input))
            val analysisResult = analyzeText(input)
            displayedResult.emit(analysisResult)
            val resultInputState = when (analysisResult) {
                is TextAnalysisResult.Success -> createNewInputState(requestsChannel)
                is TextAnalysisResult.Error -> createNewInputState(requestsChannel, input)
            }
            emit(resultInputState)
        }

        val inputState: StateFlow<TextAnalysisInputState> = requestInputFlow
            .onStart { emit(createNewInputState(requestsChannel)) }
            .stateIn(viewModelScope)

        val historyState = repository.changesFlow
            .onStart { emit(Unit) }
            .map { getLatestHistory() }
            .stateIn(viewModelScope)

        return ScreenState.Loaded(
            contentState = contentState,
            inputState = inputState,
            history = historyState,
            setContent = { displayedResult.value = it }
        )
    }

    private suspend fun analyzeText(input: String): TextAnalysisResult {
        val request = ApiTextAnalysisRequest(text = input)
        return networkApi.postTextAnalysisRequest(request)
            .map {
                val textAnalysisData = TextAnalysisData(
                    text = input,
                    timestamp = Clock.System.now(),
                    translation = it.value.translation,
                    annotatedTextJson = Json.Default.encodeToString(it.value.elements)
                )

                val result = TextAnalysisResult.Success(
                    text = input,
                    translation = it.value.translation,
                    nodeList = parser.parseIchiranJson(it.value.elements)
                )

                repository.add(textAnalysisData)
                result
            }
            .getOrElse {
                TextAnalysisResult.Error(
                    message = it.message ?: getString(Res.string.error_unknown)
                )
            }
    }

    private fun createContentStateFlow(
        result: TextAnalysisResult?
    ) = channelFlow<TextAnalysisContentState> {

        if (result == null) {
            send(TextAnalysisContentState.Empty)
            return@channelFlow
        }

        val changeContentModeRequestsChannel = Channel<String>()

        val browseContentMode = TextAnalysisContentMode.Browse(
            furigana = mutableStateOf(true),
            highlight = mutableStateOf(true),
            switchToSaveWordsMode = launcherLambda {
                val name = TextAnalysisContentMode.SaveWords::class.simpleName!!
                changeContentModeRequestsChannel.send(name)
            }
        )

        val contentMode = mutableStateOf<TextAnalysisContentMode>(browseContentMode)

        val state = TextAnalysisContentState.Loaded(
            contentMode = contentMode,
            result = result
        )
        send(state)

        changeContentModeRequestsChannel.consumeAsFlow().collect { className ->
            contentMode.value = when (className) {
                TextAnalysisContentMode.Browse::class.simpleName -> browseContentMode
                TextAnalysisContentMode.SaveWords::class.simpleName -> {
                    val elements = result
                        .let { it as TextAnalysisResult.Success }
                        .nodeList
                    createSaveWordsContentMode(
                        elements = elements,
                        switchToBrowseMode = launcherLambda {
                            val name = TextAnalysisContentMode.Browse::class.simpleName!!
                            changeContentModeRequestsChannel.send(name)
                        }
                    )
                }

                TextAnalysisContentMode.SaveLetters::class.simpleName -> {
                    val letters = result
                        .let { it as TextAnalysisResult.Success }
                        .text
                        .map { it.toString() }
                    createSaveLettersContentMode(
                        letters = letters,
                        switchToBrowseMode = launcherLambda {
                            val name = TextAnalysisContentMode.Browse::class.simpleName!!
                            changeContentModeRequestsChannel.send(name)
                        }
                    )
                }

                else -> error("Invalid content mode value[$className]")
            }

        }

    }

    private fun createSaveWordsContentMode(
        elements: List<TextAnalysisNode>,
        switchToBrowseMode: () -> Unit
    ): TextAnalysisContentMode.SaveWords {
        val selectedWords = mutableStateOf(emptySet<TextAnalysisNode.Word>())
        return TextAnalysisContentMode.SaveWords(
            selected = selectedWords,
            toggleSelection = { word ->
                selectedWords.value = selectedWords.value.let {
                    if (it.contains(word)) it.minus(word)
                    else it.plus(word)
                }
            },
            selectAll = {
                selectedWords.value = elements
                    .filterIsInstance<TextAnalysisNode.Word>()
                    .toSet()
            },
            selectNone = {
                selectedWords.value = emptySet()
            },
            switchToBrowseMode = switchToBrowseMode
        )
    }

    private fun createSaveLettersContentMode(
        letters: List<String>,
        switchToBrowseMode: () -> Unit
    ): TextAnalysisContentMode.SaveLetters {
        val lettersSet = letters.toSet()
        val selected = mutableStateOf(emptySet<String>())

        return TextAnalysisContentMode.SaveLetters(
            letters = letters,
            selected = selected,
            toggleSelection = { word ->
                selected.value = selected.value.let {
                    if (it.contains(word)) it.minus(word)
                    else it.plus(word)
                }
            },
            selectAll = { selected.value = lettersSet },
            selectNone = { selected.value = emptySet() },
            selectAllKanji = {
                selected.value = lettersSet.asSequence()
                    .filter { it.first().isKanji() }
                    .map { it.toString() }
                    .toSet()

            },
            switchToBrowseMode = switchToBrowseMode
        )
    }

    private fun createNewInputState(
        submitRequestsChannel: Channel<String>,
        input: String = ""
    ): TextAnalysisInputState {
        val input = mutableStateOf(input)
        return TextAnalysisInputState.Typing(
            input = input,
            isInputValid = derivedStateOf {
                input.value.length <= TextAnalysisContract.INPUT_LIMIT
            },
            submit = { viewModelScope.launch { submitRequestsChannel.send(input.value) } }
        )
    }

    private suspend fun getLatestHistory() = paginateable(
        coroutineScope = viewModelScope,
        limit = repository.getCount().toInt()
    ) { offset ->
        repository.get(
            offset = offset.toLong(),
            limit = 10
        ).map {
            val nodeList = parser.parseIchiranJson(
                Json.Default.decodeFromString(it.annotatedTextJson)
            )
            TextAnalysisResult.Success(
                text = it.text,
                translation = it.translation,
                nodeList = nodeList
            )
        }
    }

}
