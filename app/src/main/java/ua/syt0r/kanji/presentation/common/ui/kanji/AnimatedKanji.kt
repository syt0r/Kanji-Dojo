package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun AnimatedKanjiPreview() {

    AppTheme {
        AnimatedKanji(
            strokes = PreviewKanji.strokes,
            modifier = Modifier.size(200.dp)
        )
    }

}
