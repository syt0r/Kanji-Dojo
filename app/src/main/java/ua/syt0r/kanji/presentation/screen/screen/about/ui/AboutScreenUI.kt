package ua.syt0r.kanji.presentation.screen.screen.about.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

            Card(
                modifier = Modifier
                    .size(128.dp)
                    .align(CenterHorizontally)
            ) {

                Box {

                    Icon(
                        painter = painterResource(R.drawable.drawable_icon_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )

                    Icon(
                        painter = painterResource(R.drawable.drawable_icon_foreground),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )

                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.app_name, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = stringResource(R.string.about_description))

            Spacer(modifier = Modifier.height(8.dp))

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
