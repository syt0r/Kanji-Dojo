package ua.syt0r.kanji.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.ClickableFuriganaText
import ua.syt0r.kanji.presentation.common.ui.FuriganaText


@Composable
fun AlternativeWordsDialog(
    word: JapaneseWord,
    onDismissRequest: () -> Unit,
    onFuriganaClick: ((String) -> Unit)? = null
) {

    MultiplatformDialog(
        onDismissRequest = onDismissRequest,
        content = {

            Column(
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
            ) {

                Text(
                    text = resolveString { alternativeDialog.title },
                    style = MaterialTheme.typography.titleLarge
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = resolveString { alternativeDialog.readingsTitle },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    AutoBreakRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalItemSpacing = 16.dp
                    ) {
                        word.readings.forEach { reading ->
                            if (onFuriganaClick != null) {
                                ClickableFuriganaText(
                                    furiganaString = reading,
                                    onClick = onFuriganaClick
                                )
                            } else {
                                FuriganaText(furiganaString = reading)
                            }
                        }
                    }
                    Text(
                        text = resolveString { alternativeDialog.meaningsTitle },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    AutoBreakRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalItemSpacing = 16.dp
                    ) {
                        word.meanings.forEach { text ->
                            Text(
                                text = text.capitalize(Locale.current),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = resolveString { alternativeDialog.button })
                }

            }

        }
    )

}

//@Preview(showBackground = true, locale = "")
//@Composable
//private fun Preview() {
//
//    AppTheme(useDarkTheme = true) {
//        AlternativeWordsDialog(
//            word = PreviewKanji.WordWithAlternativesSample,
//            onDismissRequest = {},
//            onFuriganaClick = {}
//        )
//    }
//
//}