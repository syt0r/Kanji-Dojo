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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import ua.syt0r.kanji.core.AccountManager
import ua.syt0r.kanji.core.ApiTextAnalysisRequest
import ua.syt0r.kanji.core.NetworkApi
import ua.syt0r.kanji.core.launcherLambda
import ua.syt0r.kanji.core.user_data.database.TextAnalysisData
import ua.syt0r.kanji.core.user_data.database.TextAnalysisRepository
import ua.syt0r.kanji.presentation.common.paginateable
import ua.syt0r.kanji.presentation.screen.main.screen.text_analysis.TextAnalysisContract.ScreenState

@OptIn(ExperimentalCoroutinesApi::class)
class TextAnalysisViewModel(
    private val viewModelScope: CoroutineScope,
    private val accountManager: AccountManager,
    private val repository: TextAnalysisRepository,
    private val networkApi: NetworkApi
) {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state

    private val parser = TextAnalysisParser()

    init {

        viewModelScope.launch {

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

            _state.value = ScreenState.Loaded(
                contentState = contentState,
                inputState = inputState,
                history = historyState,
                setContent = { displayedResult.value = it }
            )

        }

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
                    message = it.message
                        ?: "Generic error message"//getString(Res.string.sponsor_recent_donations_error)
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

        val toggleContentModeRequestsChannel = Channel<Unit>()

        val browseContentMode = TextAnalysisContentMode.Browse(
            furigana = mutableStateOf(true),
            highlight = mutableStateOf(true),
            switchToSaveWordsMode = launcherLambda { toggleContentModeRequestsChannel.send(Unit) }
        )

        val contentMode = mutableStateOf<TextAnalysisContentMode>(browseContentMode)

        val state = TextAnalysisContentState.Loaded(
            contentMode = contentMode,
            result = result
        )
        send(state)

        toggleContentModeRequestsChannel.consumeAsFlow().collect {
            contentMode.value = when (contentMode.value) {
                is TextAnalysisContentMode.Browse -> {
                    val elements = result
                        .let { it as TextAnalysisResult.Success }
                        .nodeList
                    createSaveWordsContentMode(
                        elements = elements,
                        switchToBrowseMode = launcherLambda {
                            toggleContentModeRequestsChannel.send(Unit)
                        }
                    )
                }

                is TextAnalysisContentMode.SaveWords -> browseContentMode
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
            TextAnalysisResult.Success(
                text = it.text,
                translation = it.translation,
                nodeList = parser.parseIchiranJson(
                    Json.Default.decodeFromString(it.annotatedTextJson)
                )
            )
        }
    }

}
