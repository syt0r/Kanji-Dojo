package ua.syt0r.kanji.presentation.screen.main.screen.sponsor

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import org.jetbrains.compose.resources.getStringArray
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.NetworkApi
import ua.syt0r.kanji.core.format
import ua.syt0r.kanji.core.toLocalDateTime
import ua.syt0r.kanji.presentation.common.CommonDateFormat
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.sponsor_recent_donations_error
import ua.syt0r.kanji.sponsor_recent_donations_messages
import ua.syt0r.kanji.sponsor_recent_donations_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SponsorScreenUI(
    onUpClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onUpClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                title = { }
            )
        }
    ) {

        Column(
            modifier = Modifier.padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .wrapContentWidth()
                .padding(20.dp)
                .widthIn(max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SponsorScreenDefaultContent()

            content()

        }

    }

}

@Composable
fun ColumnScope.SponsorScreenDefaultContent() {

    Text(
        text = resolveString { appName },
        style = MaterialTheme.typography.headlineLarge
    )

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(20.dp)
    ) {
        Text(
            text = resolveString { sponsor.message },
            textAlign = TextAlign.Justify
        )
    }

    RecentDonations()

}


sealed interface DonationsSectionState {
    data object Loading : DonationsSectionState
    data class Loaded(val items: List<Item>) : DonationsSectionState {
        data class Item(val date: LocalDate, val message: String)
    }

    data class Error(val message: String) : DonationsSectionState
}

@Composable
private fun RecentDonations() {

    val networkApi = koinInject<NetworkApi>()

    val sectionState = remember {
        MutableStateFlow<DonationsSectionState>(DonationsSectionState.Loading)
    }

    LaunchedEffect(Unit) {

        networkApi.getDonations()
            .map {
                val messagesPool = getStringArray(Res.array.sponsor_recent_donations_messages)
                    .shuffled()

                it.mapIndexed { index, donation ->
                    DonationsSectionState.Loaded.Item(
                        date = Instant.fromEpochMilliseconds(donation.time)
                            .toLocalDateTime().date,
                        message = messagesPool[index % messagesPool.size]
                            .format(donation.amountJpy.toLong().toString())
                    )
                }
            }
            .onSuccess {
                sectionState.value = DonationsSectionState.Loaded(it)
            }
            .onFailure {
                sectionState.value = DonationsSectionState.Error(
                    message = it.message ?: it.toString()
                )
            }
    }

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = stringResource(Res.string.sponsor_recent_donations_title),
            style = MaterialTheme.typography.titleMedium
        )

        val composeState = sectionState.collectAsState()

        AnimatedContent(
            targetState = composeState.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (it) {
                DonationsSectionState.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                )

                is DonationsSectionState.Error -> {
                    Text(stringResource(Res.string.sponsor_recent_donations_error, it.message))
                }

                is DonationsSectionState.Loaded -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(it.items) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = it.date.format(CommonDateFormat),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = it.message,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}
