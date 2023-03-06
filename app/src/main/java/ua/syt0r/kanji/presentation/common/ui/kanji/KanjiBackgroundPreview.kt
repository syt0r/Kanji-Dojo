package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun KanjiBackgroundPreview() {

    Box(
        Modifier
            .size(400.dp)
            .background(Color.Black)
    ) {

        KanjiBackground(
            Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .background(Color.White)
        )

    }

}