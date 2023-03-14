package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable

@Composable
fun MostlySingleLineEliminateOverflowRow(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit
) {

    Layout(content, modifier) { measurables, constraints ->

        val containerWidth = constraints.maxWidth

        val placeables = mutableListOf<Placeable>()

        var currentIndex = 0
        var currentWidth = 0

        while (currentIndex < measurables.size && currentWidth <= containerWidth) {

            val placeable = measurables[currentIndex].measure(
                constraints = constraints.run {
                    //Make sure at least first item is fully visible, hence mostly single line
                    if (currentIndex == 0)
                        copy(
                            minWidth = 0,
                            minHeight = 0,
                            maxWidth = containerWidth - currentWidth
                        )
                    else {
                        copy(minWidth = 0, minHeight = 0)
                    }
                }
            )

            if (currentWidth + placeable.width <= containerWidth)
                placeables.add(placeable)

            currentIndex++
            currentWidth += placeable.width

        }

        val rowHeight = placeables.maxOfOrNull { it.height } ?: 0
        val layoutHeight = constraints.run {
            if (hasFixedHeight) constraints.maxHeight else rowHeight
        }

        val itemsToDrawWidth = currentWidth

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
            placeables.forEach {

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


//@Preview
//@Composable
//private fun Preview() {
//
//    val horizontalAlignment = listOf(Alignment.CenterHorizontally, Alignment.Start, Alignment.End)
//    val verticalAlignment = listOf(Alignment.CenterVertically, Alignment.Top, Alignment.Bottom)
//
//    Column {
//
//        horizontalAlignment.zip(verticalAlignment).forEach { (h, v) ->
//
//            MostlySingleLineEliminateOverflowRow(
//                modifier = Modifier
//                    .size(width = 200.dp, height = 50.dp)
//                    .background(color = Color(Random.nextInt())),
//                horizontalAlignment = h,
//                verticalAlignment = v
//            ) {
//
//                (0..40).forEach {
//                    Text(text = "$it")
//                }
//
//                Text(text = "kek", modifier = Modifier.height(30.dp))
//
//
//            }
//
//        }
//
//    }
//
//}