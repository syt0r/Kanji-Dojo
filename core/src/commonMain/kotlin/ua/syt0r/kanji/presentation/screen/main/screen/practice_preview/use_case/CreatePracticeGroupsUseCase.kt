package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.common.hiraganaToKatakana
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupSummary
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupsCreationResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType

class CreatePracticeGroupsUseCase : PracticePreviewScreenContract.CreatePracticeGroupsUseCase {

    companion object {
        private const val CharactersInGroup = 6

        private val HiraganaBaseGroups = listOf(
            "あいうえお",
            "かきくけこ",
            "さしすせそ",
            "たちつてと",
            "なにぬねの",
            "はひふへほ",
            "まみむめも",
            "らりるれろ",
            "やゆよ",
            "わをん"
        )

        private val HiraganaFullGroups = HiraganaBaseGroups + listOf(
            "がぎぐげご",
            "ざじずぜぞ",
            "だぢづでど",
            "ばびぶべぼ",
            "ぱぴぷぺぽ"
        )

        private val KatakanaBaseGroups = HiraganaBaseGroups.groupsToKatakana()
        private val KatakanaFullGroups = HiraganaFullGroups.groupsToKatakana()

        private fun List<String>.groupsToKatakana(): List<String> = map { groupString ->
            groupString.toCharArray().joinToString("") { hiraganaToKatakana(it).toString() }
        }

    }

    override fun create(
        items: List<PracticePreviewItem>,
        visibleItems: List<PracticePreviewItem>,
        type: PracticeType,
        probeKanaGroups: Boolean
    ): PracticeGroupsCreationResult {

        val itemsMap = items.associateBy { it.character }
        val characters = itemsMap.keys

        val (chunkedGroups: List<List<PracticePreviewItem>>, kanaGroupsFound: Boolean) = when {
            !probeKanaGroups -> visibleItems.chunked(CharactersInGroup) to false

            HiraganaBaseGroups.areMatchingSets(characters) -> {
                HiraganaBaseGroups.associateGroupsWithItems(itemsMap) to true
            }

            HiraganaFullGroups.areMatchingSets(characters) -> {
                HiraganaFullGroups.associateGroupsWithItems(itemsMap) to true
            }

            KatakanaBaseGroups.areMatchingSets(characters) -> {
                KatakanaBaseGroups.associateGroupsWithItems(itemsMap) to true
            }

            KatakanaFullGroups.areMatchingSets(characters) -> {
                KatakanaFullGroups.associateGroupsWithItems(itemsMap) to true
            }

            else -> visibleItems.chunked(CharactersInGroup) to false
        }

        return chunkedGroups.mapIndexed { index, groupItems ->
            val itemReviewStates = when (type) {
                PracticeType.Writing -> groupItems.map { it.writingSummary.state }
                PracticeType.Reading -> groupItems.map { it.readingSummary.state }
            }

            val groupReviewState = when {
                itemReviewStates.all { it == CharacterReviewState.RecentlyReviewed } -> CharacterReviewState.RecentlyReviewed
                itemReviewStates.any { it == CharacterReviewState.NeedReview } -> CharacterReviewState.NeedReview
                else -> CharacterReviewState.NeverReviewed
            }

            val summary = when (type) {
                PracticeType.Writing -> PracticeGroupSummary(
                    firstReviewDate = groupItems
                        .mapNotNull { it.writingSummary.firstReviewDate }
                        .minOrNull(),
                    lastReviewDate = groupItems
                        .mapNotNull { it.writingSummary.lastReviewDate }
                        .maxOrNull(),
                    state = groupReviewState
                )

                PracticeType.Reading -> PracticeGroupSummary(
                    firstReviewDate = groupItems
                        .mapNotNull { it.readingSummary.firstReviewDate }
                        .minOrNull(),
                    lastReviewDate = groupItems
                        .mapNotNull { it.readingSummary.lastReviewDate }
                        .maxOrNull(),
                    state = groupReviewState
                )
            }

            PracticeGroup(
                index = index + 1,
                items = groupItems,
                summary = summary,
                reviewState = groupReviewState
            )
        }.let { PracticeGroupsCreationResult(kanaGroupsFound, it) }
    }

    private fun List<String>.areMatchingSets(characters: Set<String>): Boolean {
        val set = flatMap { it.map { it.toString() } }.toSet()
        return set.size == characters.size && characters.intersect(set).size == set.size
    }

    private fun List<String>.associateGroupsWithItems(
        itemsMap: Map<String, PracticePreviewItem>
    ): List<List<PracticePreviewItem>> = map { groupString ->
        groupString.map { char -> itemsMap.getValue(char.toString()) }
    }

}
