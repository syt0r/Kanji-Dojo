package ua.syt0r.kanji.core.user_data

import kotlinx.datetime.LocalTime
import ua.syt0r.kanji.core.suspended_property.SuspendedProperty
import ua.syt0r.kanji.core.suspended_property.SuspendedPropertyRegistry
import ua.syt0r.kanji.core.suspended_property.createEnumProperty
import ua.syt0r.kanji.core.suspended_property.createLocalTimeProperty
import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.PracticePreviewLayout
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption
import ua.syt0r.kanji.core.user_data.model.SupportedTheme

class DefaultUserPreferencesRepository(
    private val registry: SuspendedPropertyRegistry
) : UserPreferencesRepository, SuspendedPropertyRegistry by registry {

    companion object {

        private const val analyticsEnabledKey = "analytics_enabled"
        private const val practiceTypeKey = "practice_type"
        private const val filterOptionKey = "filter_option"
        private const val sortOptionKey = "sort_option"
        private const val isSortDescendingKey = "is_desc"
        private const val themeKey = "theme"
        private const val dailyLimitEnabledKey = "daily_limit_enabled"
        private const val dailyLearnLimitKey = "daily_learn_limit"
        private const val dailyReviewLimitKey = "daily_review_limit"
        private const val reminderEnabledKey = "reminder_enabled"
        private const val reminderTimeKey = "reminder_time"
        private const val lastVersionWhenChangesDialogShownKey = "last_changes_dialog_version_shown"
        private const val practicePreviewLayoutKey = "practice_preview_layout"
        private const val kanaGroupsEnabledKey = "kana_groups_enabled"
        private const val dashboardSortByTimeKey = "dashboard_sort_by_time"

    }

    override val analyticsEnabled: SuspendedProperty<Boolean> = registerProperty {
        createBooleanProperty(
            key = analyticsEnabledKey,
            initialValueProvider = { true }
        )
    }

    override val practiceType: SuspendedProperty<PracticeType> = registerProperty {
        createEnumProperty(
            key = practiceTypeKey,
            initialValueProvider = { PracticeType.Writing }
        )
    }

    override val filterOption: SuspendedProperty<FilterOption> = registerProperty {
        createEnumProperty(
            key = filterOptionKey,
            initialValueProvider = { FilterOption.All }
        )
    }

    override val sortOption: SuspendedProperty<SortOption> = registerProperty {
        createEnumProperty(
            key = sortOptionKey,
            initialValueProvider = { SortOption.AddOrder }
        )
    }

    override val isSortDescending: SuspendedProperty<Boolean> = registerProperty {
        createBooleanProperty(
            key = isSortDescendingKey,
            initialValueProvider = { true }
        )
    }

    override val practicePreviewLayout: SuspendedProperty<PracticePreviewLayout> =
        registerProperty {
            createEnumProperty(
                key = practicePreviewLayoutKey,
                initialValueProvider = { PracticePreviewLayout.Groups }
            )
        }

    override val kanaGroupsEnabled: SuspendedProperty<Boolean> = registerProperty {
        createBooleanProperty(
            key = kanaGroupsEnabledKey,
            initialValueProvider = { true }
        )
    }

    override val theme: SuspendedProperty<SupportedTheme> = registerProperty {
        createEnumProperty(
            key = themeKey,
            initialValueProvider = { SupportedTheme.System }
        )
    }

    override val dailyLimitEnabled: SuspendedProperty<Boolean> = registerProperty {
        createBooleanProperty(
            key = dailyLimitEnabledKey,
            initialValueProvider = { false }
        )
    }

    override val dailyLearnLimit: SuspendedProperty<Int> = registerProperty {
        createIntProperty(
            key = dailyLearnLimitKey,
            initialValueProvider = { 4 }
        )
    }

    override val dailyReviewLimit: SuspendedProperty<Int> = registerProperty {
        createIntProperty(
            key = dailyReviewLimitKey,
            initialValueProvider = { 60 }
        )
    }

    override val reminderEnabled: SuspendedProperty<Boolean> = registerProperty {
        createBooleanProperty(
            key = reminderEnabledKey,
            initialValueProvider = { false }
        )
    }

    override val reminderTime: SuspendedProperty<LocalTime> = registerProperty {
        createLocalTimeProperty(
            key = reminderTimeKey,
            initialValueProvider = { LocalTime(hour = 9, minute = 0) }
        )
    }

    override val lastAppVersionWhenChangesDialogShown: SuspendedProperty<String> =
        registerProperty {
            createStringProperty(
                key = lastVersionWhenChangesDialogShownKey,
                initialValueProvider = { "" }
            )
        }

    override val dashboardSortByTime: SuspendedProperty<Boolean> = registerProperty {
        createBooleanProperty(
            key = dashboardSortByTimeKey,
            initialValueProvider = { false }
        )
    }

}