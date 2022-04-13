package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardItem(
    content: @Composable ColumnScope.() -> Unit
) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            content()

        }

    }

}