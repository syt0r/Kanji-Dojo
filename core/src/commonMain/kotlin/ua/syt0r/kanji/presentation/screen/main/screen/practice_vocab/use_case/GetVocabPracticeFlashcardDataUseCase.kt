package ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.use_case

import ua.syt0r.kanji.core.VocabCardResolver
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.formattedVocabReading
import ua.syt0r.kanji.core.app_data.data.toFurigana
import ua.syt0r.kanji.core.app_data.data.withEmptyFurigana
import ua.syt0r.kanji.core.toInfoScreenData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabExampleSentence
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeItemData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeQueueItemDescriptor

interface GetVocabPracticeFlashcardDataUseCase {
    suspend operator fun invoke(
        descriptor: VocabPracticeQueueItemDescriptor.Flashcard
    ): VocabPracticeItemData.Flashcard
}

class DefaultGetVocabPracticeFlashcardDataUseCase(
    private val vocabCardResolver: VocabCardResolver,
    private val appDataRepository: AppDataRepository
) : GetVocabPracticeFlashcardDataUseCase {

    override suspend fun invoke(
        descriptor: VocabPracticeQueueItemDescriptor.Flashcard
    ): VocabPracticeItemData.Flashcard {
        val card = vocabCardResolver.resolveUserCard(descriptor.cardId)
        val revealedReading = card.run {
            formattedVocabReading(kanaReading, kanjiReading, furigana)
        }
        val hiddenReading = card.furigana?.withEmptyFurigana()
            ?: card.kanjiReading?.toFurigana()
            ?: card.kanaReading.toFurigana()

        val sentence = appDataRepository.getSentencesWithText(
            text = card.kanjiReading ?: card.kanaReading,
            limit = 1
        ).firstOrNull()

        return VocabPracticeItemData.Flashcard(
            reading = revealedReading,
            hiddenReading = hiddenReading,
            meaning = card.meaning,
            exampleSentence = sentence?.let {
                VocabExampleSentence(
                    text = it.value,
                    translation = it.translation
                )
            },
            showMeaningInFront = descriptor.translationInFont,
            vocabReference = card.toInfoScreenData()
        )
    }

}