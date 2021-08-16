package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.experimental.NotificationNotice
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.experimental.WritingTraining

@Composable
fun DashboardScreenUI(
    onWritingOptionSelected: () -> Unit,
    onDismissNotificationNotice: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        ItemContainer {
            WritingTraining(
                lastTrainingName = "JLPT N5",
                onClick = onWritingOptionSelected
            )
        }

        ItemContainer {
            NotificationNotice(
                onDismiss = onDismissNotificationNotice,
                onAccept = {}
            )
        }

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
        DashboardScreenUI(
            onWritingOptionSelected = {},
            onDismissNotificationNotice = {}
        )
    }

}