package ua.syt0r.kanji.presentation.screen.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import ua.syt0r.kanji.core.notification.ReminderNotificationConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsAboutButton
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsBackupButton
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsContent
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsReminderNotification
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsThemeToggle
import ua.syt0r.kanji.presentation.screen.settings.FdroidSettingsScreenContract.ScreenState

@Composable
fun FdroidSettingsScreenUI(
    state: State<ScreenState>,
    onReminderConfigurationChange: (ReminderNotificationConfiguration) -> Unit,
    onBackupButtonClick: () -> Unit,
    onAboutButtonClick: () -> Unit
) {

    val transition = updateTransition(targetState = state.value, label = "Content Transition")
    transition.AnimatedContent(
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut() }
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

                SettingsContent {

                    SettingsReminderNotification(
                        configuration = it.reminderConfiguration,
                        onChanged = onReminderConfigurationChange
                    )

                    SettingsThemeToggle()

                    SettingsBackupButton(onBackupButtonClick)

                    SettingsAboutButton(onAboutButtonClick)

                }

            }

        }

    }

}