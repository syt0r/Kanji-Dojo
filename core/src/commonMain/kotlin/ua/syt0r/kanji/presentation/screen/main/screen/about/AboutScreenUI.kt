package ua.syt0r.kanji.presentation.screen.main.screen.about

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
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.icon.extraicons.AppIconBackground
import ua.syt0r.kanji.presentation.common.resources.icon.extraicons.AppIconForeground
import ua.syt0r.kanji.presentation.common.resources.string.resolveString

private const val KanjiDojoGithubLink = "https://github.com/syt0r/Kanji-Dojo"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreenUI(
    onUpButtonClick: () -> Unit = {},
    openLink: (String) -> Unit = {}
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = resolveString { aboutTitle }
                    )
                },
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
                        imageVector = ExtraIcons.AppIconBackground,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )

                    Icon(
                        imageVector = ExtraIcons.AppIconForeground,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )

                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = resolveString { appName },
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = resolveString { aboutVersion }, // TODO version
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = resolveString { aboutDescription })

            Spacer(modifier = Modifier.height(8.dp))

            ClickableRow(
                content = {
                    Text(
                        text = resolveString { aboutGithub },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = resolveString { aboutGithubDescription },
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = { openLink(KanjiDojoGithubLink) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = resolveString { aboutCreditsTitle },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AboutCredit.values().forEach { creditItem ->
                CreditItem(
                    creditItem = creditItem,
                    onClick = { openLink(creditItem.url) }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

        }

    }

}

@Composable
private fun CreditItem(
    creditItem: AboutCredit,
    onClick: () -> Unit
) {
    ClickableRow(
        content = {
            Text(
                text = resolveString(creditItem.title),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = resolveString(creditItem.description),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = resolveString { aboutLicenseTemplate }, // TODO stringResource(creditItem.license)
                style = MaterialTheme.typography.bodySmall
            )
        },
        onClick = onClick
    )
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
