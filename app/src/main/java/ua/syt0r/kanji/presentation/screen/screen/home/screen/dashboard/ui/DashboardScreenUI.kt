package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.data.DashboardScreenState
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.items.BeginnersNotice

@Composable
fun DashboardScreenUI(
    state: DashboardScreenState,
    onWritingOptionSelected: () -> Unit
) {

    when (state) {
        DashboardScreenState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        is DashboardScreenState.Loaded -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                BeginnersNotice()

            }

        }
    }

}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {

    AppTheme {
        DashboardScreenUI(
            state = DashboardScreenState.Loading,
            onWritingOptionSelected = {}
        )
    }

}

@Preview(showSystemUi = true)
@Composable
private fun DarkPreview() {

    AppTheme(useDarkTheme = true) {
        DashboardScreenUI(
            state = DashboardScreenState.Loading,
            onWritingOptionSelected = {}
        )
    }

}