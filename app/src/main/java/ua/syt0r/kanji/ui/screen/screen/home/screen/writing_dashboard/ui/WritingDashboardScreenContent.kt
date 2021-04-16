package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.ui.common.ComposeState
import ua.syt0r.kanji.ui.common.kanji.Kanji
import ua.syt0r.kanji.ui.common.kanji.KanjiBackground
import ua.syt0r.kanji.ui.screen.LocalMainNavigator
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract.State
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.WritingDashboardScreenData
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.WritingDashboardScreenItem
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme

@Preview(showBackground = true)
@Composable
fun WritingDashboardScreenContentPreview() {
    KanjiDojoTheme {
        WritingDashboardScreenContent(
            state = mutableStateOf(State.Loading)
        )
    }
}

@Composable
fun WritingDashboardScreenContent(
    state: ComposeState<State>
) {

    when (val currentState = state.value) {
        is State.Loading -> LoadingState()
        is State.Loaded -> LoadedState(data = currentState.screenData)
    }

}

@Composable
private fun LoadingState() {

}

data class GroupState(
    val expandedState: MutableState<Boolean>
)

@Composable
private fun LoadedState(data: WritingDashboardScreenData) {

    val groupStates = data.categories
        .flatMap { it.items }
        .filterIsInstance<WritingDashboardScreenItem.ItemGroup>()
        .associate { group ->
            val isGroupExpanded = remember(
                key1 = group.title,
                calculation = { mutableStateOf(false) }
            )
            group.title to GroupState(isGroupExpanded)
        }

    LazyColumn {

        data.categories.forEach { category ->

            item {
                CategoryTitle(text = category.title)
            }

            category.items.forEach { DataItem(it, groupStates) }

        }

    }

}

@Composable
private fun CategoryTitle(text: String) {

    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(vertical = 4.dp, horizontal = 12.dp),
        color = MaterialTheme.colors.onPrimary,
        fontWeight = FontWeight.SemiBold
    )

}

private fun LazyListScope.DataItem(
    dataItem: WritingDashboardScreenItem,
    groupStates: Map<String, GroupState>
) {
    when (dataItem) {
        is WritingDashboardScreenItem.SingleItem -> item {
            val navigator = LocalMainNavigator.current
            SingleItem(
                item = dataItem,
                onClick = { navigator.navigateToPracticeSet() }
            )
        }
        is WritingDashboardScreenItem.ItemGroup -> {

            val groupState = groupStates.getValue(dataItem.title)

            item {
                ItemGroup(
                    item = dataItem,
                    state = groupState,
                    onClick = {
                        groupState.expandedState.value = !groupState.expandedState.value
                    }
                )
            }

            if (groupState.expandedState.value) {
                items(dataItem.items) {
                    val navigator = LocalMainNavigator.current
                    ItemGroupChild(
                        item = it,
                        onClick = { navigator.navigateToPracticeSet() }
                    )
                }
            }
        }
    }
}

@Composable
fun SingleItem(
    item: WritingDashboardScreenItem.SingleItem,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        KanjiBackground(
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .size(50.dp)
                .shadow(4.dp)
        ) {

            Kanji(
                strokes = item.previewKanji,
                modifier = Modifier.fillMaxSize()
            )

        }

        Text(
            text = item.title,
            modifier = Modifier.fillMaxSize()
        )

    }

}

@Composable
fun ItemGroup(
    item: WritingDashboardScreenItem.ItemGroup,
    state: GroupState,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        KanjiBackground(
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .size(50.dp)
                .shadow(4.dp)
        ) {

            Kanji(
                strokes = item.previewKanji,
                modifier = Modifier.fillMaxSize()
            )

        }

        Text(
            text = item.title,
//            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(
                id = if (state.expandedState.value) R.drawable.ic_baseline_keyboard_arrow_up_24
                else R.drawable.ic_baseline_keyboard_arrow_down_24
            ),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(horizontal = 12.dp)
                .fillMaxHeight()
        )

    }

}

@Composable
fun ItemGroupChild(
    item: WritingDashboardScreenItem.SingleItem,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxSize()
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        KanjiBackground(
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .size(50.dp)
                .shadow(4.dp)
        ) {

            Kanji(
                strokes = item.previewKanji,
                modifier = Modifier.fillMaxSize()
            )

        }

        Text(
            text = item.title,
            modifier = Modifier.fillMaxSize()
        )

    }

}