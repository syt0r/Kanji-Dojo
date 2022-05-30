package ua.syt0r.kanji.presentation.screen.screen.about.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onUpButtonClick: () -> Unit = {},
    openLink: (String) -> Unit = {}
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
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            ElevatedCard(
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
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = stringResource(R.string.about_description))

            Spacer(modifier = Modifier.height(8.dp))

            ClickableRow(
                content = {
                    Text(
                        text = "Github",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Source code & issues",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = { openLink("https://github.com/SYtor/Kanji-Dojo") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Credits", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            ClickableRow(
                content = {
                    Text(
                        text = "KaniVG",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Provides writing strokes",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "License: Creative Commons Attribution-Share Alike 3.0",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = { openLink("https://kanjivg.tagaini.net/") }
            )

            ClickableRow(
                content = {
                    Text(
                        text = "Kanji Dic",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Provides characters info, such as meanings, readings and classifications",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "License: Creative Commons Attribution-Share Alike 3.0",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = { openLink("http://www.edrdg.org/wiki/index.php/KANJIDIC_Project") }
            )

            ClickableRow(
                content = {
                    Text(
                        text = "Tanos by Jonathan Waller",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Provides JLPT classification for kanji",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "License: Creative Commons BY",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = { openLink("http://www.tanos.co.uk/jlpt/") }
            )

            Spacer(modifier = Modifier.height(30.dp))

        }

    }

}

@Composable
private fun ClickableRow(
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {

    Column(
        Modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        content()
    }

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        AboutScreenUI()
    }
}
