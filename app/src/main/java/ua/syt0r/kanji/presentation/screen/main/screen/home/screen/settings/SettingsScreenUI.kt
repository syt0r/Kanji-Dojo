package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState

@Composable
fun SettingsScreenUI(
    screenState: ScreenState,
    onAnalyticsToggled: (Boolean) -> Unit = {},
    onAboutButtonClick: () -> Unit = {}
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
                        .padding(vertical = 18.dp, horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(Modifier.weight(1f)) {
                        Text(text = "Analytics")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Allow sending anonymous data to improve experience",
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        SettingsScreenUI(
            screenState = ScreenState.Loaded(analyticsEnabled = false)
        )
    }
}
