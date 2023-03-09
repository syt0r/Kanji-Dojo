package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeData
import javax.inject.Inject

class LoadPracticeDataUseCase @Inject constructor(
    private val kanjiDataRepository: KanjiDataRepository,
    private val userDataRepository: UserDataContract.PracticeRepository
) {

    suspend fun load(configuration: MainDestination.CreatePractice): CreatePracticeData {
        return when (configuration) {

            is MainDestination.CreatePractice.New -> {
                CreatePracticeData(
                    title = "",
                    characters = emptySet()
                )
            }

            is MainDestination.CreatePractice.EditExisting -> {
                val practice = userDataRepository.getPracticeInfo(configuration.practiceId)
                CreatePracticeData(
                    title = practice.name,
                    characters = userDataRepository.getKanjiForPractice(configuration.practiceId)
                        .toSet()
                )
            }

            is MainDestination.CreatePractice.Import -> {
                val characters = kanjiDataRepository.getKanjiByClassification(
                    classification = configuration.classification
                )

                CreatePracticeData(
                    title = configuration.title,
                    characters = characters.toSet()
                )
            }

        }
    }

}