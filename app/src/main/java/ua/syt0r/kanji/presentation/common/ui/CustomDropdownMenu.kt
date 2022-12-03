package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit
) {

    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    if (expandedStates.currentState || expandedStates.targetState) {

        Popup(
            onDismissRequest = onDismissRequest,
            properties = properties,
            popupPositionProvider = CustomPopupPositionProvider
        ) {

            val transition = updateTransition(expandedStates, "DropDownMenu")
            val scale by transition.animateFloat(label = "DropDownMenuScale") {
                if (it) 1f else 0f
            }
            val alpha by transition.animateFloat(label = "DropDownMenuScaleAlpha") {
                if (it) 1f else 0f
            }

            Card(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .width(IntrinsicSize.Max)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                content()
            }

        }
    }

}

private object CustomPopupPositionProvider : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return IntOffset(
            x = anchorBounds.left,
            y = if (popupContentSize.height + anchorBounds.bottom < windowSize.height) anchorBounds.bottom
            else anchorBounds.top - popupContentSize.height
        )
    }

}