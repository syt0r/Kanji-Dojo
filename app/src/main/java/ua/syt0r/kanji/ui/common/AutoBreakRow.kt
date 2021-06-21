package ua.syt0r.kanji.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun AutoBreakRowPreview() {

    AutoBreakRow(Modifier.size(width = 200.dp, height = 100.dp)) {

        (0..20).forEach {
            Text(text = "$it")
        }

    }

}

@Composable
fun AutoBreakRow(
    modifier: Modifier = Modifier,
    horizontalItemSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {

    Layout(content, modifier) { measurables, constraints ->

        val horizontalSpacingPx = horizontalItemSpacing.roundToPx()

        val placeableLineItems = measurables.map {
            it.measure(constraints = constraints.copy(minHeight = 0, minWidth = 0))
        }

        val placeableLines = mutableMapOf<Int, PlaceableLine>()

        val containerWidth = constraints.maxWidth

        var currentLineIndex = 0
        var currentLineItemsWidth = 0

        placeableLineItems.forEach { item ->

            val placeableLine = placeableLines[currentLineIndex]
                ?.let {
                    val expectedTotalSpacingWidth = it.placeables.size * horizontalSpacingPx
                    if (item.width + currentLineItemsWidth + expectedTotalSpacingWidth < containerWidth) {
                        it.placeables.add(item)
                        currentLineItemsWidth += item.width
                        if (item.height > it.maxHeight) it.maxHeight = item.height
                        it.startSpacing = (containerWidth -
                                expectedTotalSpacingWidth - it.placeables.map { it.width }
                            .sum()) / 2
                        it
                    } else {
                        currentLineIndex++
                        currentLineItemsWidth = item.width
                        PlaceableLine(
                            placeables = mutableListOf(item),
                            maxHeight = item.height,
                            startSpacing = containerWidth / 2 - item.width / 2
                        )
                    }
                }
                ?: PlaceableLine(
                    placeables = mutableListOf(item),
                    maxHeight = item.height,
                    startSpacing = containerWidth / 2 - item.width / 2
                ).also {
                    currentLineItemsWidth = item.width
                }

            placeableLines[currentLineIndex] = placeableLine

        }

        layout(
            width = containerWidth,
            height = placeableLines.values.map { it.maxHeight }.sum()
        ) {

            var itemY = 0
            placeableLines.forEach { (_, value) ->
                var itemX = value.startSpacing

                value.placeables.forEach {
                    it.place(itemX, itemY)
                    itemX += (it.width + horizontalSpacingPx)
                }

                itemY += value.maxHeight
            }

        }

    }

}

private data class PlaceableLine(
    val placeables: MutableList<Placeable>,
    var maxHeight: Int,
    var startSpacing: Int
)