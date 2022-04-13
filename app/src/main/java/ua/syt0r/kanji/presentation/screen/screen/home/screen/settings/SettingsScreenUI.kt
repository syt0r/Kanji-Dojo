package ua.syt0r.kanji.presentation.screen.screen.home.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreenUI(onAboutButtonClick: () -> Unit) {

    Column(Modifier.fillMaxSize()) {

        Text(
            text = "About",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAboutButtonClick() }
                .padding(vertical = 12.dp, horizontal = 24.dp)
        )

    }

}

@Composable
private fun SettingSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary
    )
}