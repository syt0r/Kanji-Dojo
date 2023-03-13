package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

typealias RepoPracticeType = ua.syt0r.kanji.core.user_data.model.PracticeType
typealias RepoFilterOption = ua.syt0r.kanji.core.user_data.model.FilterOption
typealias RepoSortOption = ua.syt0r.kanji.core.user_data.model.SortOption

data class PracticePreviewScreenConfiguration(
    val practiceType: PracticeType = PracticeType.Writing,
    val filterOption: FilterOption = FilterOption.All,
    val sortOption: SortOption = SortOption.ADD_ORDER,
    val isDescending: Boolean = false
)

enum class PracticeType(
    val titleResolver: StringResolveScope<String>,
    val correspondingRepoType: RepoPracticeType
) {
    Writing(
        titleResolver = { practicePreview.practiceTypeWriting },
        correspondingRepoType = RepoPracticeType.Writing
    ),
    Reading(
        titleResolver = { practicePreview.practiceTypeReading },
        correspondingRepoType = RepoPracticeType.Reading
    )
}

fun RepoPracticeType.toScreenType() =
    PracticeType.values().find { it.correspondingRepoType == this }!!

enum class FilterOption(
    val titleResolver: StringResolveScope<String>,
    val correspondingRepoType: RepoFilterOption
) {
    All(
        titleResolver = { practicePreview.filterAll },
        correspondingRepoType = RepoFilterOption.All
    ),
    ReviewOnly(
        titleResolver = { practicePreview.filterReviewOnly },
        correspondingRepoType = RepoFilterOption.ReviewOnly
    ),
    NewOnly(
        titleResolver = { practicePreview.filterNewOnly },
        correspondingRepoType = RepoFilterOption.NewOnly
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
    )
}

fun RepoSortOption.toScreenType() = SortOption.values().find { it.correspondingRepoType == this }!!
