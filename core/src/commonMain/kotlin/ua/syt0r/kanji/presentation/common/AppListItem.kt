package ua.syt0r.kanji.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

object AppListItemDefaults {
    val ClickablePaddings = PaddingValues(horizontal = 12.dp)
}

@Composable
fun AppListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    paddingValues: PaddingValues = AppListItemDefaults.ClickablePaddings
) {

    ListItem(
        headlineContent = headlineContent,
        modifier = modifier
            .padding(paddingValues)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick),
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent
    )

}
