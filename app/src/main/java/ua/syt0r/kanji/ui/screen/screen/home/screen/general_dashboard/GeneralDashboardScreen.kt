package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.ui.Ads
import ua.syt0r.kanji.ui.screen.screen.home.HomeScreenContent
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui.ContinuePreviousPractice
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui.PracticeCalendar
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme

@Composable
fun GeneralDashboardScreen() {

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

        ItemContainer {
            Ads(Modifier.padding(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

    }

}

@Composable
fun ItemContainer(content: @Composable () -> Unit) {

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
fun GeneralDashboardScreenContentPreview() {

    KanjiDojoTheme {
        GeneralDashboardScreen()
    }

}

@Preview(showSystemUi = true)
@Composable
fun GeneralDashboardScreenPreview() {

    KanjiDojoTheme {

        HomeScreenContent {
            GeneralDashboardScreen()
        }

    }

}
