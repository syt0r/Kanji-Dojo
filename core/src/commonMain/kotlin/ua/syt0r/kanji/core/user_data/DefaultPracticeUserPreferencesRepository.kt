package ua.syt0r.kanji.core.user_data

import androidx.compose.ui.text.intl.Locale
import ua.syt0r.kanji.core.suspended_property.SuspendedProperty
import ua.syt0r.kanji.core.suspended_property.SuspendedPropertyProvider

class DefaultPracticeUserPreferencesRepository(
    provider: SuspendedPropertyProvider,
    private val isSystemLanguageJapanese: Boolean = Locale.current.language == "ja",
) : PracticeUserPreferencesRepository {

    override val noTranslationLayout: SuspendedProperty<Boolean> = provider.createBooleanProperty(
        key = "no_trans_layout_enabled",
        initialValueProvider = { true }
    )

    override val leftHandMode: SuspendedProperty<Boolean> = provider.createBooleanProperty(
        key = "left_handed_mode",
        initialValueProvider = { isSystemLanguageJapanese }
    )

    override val altStrokeEvaluator: SuspendedProperty<Boolean> = provider.createBooleanProperty(
        key = "use_alt_stroke_evaluator",
        initialValueProvider = { true }
    )

    override val kanaAutoPlay: SuspendedProperty<Boolean> = provider.createBooleanProperty(
        key = "practice_kana_auto_play",
        initialValueProvider = { true }
    )

    override val highlightRadicals: SuspendedProperty<Boolean> = provider.createBooleanProperty(
        key = "highlight_radicals",
        initialValueProvider = { true }
    )

    override val writingToleratedMistakes: SuspendedProperty<Int> = provider.createIntProperty(
        key = "writing_tolerated_mistakes",
        initialValueProvider = { 2 }
    )

    override val readingToleratedMistakes: SuspendedProperty<Int> = provider.createIntProperty(
        key = "reading_tolerated_mistakes",
        initialValueProvider = { 0 }
    )

}