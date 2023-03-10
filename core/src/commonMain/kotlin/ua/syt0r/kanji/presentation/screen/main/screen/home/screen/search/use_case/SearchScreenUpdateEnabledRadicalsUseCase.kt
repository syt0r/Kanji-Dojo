package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchListItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.SearchByRadicalsResult

class SearchScreenUpdateEnabledRadicalsUseCase(
    private val kanjiDataRepository: KanjiDataRepository,
) : SearchScreenContract.UpdateEnabledRadicalsUseCase {

    override suspend fun update(
        allRadicals: List<RadicalSearchListItem>,
        searchResult: SearchByRadicalsResult
    ): List<RadicalSearchListItem> {
        val radicalsForSelectedCharacters = kanjiDataRepository
            .getAllRadicalsInCharacters(searchResult.characters)
            .toSet()

        val updatedRadicals = allRadicals.map {
            when (it) {
                is RadicalSearchListItem.StrokeGroup -> it
                is RadicalSearchListItem.Character -> {
                    it.copy(
                        isEnabled = if (searchResult.characters.isEmpty()) true
                        else radicalsForSelectedCharacters.contains(it.character)
                    )
                }
            }
        }
        return updatedRadicals
    }


}