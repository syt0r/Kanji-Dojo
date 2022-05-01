package ua.syt0r.kanji.presentation.screen.screen.practice_create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.language.CharactersClassification
import ua.syt0r.kanji.core.language.hiragana
import ua.syt0r.kanji.core.language.katakana
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.EnteredKanji
import ua.syt0r.kanji_db_model.isHiragana
import ua.syt0r.kanji_db_model.isKana
import ua.syt0r.kanji_db_model.isKanji
import ua.syt0r.kanji_db_model.isKatakana
import javax.inject.Inject

@HiltViewModel
class CreateWritingPracticeViewModel @Inject constructor(
    private val kanjiDataRepository: KanjiDataContract.Repository,
    private val practiceRepository: UserDataContract.PracticeRepository
) : ViewModel(), CreateWritingPracticeScreenContract.ViewModel {

    lateinit var configuration: CreatePracticeConfiguration

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun initialize(configuration: CreatePracticeConfiguration) {
        if (!this::configuration.isInitialized) {
            this.configuration = configuration

            viewModelScope.launch {

                val (practiceTitle: String?, data: Set<EnteredKanji>) = withContext(Dispatchers.IO) {
                    when (configuration) {

                        is CreatePracticeConfiguration.NewPractice -> null to emptySet()

                        is CreatePracticeConfiguration.EditExisting -> {
                            val practice = practiceRepository
                                .getPracticeInfo(configuration.practiceId)
                            practice.name to practiceRepository.getKanjiForPracticeSet(configuration.practiceId)
                                .map { EnteredKanji(it, true) }
                                .toSet()
                        }
                        is CreatePracticeConfiguration.Import -> {

                            configuration.title to when (configuration.classification) {
                                CharactersClassification.Kana.HIRAGANA -> {
                                    hiragana.map { EnteredKanji(it.toString(), true) }.toSet()
                                }
                                CharactersClassification.Kana.KATAKANA -> {
                                    katakana.map { EnteredKanji(it.toString(), true) }.toSet()
                                }
                                else -> {
                                    TODO()
                                }
                            }

                        }
                    }
                }

                state.value = ScreenState.Loaded(
                    initialPracticeTitle = practiceTitle,
                    data = data,
                    currentDataAction = DataAction.Loaded
                )
            }

        }
    }

    override fun submitUserInput(input: String) {
        viewModelScope.launch {
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.ProcessingInput)

            val newData = withContext(Dispatchers.IO) {
                val parsedCharacters = input.toCharArray()
                    .filter { it.isKanji() || it.isKana() }
                    .map { it.toString() }

                screenState.data + parsedCharacters.map { character ->
                    val strokes = kanjiDataRepository.getStrokes(character)
                    EnteredKanji(
                        kanji = character,
                        isKnown = strokes.isNotEmpty() && character.first().let {
                            when {
                                it.isHiragana() -> hiragana.contains(it)
                                it.isKatakana() -> katakana.contains(it)
                                it.isKanji() -> kanjiDataRepository.getReadings(character)
                                    .isNotEmpty()
                                else -> throw IllegalStateException()
                            }
                        }
                    )
                }.toSet()
            }

            state.value = screenState.copy(data = newData, currentDataAction = DataAction.Loaded)
        }
    }

    override fun removeCharacter(character: String) {

    }

    override fun savePractice(title: String) {
        viewModelScope.launch {
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.Saving)

            withContext(Dispatchers.IO) {

                practiceRepository.createPracticeSet(
                    kanjiList = screenState.data
                        .filter { it.isKnown }
                        .map { it.kanji },
                    setName = title
                )

            }

            state.value = screenState.copy(currentDataAction = DataAction.SaveCompleted)
        }
    }

    override fun deletePractice() {
        viewModelScope.launch {
            Logger.d("start")
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.Deleting)

            val practiceId = (configuration as CreatePracticeConfiguration.EditExisting).practiceId

            withContext(Dispatchers.IO) {
//                practiceRepository.deletePracticeSet(practiceId)
                delay(500)
            }

            state.value = screenState.copy(currentDataAction = DataAction.DeleteCompleted)
            Logger.d("end")
        }
    }

}