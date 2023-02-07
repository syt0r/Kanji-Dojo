package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

@Composable
fun AutoBreakRow(
    modifier: Modifier = Modifier,
    horizontalItemSpacing: Dp = 8.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable () -> Unit
) {

    Layout(content, modifier) { measurables, constraints ->

        val horizontalSpacingPx = horizontalItemSpacing.roundToPx()

        val placeables = measurables.map {
            it.measure(constraints = constraints.copy(minHeight = 0, minWidth = 0))
        }

        val placeableLines = mutableListOf<PlaceableLine>()

        var lineItems = mutableListOf<Placeable>()
        var lineHeight = 0
        var lineItemsWidth = 0

        placeables.forEach { placeable ->

            val isEnoughSpaceForItem = lineItemsWidth + placeable.width +
                    horizontalSpacingPx * lineItems.size <= constraints.maxWidth

            if (!isEnoughSpaceForItem) {
                placeableLines.add(PlaceableLine(lineItems, lineHeight, lineItemsWidth))
                lineItems = mutableListOf()
                lineHeight = 0
                lineItemsWidth = 0
            }

            lineItems.add(placeable)
            lineItemsWidth += placeable.width
            if (placeable.height > lineHeight) lineHeight = placeable.height

        }

        if (lineItems.isNotEmpty()) {
            placeableLines.add(PlaceableLine(lineItems, lineHeight, lineItemsWidth))
        }

        val layoutWidth = placeableLines.maxOfOrNull {
            it.itemsWidth + max(0, it.placeables.size - 1) * horizontalSpacingPx
        } ?: constraints.minWidth

        layout(
            width = layoutWidth,
            height = placeableLines.sumOf { it.height }
        ) {

            var lineY = 0
            placeableLines.forEach { line ->

                var itemX = when (horizontalAlignment) {
                    Alignment.CenterHorizontally -> (layoutWidth - line.itemsWidth -
                            max(0, line.placeables.size - 1) * horizontalSpacingPx) / 2
                    Alignment.Start -> 0
                    Alignment.End -> layoutWidth - line.itemsWidth -
                            max(0, line.placeables.size - 1) * horizontalSpacingPx
                    else -> throw IllegalArgumentException("Unsupported horizontal alignment")
                }

                line.placeables.forEachIndexed { index, placeable ->
                    if (index != 0) itemX += horizontalSpacingPx
                    placeable.place(
                        x = itemX,
                        y = lineY + (line.height - placeable.height) / 2
                    )
                    itemX += placeable.width
                }

                lineY += line.height

            }

        }

    }

}

private data class PlaceableLine(
    val placeables: List<Placeable>,
    val height: Int,
    val itemsWidth: Int
)

@Preview(showBackground = true)
@Composable
private fun Preview() {

    val alignments = listOf(Alignment.CenterHorizontally, Alignment.Start, Alignment.End)

    Column {

        alignments.forEach {

            AutoBreakRow(Modifier.size(width = 200.dp, height = 100.dp), horizontalAlignment = it) {

                (0..20).forEach {
                    Text(text = "$it")
                }

                Text(text = "kek", fontSize = 8.sp)

            }

            Spacer(modifier = Modifier.height(10.dp))

        }

    }

}