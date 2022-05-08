package ua.syt0r.kanji.presentation.screen.screen.home.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.screen.screen.home.screen.settings.SettingsScreenContract.ScreenState

@Composable
fun SettingsScreenUI(
    screenState: ScreenState,
    onAnalyticsToggled: (Boolean) -> Unit,
    onAboutButtonClick: () -> Unit
) {

    when (screenState) {
        is ScreenState.Loading -> {
            CircularProgressIndicator(
                Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
        is ScreenState.Loaded -> {

            Column(Modifier.fillMaxSize()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAnalyticsToggled(!screenState.analyticsEnabled) }
                        .padding(vertical = 18.dp, horizontal = 24.dp)
                ) {

                    Column(Modifier.weight(1f)) {
                        Text(text = "Analytics")
                        Text(
                            text = "Allow sending anonymous data to improve experience",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // TODO replace when material3 version if fixed
                    androidx.compose.material.Switch(
                        checked = screenState.analyticsEnabled,
                        onCheckedChange = { onAnalyticsToggled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.secondary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                            uncheckedTrackColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Text(
                    text = "About",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAboutButtonClick() }
                        .padding(vertical = 18.dp, horizontal = 24.dp)
                )

            }

        }

    }

}
