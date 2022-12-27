package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeData
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.Hiragana
import ua.syt0r.kanji.common.Katakana
import javax.inject.Inject

class LoadPracticeDataUseCase @Inject constructor(
    private val kanjiDataRepository: KanjiDataRepository,
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
                        Hiragana.map { it.toString() }
                    }
                    CharactersClassification.Kana.KATAKANA -> {
                        Katakana.map { it.toString() }
                    }
                    is CharactersClassification.JLPT -> {
                        kanjiDataRepository.getKanjiByJLPT(configuration.classification)
                    }
                    is CharactersClassification.Grade -> {
                        kanjiDataRepository.getKanjiByGrade(configuration.classification)
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