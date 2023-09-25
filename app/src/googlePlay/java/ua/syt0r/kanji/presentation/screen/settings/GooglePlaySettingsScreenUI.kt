package ua.syt0r.kanji.presentation.screen.settings

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import ua.syt0r.kanji.core.notification.ReminderNotificationConfiguration
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsAboutButton
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsContent
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsReminderNotification
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsSwitchRow
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsThemeToggle
import ua.syt0r.kanji.presentation.screen.settings.GooglePlaySettingsScreenContract.ScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GooglePlaySettingsScreenUI(
    state: State<ScreenState>,
    onReminderConfigurationChange: (ReminderNotificationConfiguration) -> Unit,
    onAboutButtonClick: () -> Unit,
    onAnalyticsToggled: (Boolean) -> Unit
) {

    val transition = updateTransition(targetState = state.value, label = "Content Transition")
    transition.Crossfade(
        contentKey = { it::class }
    ) {

        when (it) {
            is ScreenState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }

            is ScreenState.Loaded -> {

                LoadedState(
                    screenState = it,
                    onReminderConfigurationChange = onReminderConfigurationChange,
                    onAboutButtonClick = onAboutButtonClick,
                    onAnalyticsToggled = onAnalyticsToggled
                )

            }

        }

    }

}

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onReminderConfigurationChange: (ReminderNotificationConfiguration) -> Unit,
    onAboutButtonClick: () -> Unit,
    onAnalyticsToggled: (Boolean) -> Unit
) {

    SettingsContent {

        val strings = resolveString { settings }
        
        SettingsSwitchRow(
            title = strings.analyticsTitle,
            message = strings.analyticsMessage,
            isEnabled = screenState.analyticsEnabled,
            onToggled = { onAnalyticsToggled(!screenState.analyticsEnabled) }
        )

        SettingsReminderNotification(
            configuration = screenState.reminderConfiguration,
            onChanged = onReminderConfigurationChange
        )

        SettingsThemeToggle()

        SettingsAboutButton(onAboutButtonClick)

    }

}