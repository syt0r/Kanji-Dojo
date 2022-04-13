package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BeginnersNotice() {

    DashboardItem {

        Text(
            text = "Welcome to Kanji Dojo",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "To start using app go to practice screen and select or create our own set of kanji to learn",
            style = MaterialTheme.typography.bodyMedium
        )

    }

}