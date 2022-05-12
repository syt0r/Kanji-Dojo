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
import androidx.compose.ui.unit.dp

/***
 * Phantom items - items present in content composable, counted from the end
 * If there is enough space all items excluding phantomItemsCount are shown
 * If there is not enough space then phantom items are drawn in priority, other
 * items are drawn if can be fitted
 */
@Composable
fun PhantomRow(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    phantomItemsCount: Int = 1,
    content: @Composable () -> Unit
) {

    Layout(content, modifier) { measurables, constraints ->

        val placeableItems = measurables.map {
            it.measure(constraints = constraints.copy(minWidth = 0, minHeight = 0))
        }

        val containerWidth = constraints.maxWidth
        val itemsToDraw = mutableListOf<Placeable>()
        var itemsToDrawWidth: Int

        val defaultItems = placeableItems.dropLast(phantomItemsCount)
        val defaultItemsTotalWidth = defaultItems.sumOf { it.width }

        if (defaultItemsTotalWidth < containerWidth) {

            itemsToDraw.addAll(defaultItems)
            itemsToDrawWidth = defaultItemsTotalWidth

        } else {

            val phantomItems = placeableItems.drop(defaultItems.size)
            val phantomItemsTotalWidth = phantomItems.sumOf { it.width }

            itemsToDrawWidth = phantomItemsTotalWidth

            for (item in defaultItems) {
                if (itemsToDrawWidth + item.width < containerWidth) {
                    itemsToDraw.add(item)
                    itemsToDrawWidth += item.width
                } else {
                    break
                }
            }
            itemsToDraw.addAll(phantomItems)

        }

        val rowHeight = itemsToDraw.maxOfOrNull { it.height } ?: 0

        layout(
            width = containerWidth,
            height = rowHeight
        ) {

            var itemX = when (horizontalAlignment) {
                Alignment.CenterHorizontally -> (containerWidth - itemsToDrawWidth) / 2
                Alignment.Start -> 0
                Alignment.End -> containerWidth - itemsToDrawWidth
                else -> throw IllegalArgumentException("Unsupported horizontal alignment")
            }

            itemsToDraw.forEach {
                val itemY = (rowHeight - it.height) / 2

                it.place(itemX, itemY)
                itemX += (it.width)
            }

        }

    }

}


@Preview(showBackground = true)
@Composable
private fun Preview() {

    val alignments = listOf(Alignment.CenterHorizontally, Alignment.Start, Alignment.End)

    Column {

        alignments.forEach {

            PhantomRow(
                modifier = Modifier.size(width = 200.dp, height = 100.dp),
                horizontalAlignment = it,
                phantomItemsCount = 1
            ) {

                (0..40).forEach {
                    Text(text = "$it")
                }
                Text(text = "kek")


            }

            Spacer(modifier = Modifier.height(10.dp))

        }

    }

}