package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.secondary
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily

@Composable
fun CustomTopBar(
    title: String,
    upButtonVisible: Boolean,
    onUpButtonClick: () -> Unit = {}
) {

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                fontFamily = stylizedFontFamily
            )
        },
        backgroundColor = secondary,
        contentColor = Color.White,
        navigationIcon = if (upButtonVisible) {
            {
                IconButton(
                    onClick = onUpButtonClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
                        contentDescription = null
                    )
                }
            }
        } else null
    )

}
