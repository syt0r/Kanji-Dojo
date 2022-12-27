package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case

import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import javax.inject.Inject

class SavePracticeUseCase @Inject constructor(
    private val repository: UserDataContract.PracticeRepository
) {

    suspend fun save(
        configuration: CreatePracticeConfiguration,
        title: String,
        state: ScreenState.Loaded
    ) {
        Logger.logMethod()
        when (configuration) {
            is CreatePracticeConfiguration.EditExisting -> {
                repository.updatePractice(
                    id = configuration.practiceId,
                    title = title,
                    charactersToAdd = state.characters.toList(),
                    charactersToRemove = state.charactersPendingForRemoval.toList()
                )
            }
            else -> {
                repository.createPractice(
                    characters = state.characters.toList(),
                    title = title
                )
            }
        }
    }

}