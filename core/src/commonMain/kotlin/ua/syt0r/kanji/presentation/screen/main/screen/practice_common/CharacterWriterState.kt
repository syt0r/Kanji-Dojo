package ua.syt0r.kanji.presentation.screen.main.screen.practice_common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.launchUnit
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import kotlin.math.max


interface CharacterWriterState {

    val character: String
    val strokes: List<Path>
    val configuration: CharacterWriterConfiguration
    val content: State<CharacterWriterContent>
    val progress: State<CharacterWritingProgress>

    fun submit(inputData: CharacterInputData)
    fun toggleAnimationState()

}

sealed interface CharacterWriterConfiguration {

    data class StrokeInput(
        val isStudyMode: Boolean
    ) : CharacterWriterConfiguration

    object CharacterInput : CharacterWriterConfiguration

}

sealed interface CharacterInputData {

    data class MultipleStrokes(
        val characterStrokes: List<Path>,
        val inputStrokes: List<Path>
    ) : CharacterInputData

    data class SingleStroke(
        val userPath: Path,
        val kanjiPath: Path
    ) : CharacterInputData
}

sealed interface StrokeProcessingResult {

    data class Correct(
        val userPath: Path,
        val kanjiPath: Path
    ) : StrokeProcessingResult

    data class Mistake(
        val hintStroke: Path
    ) : StrokeProcessingResult

}

sealed interface CharacterWriterContent {

    interface SingleStrokeInput : CharacterWriterContent {

        val isStudyMode: Boolean
        val drawnStrokesCount: State<Int>
        val currentStrokeMistakes: State<Int>
        val totalMistakes: State<Int>
        val hintClicksSharedFlow: SharedFlow<Unit>
        val inputProcessingResults: SharedFlow<StrokeProcessingResult>

        suspend fun notifyHintClick()
        fun skipRemainingStrokes()

    }

    sealed interface MultipleStrokeInput : CharacterWriterContent {

        data class Writing(
            val strokes: MutableState<List<Path>>
        ) : MultipleStrokeInput

        data class Processing(
            val strokes: List<Path>
        ) : MultipleStrokeInput

        data class Processed(
            val strokeProcessingResults: List<StrokeProcessingResult>,
            val mistakes: Int,
            val completedAnimation: MutableState<Boolean>
        ) : MultipleStrokeInput

    }

    data class Animation(
        val previousState: CharacterWriterContent
    ) : CharacterWriterContent

}

private data class MutableSingleStrokeInputWriterContent(
    override val isStudyMode: Boolean,
    private val totalStrokesCount: Int,
    override val drawnStrokesCount: MutableState<Int>,
    override val currentStrokeMistakes: MutableState<Int>,
    override val totalMistakes: MutableState<Int>,
    override val hintClicksSharedFlow: MutableSharedFlow<Unit>,
    override val inputProcessingResults: MutableSharedFlow<StrokeProcessingResult>
) : CharacterWriterContent.SingleStrokeInput {

    override suspend fun notifyHintClick() {
        currentStrokeMistakes.value += 1
        totalMistakes.value += 1
        hintClicksSharedFlow.emit(Unit)
    }

    override fun skipRemainingStrokes() {
        totalMistakes.value += (totalStrokesCount - drawnStrokesCount.value)
        drawnStrokesCount.value = totalStrokesCount
    }

}

sealed interface CharacterWritingProgress {

    data object Writing : CharacterWritingProgress

    sealed interface Completed : CharacterWritingProgress {
        val isCorrect: Boolean
        val mistakes: Int

        data class Idle(
            override val isCorrect: Boolean,
            override val mistakes: Int
        ) : Completed

        data class Animating(
            override val isCorrect: Boolean,
            override val mistakes: Int
        ) : Completed
    }

}

