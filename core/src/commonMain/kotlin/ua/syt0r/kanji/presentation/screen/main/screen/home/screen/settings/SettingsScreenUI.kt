package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.theme_manager.LocalThemeManager
import ua.syt0r.kanji.core.user_data.model.SupportedTheme
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.MultiplatformPopup
import ua.syt0r.kanji.presentation.common.ui.PopupContentItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreenUI(
    state: State<ScreenState>,
    onNoTranslationToggled: (Boolean) -> Unit,
    leftHandedToggled: (Boolean) -> Unit,
    onAnalyticsToggled: (Boolean) -> Unit,
    onAboutButtonClick: () -> Unit
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
                    onNoTranslationToggled = onNoTranslationToggled,
                    leftHandedToggled = leftHandedToggled,
                    onAnalyticsToggled = onAnalyticsToggled,
                    onAboutButtonClick = onAboutButtonClick
                )

            }

        }

    }

}

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onNoTranslationToggled: (Boolean) -> Unit,
    leftHandedToggled: (Boolean) -> Unit,
    onAnalyticsToggled: (Boolean) -> Unit,
    onAboutButtonClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
            .widthIn(max = 400.dp)
            .padding(horizontal = 10.dp)
            .padding(top = 4.dp, bottom = 16.dp)
    ) {

        SwitchRow(
            title = resolveString { settings.noTranslationLayoutTitle },
            message = resolveString { settings.noTranslationLayoutMessage },
            isEnabled = screenState.noTranslationLayoutEnabled,
            onToggled = { onNoTranslationToggled(!screenState.noTranslationLayoutEnabled) }
        )

        SwitchRow(
            title = resolveString { settings.leftHandedModeTitle },
            message = resolveString { settings.leftHandedModeMessage },
            isEnabled = screenState.leftHandedModeEnabled,
            onToggled = { leftHandedToggled(!screenState.leftHandedModeEnabled) }
        )

        SwitchRow(
            title = resolveString { settings.analyticsTitle },
            message = resolveString { settings.analyticsMessage },
            isEnabled = screenState.analyticsEnabled,
            onToggled = { onAnalyticsToggled(!screenState.analyticsEnabled) }
        )

        ThemeToggle()

        Text(
            text = resolveString { settings.aboutTitle },
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onAboutButtonClick)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
        )

    }

}

@Composable
private fun SwitchRow(
    title: String,
    message: String,
    isEnabled: Boolean,
    onToggled: () -> Unit
) {

    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onToggled)
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggled() },
            colors = SwitchDefaults.colors(
                uncheckedTrackColor = MaterialTheme.colorScheme.background
            )
        )
    }

}

@Composable
private fun ThemeToggle() {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .padding(start = 20.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = resolveString { settings.themeTitle },
            modifier = Modifier.weight(1f)
        )

        var isExpanded by remember { mutableStateOf(false) }

        Box {

            val themeManager = LocalThemeManager.current

            TextButton(
                onClick = { isExpanded = !isExpanded },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(themeManager.currentTheme.value.resolveDisplayText())
                Icon(Icons.Default.ArrowDropDown, null)
            }
            MultiplatformPopup(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {

                Column {
                    SupportedTheme.values().forEach {
                        PopupContentItem(
                            onClick = { themeManager.changeTheme(it) }
                        ) {
                            Text(text = it.resolveDisplayText())
                        }
                    }
                }

            }
        }

    }
}

@Composable
private fun SupportedTheme.resolveDisplayText(): String = resolveString {
    when (this@resolveDisplayText) {
        SupportedTheme.System -> settings.themeSystem
        SupportedTheme.Light -> settings.themeLight
        SupportedTheme.Dark -> settings.themeDark
    }
}
