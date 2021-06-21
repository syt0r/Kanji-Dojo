package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.user_data.model.PracticeSetInfo
import ua.syt0r.kanji.ui.screen.LocalMainNavigator
import ua.syt0r.kanji.ui.screen.MainContract
import ua.syt0r.kanji.ui.theme.primary

@Composable
fun WritingDashboardScreen(
    viewModel: WritingDashboardScreenContract.ViewModel = hiltViewModel<WritingDashboardViewModel>(),
    mainNavigation: MainContract.Navigation = LocalMainNavigator.current
) {

    val mutableState = viewModel.state.observeAsState()

    when (val state = mutableState.value!!) {
        WritingDashboardScreenContract.State.Loading -> LoadingState()
        is WritingDashboardScreenContract.State.Loaded -> LoadedState(
            practiceSets = state.practiceSets,
            onPracticeSetCreateOptionSelected = {
                when (it) {
                    DialogOption.CUSTOM -> mainNavigation.navigateToCreateCustomPracticeSet()
                }
            },
            onPracticeSetSelected = { mainNavigation.navigateToPracticeSet(it) }
        )
    }

}

@Composable
private fun LoadingState() {
    CircularProgressIndicator(
        strokeWidth = 8.dp,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .size(64.dp),
        color = MaterialTheme.colors.primary
    )
}

@Composable
private fun LoadedState(
    practiceSets: List<PracticeSetInfo>,
    onPracticeSetCreateOptionSelected: (DialogOption) -> Unit,
    onPracticeSetSelected: (Long) -> Unit
) {

    val isDialogOpened = remember { mutableStateOf(false) }
    if (isDialogOpened.value) {
        SelectionDialog(
            onDismiss = { isDialogOpened.value = false },
            onOptionSelected = {
                isDialogOpened.value = false
                onPracticeSetCreateOptionSelected(it)
            }
        )
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

        if (practiceSets.isEmpty()) {

            Text(
                text = buildAnnotatedString {

                    append("No practice sets created yet. Click on ")

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("+")
                    }

                    append(" to choose from available or create custom set")

                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp)
                    .wrapContentSize(),
                textAlign = TextAlign.Center
            )

        } else {

            LazyColumn {

                items(practiceSets) {
                    Column {
                        Text(
                            text = it.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPracticeSetSelected(it.id) }
                                .padding(vertical = 12.dp, horizontal = 24.dp)
                        )
                    }
                }

            }

        }

    }

}

private enum class DialogOption { PREDEFINED, CUSTOM }

@Composable
private fun SelectionDialog(
    onDismiss: () -> Unit,
    onOptionSelected: (DialogOption) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val delayedClick: (DialogOption) -> Unit = {
        coroutineScope.launch {
            delay(150)
            onOptionSelected(it)
        }
    }

    Dialog(onDismissRequest = onDismiss) {

        Card(
            modifier = Modifier.width(280.dp),
            shape = RoundedCornerShape(corner = CornerSize(size = 16.dp))
        ) {

            Column {

                Text(
                    text = "Add practice set",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.size(width = 0.dp, height = 12.dp))

                Text(
                    text = "Predefined",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { delayedClick(DialogOption.PREDEFINED) })
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                )

                Text(
                    text = "Custom",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { delayedClick(DialogOption.CUSTOM) })
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.size(width = 0.dp, height = 12.dp))

            }

        }

    }

}