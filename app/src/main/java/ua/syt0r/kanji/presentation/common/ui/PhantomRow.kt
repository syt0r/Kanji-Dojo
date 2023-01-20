package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/***
 * Phantom items - items present in content composable, counted from the end
 * If there is enough space all items excluding phantomItemsCount are shown
 * If there is not enough space then phantom items are drawn in priority, other
 * items are drawn if can be fitted
 */
@Composable
fun PhantomRow(
    modifier: Modifier = Modifier,
    phantomItemsCount: Int = 0,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
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
        val layoutHeight = constraints.run {
            if (hasFixedHeight) constraints.maxHeight else rowHeight
        }

        layout(
            width = containerWidth,
            height = layoutHeight
        ) {

            var itemX = when (horizontalAlignment) {
                Alignment.CenterHorizontally -> (containerWidth - itemsToDrawWidth) / 2
                Alignment.Start -> 0
                Alignment.End -> containerWidth - itemsToDrawWidth
                else -> throw IllegalArgumentException("Unsupported horizontal alignment")
            }

            itemsToDraw.forEach {

                val itemY = when (verticalAlignment) {
                    Alignment.CenterVertically -> (layoutHeight - it.height) / 2
                    Alignment.Top -> 0
                    Alignment.Bottom -> layoutHeight - it.height
                    else -> throw IllegalArgumentException("Unsupported vertical alignment[$verticalAlignment]")
                }

                it.place(itemX, itemY)
                itemX += (it.width)

            }

        }

    }

}


@Preview
@Composable
private fun Preview() {

    val horizontalAlignment = listOf(Alignment.CenterHorizontally, Alignment.Start, Alignment.End)
    val verticalAlignment = listOf(Alignment.CenterVertically, Alignment.Top, Alignment.Bottom)

    Column {

        horizontalAlignment.zip(verticalAlignment).forEach { (h, v) ->

            PhantomRow(
                modifier = Modifier
                    .size(width = 200.dp, height = 50.dp)
                    .background(color = Color(Random.nextInt())),
                horizontalAlignment = h,
                verticalAlignment = v,
                phantomItemsCount = 1
            ) {

                (0..40).forEach {
                    Text(text = "$it")
                }

                Text(text = "kek", modifier = Modifier.height(30.dp))


            }

        }

    }

}