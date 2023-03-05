package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreenUI(
    state: State<ScreenState>,
    onNoTranslationToggled: (Boolean) -> Unit,
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
            textResId = R.string.settings_no_translation_layout_title,
            descriptionResId = R.string.settings_no_translation_layout_subtitle,
            isEnabled = screenState.noTranslationLayoutEnabled,
            onToggled = { onNoTranslationToggled(!screenState.noTranslationLayoutEnabled) }
        )

        SwitchRow(
            textResId = R.string.settings_analytics_title,
            descriptionResId = R.string.settings_analytics_subtitle,
            isEnabled = screenState.analyticsEnabled,
            onToggled = { onAnalyticsToggled(!screenState.analyticsEnabled) }
        )

        Text(
            text = stringResource(R.string.settings_about_title),
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
    @StringRes textResId: Int,
    @StringRes descriptionResId: Int,
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
            Text(text = stringResource(textResId))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(descriptionResId),
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        SettingsScreenUI(
            state = ScreenState.Loaded(
                analyticsEnabled = false,
                noTranslationLayoutEnabled = false
            ).run { rememberUpdatedState(this) },
            onNoTranslationToggled = {},
            onAnalyticsToggled = {},
            onAboutButtonClick = {}
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.Preview()
}
