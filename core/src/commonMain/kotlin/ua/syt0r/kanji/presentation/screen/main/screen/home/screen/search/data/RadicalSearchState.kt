package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data

import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import java.util.*
import kotlin.random.Random

data class RadicalSearchState(
    val radicalsListItems: List<RadicalSearchListItem>,
    val characterListItems: List<RadicalSearchListItem>,
    val isLoading: Boolean
) {

    companion object {
        fun random(): RadicalSearchState {
            return RadicalSearchState(
                radicalsListItems = (1..100).map { PreviewKanji.randomKanji() }
                    .distinct()
                    .plus("AGGP1000")
                    .shuffled()
                    .chunked(10)
                    .flatMapIndexed { index: Int, strings: List<String> ->
                        LinkedList<RadicalSearchListItem>().apply {
                            add(RadicalSearchListItem.StrokeGroup(index + 1))
                            addAll(
                                strings.map {
                                    RadicalSearchListItem.Character(it, Random.nextBoolean())
                                }
                            )
                        }
                    },
                characterListItems = (1..100)
                    .map { PreviewKanji.randomKanji() }
                    .distinct()
                    .chunked(10)
                    .flatMapIndexed { index: Int, strings: List<String> ->
                        LinkedList<RadicalSearchListItem>().apply {
                            add(RadicalSearchListItem.StrokeGroup(index + 1))
                            addAll(strings.map { RadicalSearchListItem.Character(it, true) })
                        }
                    },
                isLoading = true
            )
        }
    }

}