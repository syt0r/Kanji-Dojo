package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreenUI(
    state: State<ScreenState>,
    onAnalyticsToggled: (Boolean) -> Unit = {},
    onAboutButtonClick: () -> Unit = {}
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
    onAnalyticsToggled: (Boolean) -> Unit,
    onAboutButtonClick: () -> Unit
) {

    Column(Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAnalyticsToggled(!screenState.analyticsEnabled) }
                .padding(vertical = 18.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {
                Text(text = stringResource(R.string.settings_analytics_title))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.settings_analytics_subtitle),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = screenState.analyticsEnabled,
                onCheckedChange = { onAnalyticsToggled(it) },
                colors = SwitchDefaults.colors(
                    uncheckedTrackColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Text(
            text = stringResource(R.string.settings_about_title),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAboutButtonClick() }
                .padding(vertical = 18.dp, horizontal = 24.dp)
        )

    }

}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        SettingsScreenUI(
            state = rememberUpdatedState(ScreenState.Loaded(analyticsEnabled = false))
        )
    }
}
