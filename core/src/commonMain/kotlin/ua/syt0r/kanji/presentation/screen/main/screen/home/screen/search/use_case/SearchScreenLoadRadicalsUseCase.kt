package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchListItem
import java.util.*

class SearchScreenLoadRadicalsUseCase(
    private val kanjiDataRepository: KanjiDataRepository,
) : SearchScreenContract.LoadRadicalsUseCase {

    override suspend fun load(): List<RadicalSearchListItem> {
        return kanjiDataRepository.getRadicals()
            .groupBy { it.strokesCount }
            .toList()
            .sortedBy { it.first }
            .flatMap { (strokes, radicalData) ->
                val listItems = radicalData
                    .map {
                        RadicalSearchListItem.Character(
                            character = it.radical,
                            isEnabled = true
                        )
                    }
                    .sortedBy { it.character }

                LinkedList<RadicalSearchListItem>().apply {
                    add(RadicalSearchListItem.StrokeGroup(strokes))
                    addAll(listItems)
                }
            }
    }

}