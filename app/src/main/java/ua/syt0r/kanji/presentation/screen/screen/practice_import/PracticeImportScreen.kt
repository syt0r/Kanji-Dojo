package ua.syt0r.kanji.presentation.screen.screen.practice_import

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_import.ui.PracticeImportScreenUI


@Composable
fun PracticeImportScreen(
    mainNavigation: MainContract.Navigation,
    viewModel: PracticeImportScreenContract.ViewModel = hiltViewModel<PracticeImportViewModel>()
) {

    val screenState = viewModel.state.value

    val context = LocalContext.current

    PracticeImportScreenUI(
        screenState = screenState,
        onUpButtonClick = { mainNavigation.navigateBack() },
        onItemSelected = {
            mainNavigation.navigateToPracticeCreate(
                configuration = CreatePracticeConfiguration.Import(
                    title = it.title,
                    classification = it.classification
                )
            )
        },
        onLinkClick = { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    )

}
