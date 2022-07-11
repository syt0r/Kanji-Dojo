package ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.CustomDropdownMenu
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortOption

@Composable
fun PracticePreviewSortDialog(
    sortConfiguration: SortConfiguration,
    onSortSelected: (SortConfiguration) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {

    Dialog(
        onDismissRequest = onDismissRequest
    ) {

        Surface(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .width(IntrinsicSize.Max)
            ) {

                Text(
                    text = "Sort",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        top = 24.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 8.dp
                    )
                )

                SortOption.values().forEach { sortOption ->
                    Item(
                        sortConfiguration = sortConfiguration,
                        sortOption = sortOption,
                        onSelected = { isAsc ->
                            onSortSelected(SortConfiguration(sortOption, !isAsc))
                        }
                    )
                }

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.Item(
    sortConfiguration: SortConfiguration,
    sortOption: SortOption,
    onSelected: (isAsc: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
    ) {

        Text(text = sortOption.title)

        if (sortOption.hint != null) {

            var showHint by remember { mutableStateOf(false) }

            IconButton(
                onClick = { showHint = true }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_help_outline_24),
                    contentDescription = null
                )
                CustomDropdownMenu(
                    expanded = showHint,
                    onDismissRequest = { showHint = false }
                ) {
                    Text(
                        text = sortOption.hint,
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .widthIn(max = 200.dp)
                    )
                }
            }
        }

    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
    ) {

        FilterChip(
            selected = sortConfiguration.sortOption == sortOption && !sortConfiguration.isDescending,
            onClick = { onSelected(true) },
            label = { Text(text = sortOption.ascTitle) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        FilterChip(
            selected = sortConfiguration.sortOption == sortOption && sortConfiguration.isDescending,
            onClick = { onSelected(false) },
            label = { Text(text = sortOption.descTitle) }
        )

    }

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        PracticePreviewSortDialog(
            sortConfiguration = SortConfiguration.default
        )
    }
}

@Preview
@Composable
private fun DarkPreview() {
    AppTheme(useDarkTheme = true) {
        PracticePreviewSortDialog(
            sortConfiguration = SortConfiguration.default
        )
    }
}