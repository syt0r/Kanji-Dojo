package ua.syt0r.kanji.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.ClickableFuriganaText
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlternativeWordsDialog(
    word: JapaneseWord,
    onDismissRequest: () -> Unit,
    onFuriganaClick: (String) -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        content = {

            DialogDecoration {

                Text(
                    text = stringResource(R.string.alternative_words_dialog_title),
                    style = MaterialTheme.typography.titleLarge
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.alternative_words_dialog_readings),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    AutoBreakRow {
                        ClickableFuriganaText(
                            furiganaString = word.furiganaString,
                            onClick = onFuriganaClick
                        )
                    }
                    Text(
                        text = stringResource(R.string.alternative_words_dialog_meanings),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    AutoBreakRow(
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
                    Text(text = stringResource(R.string.alternative_words_dialog_button))
                }

            }

        }
    )

}

@Composable
private fun DialogDecoration(
    content: @Composable ColumnScope.() -> Unit
) {

    Surface(
        modifier = Modifier.clip(AlertDialogDefaults.shape)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 10.dp)
        ) {
            content()
        }
    }

}

@Preview(showBackground = true, locale = "")
@Composable
private fun Preview() {

    AppTheme(useDarkTheme = true) {
        AlternativeWordsDialog(
            word = PreviewKanji.SampleMultiMeaningWord,
            onDismissRequest = {},
            onFuriganaClick = {}
        )
    }

}