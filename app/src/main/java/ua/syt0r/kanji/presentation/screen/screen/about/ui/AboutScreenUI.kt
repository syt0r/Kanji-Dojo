package ua.syt0r.kanji.presentation.screen.screen.about.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.BuildConfig
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar

@Composable
fun AboutScreenUI(
    onUpButtonClick: () -> Unit,
    onGithubClick: () -> Unit
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.about_title),
                upButtonVisible = true,
                onUpButtonClick = onUpButtonClick
            )
        }
    ) {

        Column {

            Icon(
                painter = painterResource(R.drawable.ic_foreground),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )

            Text(text = stringResource(R.string.app_name))
            Text(text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
            Text(text = stringResource(R.string.about_description))

            Button(onClick = onGithubClick) {
                Text(text = stringResource(R.string.about_github))
            }

        }

    }


}
