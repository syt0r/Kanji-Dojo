package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case

import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchListItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.SearchByRadicalsResult
import java.util.*

class SearchScreenSearchByRadicalsUseCase(
    private val appDataRepository: AppDataRepository,
) : SearchScreenContract.SearchByRadicalsUseCase {

    override suspend fun search(radicals: Set<String>): SearchByRadicalsResult {
        if (radicals.isEmpty()) return SearchByRadicalsResult(emptyList(), emptyList())

        val foundCharacters = radicals.toList()
            .let { appDataRepository.getCharactersWithRadicals(it) }

        val dataList = foundCharacters.map { it to appDataRepository.getStrokes(it).size }
            .groupBy { (_, strokesCount) -> strokesCount }
            .map { (strokesCount, listOfPairs) -> strokesCount to listOfPairs.map { it.first } }
            .sortedBy { (strokesCount, _) -> strokesCount }
            .flatMap { (strokesCount, characters) ->
                val listCharacters = characters.sortedBy { it }
                    .map { RadicalSearchListItem.Character(it, true) }
                LinkedList<RadicalSearchListItem>().apply {
                    add(RadicalSearchListItem.StrokeGroup(strokesCount))
                    addAll(listCharacters)
                }
            }

        return SearchByRadicalsResult(foundCharacters, dataList)
    }

}