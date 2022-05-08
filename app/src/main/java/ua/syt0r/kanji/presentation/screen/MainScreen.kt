package ua.syt0r.kanji.presentation.screen

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    viewModel: MainContract.ViewModel = hiltViewModel<MainViewModel>()
) {

    val navController = rememberNavController()

    val mainNavigation = MainNavigation(
        navHostController = navController,
        mainViewModel = viewModel,
        analyticsManager = (LocalContext.current as MainActivity).analyticsManager
    )
    mainNavigation.DrawContent()

    val shouldShowAnalyticsConsent = viewModel.shouldShowAnalyticsConsentDialog()
        .collectAsState(initial = false)

    if (shouldShowAnalyticsConsent.value) {
        AlertDialog(
            onDismissRequest = { viewModel.consentForAnalytics() },
            tonalElevation = 0.dp,
            title = { Text(text = "Data tracking notice") },
            text = { Text(text = "Kanji Dojo uses Firebase Analytics to send anonymous data about how you use application to in order to improve it. You can disable it in settings") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.consentForAnalytics() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "Continue")
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }

}