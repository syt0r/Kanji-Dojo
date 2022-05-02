package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.data.DashboardScreenState
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.items.BeginnersNotice
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.items.WritingTraining

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenUI(
    screenState: DashboardScreenState,
    onWritingOptionSelected: () -> Unit
) {

    Crossfade(targetState = screenState) { screenState ->
        when (screenState) {
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

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {

                        ElevatedCard(
                            Modifier
                                .weight(1f)
                                .height(100.dp)
                        ) {

                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.headlineLarge
                                )

                                Text(
                                    text = "practices last week",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        ElevatedCard(
                            Modifier
                                .weight(1f)
                                .height(100.dp)
                        ) {

                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "JLPT N5",
                                    style = MaterialTheme.typography.headlineLarge
                                )

                                Text(
                                    text = "Resume last practice",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }

                    }

                    BeginnersNotice()

                    screenState.reviewedPractice?.let {
                        WritingTraining(it, onClick = {})
                    }

                }

            }
        }

    }

}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {

    AppTheme {
        DashboardScreenUI(
            screenState = DashboardScreenState.Loading,
            onWritingOptionSelected = {}
        )
    }

}

@Preview(showSystemUi = true)
@Composable
private fun DarkPreview() {

    AppTheme(useDarkTheme = true) {
        DashboardScreenUI(
            screenState = DashboardScreenState.Loading,
            onWritingOptionSelected = {}
        )
    }

}