class DefaultCharacterWriterState(
    private val coroutineScope: CoroutineScope,
    private val strokeEvaluator: KanjiStrokeEvaluator,
    override val character: String,
    override val strokes: List<Path>,
    override val configuration: CharacterWriterConfiguration
) : CharacterWriterState {

    private val _content = mutableStateOf(createInputState())
    override val content: State<CharacterWriterContent> = _content

    override val progress: State<CharacterWritingProgress> = derivedStateOf {
        content.value.toWritingProgress()
    }

    override fun submit(inputData: CharacterInputData) = coroutineScope.launchUnit {
        when (inputData) {
            is CharacterInputData.SingleStroke -> {
                handleSingleStrokeInput(inputData)
            }

            is CharacterInputData.MultipleStrokes -> {
                handleMultipleStrokeInput(inputData)
            }
        }
    }

    override fun toggleAnimationState() {
        val currentContent = content.value
        _content.value = when (currentContent) {
            is CharacterWriterContent.Animation -> currentContent.previousState
            else -> CharacterWriterContent.Animation(currentContent)
        }
    }

    private fun createInputState(): CharacterWriterContent {
        return when (configuration) {
            is CharacterWriterConfiguration.StrokeInput -> {
                MutableSingleStrokeInputWriterContent(
                    isStudyMode = configuration.isStudyMode,
                    totalStrokesCount = strokes.size,
                    drawnStrokesCount = mutableStateOf(0),
                    currentStrokeMistakes = mutableStateOf(0),
                    totalMistakes = mutableStateOf(0),
                    hintClicksSharedFlow = MutableSharedFlow(),
                    inputProcessingResults = MutableSharedFlow()
                )
            }

            CharacterWriterConfiguration.CharacterInput -> {
                CharacterWriterContent.MultipleStrokeInput.Writing(
                    strokes = mutableStateOf(emptyList())
                )
            }
        }
    }

    private suspend fun handleSingleStrokeInput(
        inputData: CharacterInputData.SingleStroke,
    ) {
        val mutableState = content.value as MutableSingleStrokeInputWriterContent

        val isDrawnCorrectly = withContext(Dispatchers.IO) {
            strokeEvaluator.areStrokesSimilar(inputData.kanjiPath, inputData.userPath)
        }
        val result = if (isDrawnCorrectly) {
            mutableState.drawnStrokesCount.value += 1
            StrokeProcessingResult.Correct(
                userPath = inputData.userPath,
                kanjiPath = inputData.kanjiPath
            )
        } else {
            val currentStrokeMistakes = mutableState.run {
                currentStrokeMistakes.value += 1
                totalMistakes.value += 1
                currentStrokeMistakes.value
            }
            val path = when {
                currentStrokeMistakes > 2 -> inputData.kanjiPath
                else -> inputData.userPath
            }
            StrokeProcessingResult.Mistake(path)
        }
        mutableState.inputProcessingResults.emit(result)
    }

    private suspend fun handleMultipleStrokeInput(
        inputData: CharacterInputData.MultipleStrokes
    ) {
        _content.value = CharacterWriterContent.MultipleStrokeInput.Processing(
            strokes = inputData.inputStrokes
        )

        val processedState = withContext(Dispatchers.IO) {
            val strokesCount = max(inputData.characterStrokes.size, inputData.inputStrokes.size)
            val results = (0 until strokesCount).map { index ->
                val input = inputData.inputStrokes.getOrNull(index)
                val stroke = inputData.characterStrokes.getOrNull(index)

                if (input == null || stroke == null) {
                    StrokeProcessingResult.Mistake(
                        hintStroke = input ?: stroke!!
                    )
                } else {
                    val similar = strokeEvaluator.areStrokesSimilar(stroke, input)
                    if (similar) {
                        StrokeProcessingResult.Correct(input, stroke)
                    } else {
                        StrokeProcessingResult.Mistake(hintStroke = stroke)
                    }
                }
            }

            CharacterWriterContent.MultipleStrokeInput.Processed(
                strokeProcessingResults = results.take(inputData.characterStrokes.size),
                mistakes = results.count { it is StrokeProcessingResult.Mistake },
                completedAnimation = mutableStateOf(false)
            )
        }

        _content.value = processedState
    }

    private fun CharacterWriterContent.toWritingProgress(): CharacterWritingProgress {
        return when (this) {
            is CharacterWriterContent.SingleStrokeInput -> {
                when (drawnStrokesCount.value == strokes.size) {
                    false -> CharacterWritingProgress.Writing
                    true -> {
                        val mistakes = totalMistakes.value
                        CharacterWritingProgress.Completed.Idle(
                            isCorrect = isResultCorrect(mistakes),
                            mistakes = mistakes
                        )
                    }
                }
            }

            is CharacterWriterContent.MultipleStrokeInput.Writing,
            is CharacterWriterContent.MultipleStrokeInput.Processing -> {
                CharacterWritingProgress.Writing
            }

            is CharacterWriterContent.MultipleStrokeInput.Processed -> {
                CharacterWritingProgress.Completed.Idle(
                    isCorrect = isResultCorrect(mistakes),
                    mistakes = mistakes
                )
            }

            is CharacterWriterContent.Animation -> {
                val completedProgress = previousState.toWritingProgress()
                completedProgress as CharacterWritingProgress.Completed
                CharacterWritingProgress.Completed.Animating(
                    isCorrect = completedProgress.isCorrect,
                    mistakes = completedProgress.mistakes
                )
            }
        }
    }

    private fun isResultCorrect(characterMistakes: Int): Boolean {
        return when (strokes.size) {
            1 -> characterMistakes == 0
            2, 3 -> characterMistakes < 2
            else -> characterMistakes <= 2
        }
    }

}
