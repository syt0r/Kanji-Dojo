package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.material.Text
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.runtime.Composable

@Composable
fun CustomTopBar(
    title: String,
    upButtonVisible: Boolean,
    onUpButtonClick: () -> Unit = {}
) {

    SmallTopAppBar(
        title = { Text(text = title) }
    )

}
