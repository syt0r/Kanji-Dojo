package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiClassificationGroup
import ua.syt0r.kanji.di.getViewModel
import ua.syt0r.kanji.ui.theme.primary

@Composable
fun WritingDashboardScreen(
    viewModel: WritingDashboardScreenContract.ViewModel = getViewModel<WritingDashboardViewModel>()
) {

//    WritingDashboardScreenContent(
//        state = viewModel.state.observeAsState(initial = WritingDashboardScreenContract.State.DEFAULT),
//    )

    val mutableState = viewModel.state.observeAsState(
        initial = WritingDashboardScreenContract.State.Loading
    )

    when (val state = mutableState.value) {
        WritingDashboardScreenContract.State.Loading -> LoadingState()
        is WritingDashboardScreenContract.State.Loaded -> LoadedState(
            classifications = state.classifications
        )
    }


}

@Composable
private fun LoadingState() {

}

@Composable
private fun LoadedState(classifications: List<KanjiClassificationGroup>) {

    val isDialogOpened = remember { mutableStateOf(false) }
    if (isDialogOpened.value) {
        AddDialog { isDialogOpened.value = false }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isDialogOpened.value = true },
                contentColor = primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_add_24),
                    contentDescription = ""
                )
            }
        }
    ) {

        LazyColumn {


        }

    }

}

@Composable
private fun AddDialog(onDismiss: () -> Unit) {

    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {

            Button(onClick = onDismiss) {
                Text(text = "123")
            }

        },
        title = {
            Text(text = "Add")
        }
    )

}