package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.ui.graphics.vector.ImageVector
import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

typealias RepoPracticeType = ua.syt0r.kanji.core.user_data.model.PracticeType
typealias RepoFilterOption = ua.syt0r.kanji.core.user_data.model.FilterOption
typealias RepoSortOption = ua.syt0r.kanji.core.user_data.model.SortOption
typealias RepoLayout = ua.syt0r.kanji.core.user_data.model.PracticePreviewLayout

data class PracticePreviewScreenConfiguration(
    val practiceType: PracticeType = PracticeType.Writing,
    val filterOption: FilterOption = FilterOption.All,
    val sortOption: SortOption = SortOption.ADD_ORDER,
    val isDescending: Boolean = false,
    val layout: PracticePreviewLayout = PracticePreviewLayout.Groups,
    val kanaGroups: Boolean = true,
)

enum class PracticeType(
    val titleResolver: StringResolveScope<String>,
    val correspondingRepoType: RepoPracticeType,
    val imageVector: ImageVector
) {
    Writing(
        titleResolver = { practicePreview.practiceTypeWriting },
        correspondingRepoType = RepoPracticeType.Writing,
        imageVector = Icons.Default.Draw
    ),
    Reading(
        titleResolver = { practicePreview.practiceTypeReading },
        correspondingRepoType = RepoPracticeType.Reading,
        imageVector = Icons.Default.LocalLibrary
    )
}

fun RepoPracticeType.toScreenType() =
    PracticeType.values().find { it.correspondingRepoType == this }!!

enum class FilterOption(
    val titleResolver: StringResolveScope<String>,
    val correspondingRepoType: RepoFilterOption,
    val imageVector: ImageVector = Icons.Default.FilterAlt
) {
    All(
        titleResolver = { practicePreview.filterAll },
        correspondingRepoType = RepoFilterOption.All,
        imageVector = Icons.Default.Apps
    ),
    ReviewOnly(
        titleResolver = { practicePreview.filterReviewOnly },
        correspondingRepoType = RepoFilterOption.ReviewOnly,
        imageVector = Icons.Default.AccessTimeFilled
    ),
    NewOnly(
        titleResolver = { practicePreview.filterNewOnly },
        correspondingRepoType = RepoFilterOption.NewOnly,
        imageVector = Icons.Default.TipsAndUpdates
    )
}

fun RepoFilterOption.toScreenType() =
    FilterOption.values().find { it.correspondingRepoType == this }!!

enum class SortOption(
    val titleResolver: StringResolveScope<String>,
    val hintResolver: StringResolveScope<String>,
    val correspondingRepoType: RepoSortOption
) {
    ADD_ORDER(
        titleResolver = { practicePreview.sortOptionAddOrder },
        hintResolver = { practicePreview.sortOptionAddOrderHint },
        correspondingRepoType = RepoSortOption.AddOrder
    ),
    FREQUENCY(
        titleResolver = { practicePreview.sortOptionFrequency },
        hintResolver = { practicePreview.sortOptionFrequencyHint },
        correspondingRepoType = RepoSortOption.Frequency
    ),
    NAME(
        titleResolver = { practicePreview.sortOptionName },
        hintResolver = { practicePreview.sortOptionNameHint },
        correspondingRepoType = RepoSortOption.Name
    );

    val imageVector = Icons.Default.ArrowForward
}

fun RepoSortOption.toScreenType() = SortOption.values().find { it.correspondingRepoType == this }!!


enum class PracticePreviewLayout(
    val titleResolver: StringResolveScope<String>,
    val correspondingRepoType: RepoLayout
) {
    SingleCharacter(
        titleResolver = { "Single Character" },
        correspondingRepoType = RepoLayout.Character
    ),
    Groups(
        titleResolver = { "Groups" },
        correspondingRepoType = RepoLayout.Groups
    )
}

fun RepoLayout.toScreenType() = PracticePreviewLayout.values()
    .find { it.correspondingRepoType == this }!!
