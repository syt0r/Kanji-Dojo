package ua.syt0r.kanji.presentation.screen.screen.about.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.BuildConfig
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreenUI(
    onUpButtonClick: () -> Unit,
    onGithubClick: () -> Unit
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            val iconSize = 128.dp

            Card(
                modifier = Modifier
                    .size(iconSize)
                    .align(CenterHorizontally)
            ) {

                Box {

                    Icon(
                        painter = painterResource(R.drawable.drawable_icon_background),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .align(Center),
                        tint = Color.Unspecified
                    )

                    Icon(
                        painter = painterResource(R.drawable.drawable_icon_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .align(Center),
                        tint = Color.Unspecified
                    )

                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = stringResource(R.string.app_name))
            Text(text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
            Text(text = stringResource(R.string.about_description))

            Button(onClick = onGithubClick) {
                Text(text = stringResource(R.string.about_github))
            }

        }

    }

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        AboutScreenUI(
            onUpButtonClick = {},
            onGithubClick = {}
        )
    }
}
