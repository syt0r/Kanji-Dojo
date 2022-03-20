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

@Composable
fun AutoBreakRow(
    modifier: Modifier = Modifier,
    horizontalItemSpacing: Dp = 8.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
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
            placeableLines.forEach { (_, line) ->

                var itemX = when (horizontalAlignment) {
                    Alignment.CenterHorizontally -> line.startSpacing
                    Alignment.Start -> 0
                    Alignment.End -> line.startSpacing * 2
                    else -> throw IllegalArgumentException("Unsupported horizontal alignment")
                }

                line.placeables.forEach {
                    it.place(itemX, itemY + (line.maxHeight - it.height) / 2)
                    itemX += (it.width + horizontalSpacingPx)
                }

                itemY += line.maxHeight

            }

        }

    }

}

private data class PlaceableLine(
    val placeables: MutableList<Placeable>,
    var maxHeight: Int,
    var startSpacing: Int
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