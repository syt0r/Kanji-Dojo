package ua.syt0r.kanji.presentation.screen.main.screen.deck_details

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPracticeSharer(snackbarHostState: SnackbarHostState): PracticeSharer {
    return remember { IosPracticeSharer() }
}

class IosPracticeSharer : PracticeSharer {
    override fun share(data: String) {
        TODO("Not yet implemented")
    }
}