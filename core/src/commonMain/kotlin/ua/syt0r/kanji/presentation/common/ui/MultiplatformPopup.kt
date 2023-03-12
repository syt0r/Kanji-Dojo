package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MenuDefaults
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.PopupPositionProvider

enum class PreferredPopupLocation { Top, Bottom }

@Composable
expect fun MultiplatformPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    preferredPopupLocation: PreferredPopupLocation = PreferredPopupLocation.Bottom,
    content: @Composable ColumnScope.() -> Unit
)


private val DropdownMenuItemDefaultMinWidth = 112.dp
private val DropdownMenuItemDefaultMaxWidth = 280.dp
private val DropdownMenuItemDefaultMinHeight = 48.dp

@Composable
fun PopupContentItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = rememberRipple(true)
            )
            .fillMaxWidth()
            // Preferred min and max width used during the intrinsic measurement.
            .sizeIn(
                minWidth = DropdownMenuItemDefaultMinWidth,
                maxWidth = DropdownMenuItemDefaultMaxWidth,
                minHeight = DropdownMenuItemDefaultMinHeight
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProvideTextStyle(androidx.compose.material3.MaterialTheme.typography.bodyLarge) {
            val contentAlpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled
            CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
                content()
            }
        }
    }
}

class CustomPopupPositionProvider(
    private val preferredPopupLocation: PreferredPopupLocation
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return IntOffset(
            x = anchorBounds.left,
            y = when (preferredPopupLocation) {
                PreferredPopupLocation.Top -> {
                    if (anchorBounds.top - popupContentSize.height < 0) 0
                    else anchorBounds.top - popupContentSize.height
                }
                PreferredPopupLocation.Bottom -> {
                    if (popupContentSize.height + anchorBounds.bottom < windowSize.height) anchorBounds.bottom
                    else anchorBounds.top - popupContentSize.height
                }
            }
        )
    }

}