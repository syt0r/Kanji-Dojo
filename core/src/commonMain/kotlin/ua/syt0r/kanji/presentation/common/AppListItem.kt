package ua.syt0r.kanji.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.AppListItemDefaults.ListItemDefaultPaddings
import ua.syt0r.kanji.presentation.common.theme.Dimens

object AppListItemDefaults {
    val ExtraPaddings = PaddingValues(horizontal = Dimens.SpacingBig)
    val ListItemDefaultPaddings = PaddingValues(
        horizontal = Dimens.ContentPaddingSmall,
        vertical = Dimens.SpacingMid
    )
    val ClickableTrailingOffset = Dimens.SpacingBig
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
    paddingValues: PaddingValues = AppListItemDefaults.ExtraPaddings
) {

    ListItem(
        headlineContent = headlineContent,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(paddingValues)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick),
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent
    )

}

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    paddingValues: PaddingValues = AppListItemDefaults.ExtraPaddings,
    rowContent: @Composable RowScope.() -> Unit
) {

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(paddingValues)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick)
            .padding(ListItemDefaultPaddings),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        rowContent()
    }

}
