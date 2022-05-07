package ua.syt0r.kanji.presentation.screen.screen.practice_create.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeData
import ua.syt0r.kanji_dojo.shared.CharactersClassification
import ua.syt0r.kanji_dojo.shared.hiragana
import ua.syt0r.kanji_dojo.shared.katakana
import javax.inject.Inject

class LoadPracticeDataUseCase @Inject constructor(
    private val kanjiDataRepository: KanjiDataContract.Repository,
    private val userDataRepository: UserDataContract.PracticeRepository
) {

    suspend fun load(configuration: CreatePracticeConfiguration): CreatePracticeData {
        return when (configuration) {

            is CreatePracticeConfiguration.NewPractice -> {
                CreatePracticeData(
                    title = "",
                    characters = emptySet()
                )
            }

            is CreatePracticeConfiguration.EditExisting -> {
                val practice = userDataRepository.getPracticeInfo(configuration.practiceId)
                CreatePracticeData(
                    title = practice.name,
                    characters = userDataRepository.getKanjiForPractice(configuration.practiceId)
                        .toSet()
                )
            }

            is CreatePracticeConfiguration.Import -> {

                val characters = when (configuration.classification) {
                    CharactersClassification.Kana.HIRAGANA -> {
                        hiragana.map { it.toString() }
                    }
                    CharactersClassification.Kana.KATAKANA -> {
                        katakana.map { it.toString() }
                    }
                    is CharactersClassification.JLPT -> {
                        kanjiDataRepository.getKanjiByJLPT(configuration.classification)
                    }
                    else -> throw IllegalStateException("Unsupported import")
                }

                CreatePracticeData(
                    title = configuration.title,
                    characters = characters.toSet()
                )
            }
        }
    }

}