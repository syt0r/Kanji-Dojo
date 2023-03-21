package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
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
