package ua.syt0r.kanji.presentation.screen.screen.home.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreenUI(onAboutButtonClick: () -> Unit) {

    Column(Modifier.fillMaxSize()) {

        Text(
            text = "About",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAboutButtonClick() }
                .padding(vertical = 18.dp, horizontal = 24.dp)
        )

    }

}
