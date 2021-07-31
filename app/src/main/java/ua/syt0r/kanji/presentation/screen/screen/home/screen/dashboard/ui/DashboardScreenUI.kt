package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.DashboardScreen
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.experimental.ContinuePreviousPractice
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.experimental.PracticeCalendar

@Composable
fun DashboardScreenUI() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        ItemContainer {
            ContinuePreviousPractice(practiceSetName = "JLPT N5") { }
        }

        ItemContainer {
            PracticeCalendar()
        }

        Spacer(modifier = Modifier.height(24.dp))

    }

}

@Composable
private fun ItemContainer(content: @Composable () -> Unit) {

    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        content()
    }

}

@Preview(showSystemUi = true)
@Composable
private fun DashboardScreenContentPreview() {

    KanjiDojoTheme {
        DashboardScreen()
    }

}

@Preview(showSystemUi = true)
@Composable
private fun DashboardScreenPreview() {

//    KanjiDojoTheme {
//
//        HomeScreenContent {
//            GeneralDashboardScreen()
//        }
//
//    }

}
