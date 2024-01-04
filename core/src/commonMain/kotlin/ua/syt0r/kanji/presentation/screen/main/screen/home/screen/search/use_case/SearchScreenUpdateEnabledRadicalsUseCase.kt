package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case

import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchListItem

class SearchScreenUpdateEnabledRadicalsUseCase(
    private val appDataRepository: AppDataRepository,
) : SearchScreenContract.UpdateEnabledRadicalsUseCase {

    override suspend fun update(
        allRadicals: List<RadicalSearchListItem>,
        selectedRadicals: Set<String>
    ): List<RadicalSearchListItem> {
        val radicalsForSelectedCharacters = appDataRepository
            .getAllRadicalsInCharactersWithSelectedRadicals(selectedRadicals)
            .toSet()

        val updatedRadicals = allRadicals.map {
            when (it) {
                is RadicalSearchListItem.StrokeGroup -> it
                is RadicalSearchListItem.Character -> {
                    it.copy(
                        isEnabled = if (selectedRadicals.isEmpty()) true
                        else radicalsForSelectedCharacters.contains(it.character)
                    )
                }
            }
        }
        return updatedRadicals
    }


}