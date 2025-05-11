package ua.syt0r.kanji.presentation.screen.main.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.sync.SyncConflictResolveStrategy
import ua.syt0r.kanji.core.sync.SyncDataDiffType
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.clickable
import ua.syt0r.kanji.presentation.common.copyCentered
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.Dimens
import ua.syt0r.kanji.presentation.screen.main.SyncDialogState


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SyncDialog(
    state: State<SyncDialogState>,
    cancelSync: () -> Unit,
    resolveConflict: (SyncConflictResolveStrategy) -> Unit,
    navigateToAccount: () -> Unit
) {

    val strings = resolveString { syncDialog }
    val dialogContent: @Composable ColumnScope.() -> Unit
    val dialogButtons: @Composable () -> Unit

    when (val currentState = state.value) {
        SyncDialogState.Hidden -> return

        SyncDialogState.Uploading -> {
            dialogContent = {
                LoadingLayout(
                    imageVector = Icons.Outlined.CloudUpload,
                    message = strings.uploadingMessage
                )
            }
            dialogButtons = {
                DialogButton(
                    onClick = cancelSync,
                    imageVector = Icons.Outlined.Close,
                    label = strings.buttonCancel
                )
            }
        }

        SyncDialogState.Downloading -> {
            dialogContent = {
                LoadingLayout(
                    imageVector = Icons.Outlined.CloudDownload,
                    message = strings.downloadingMessage
                )
            }
            dialogButtons = {
                DialogButton(
                    onClick = cancelSync,
                    imageVector = Icons.Outlined.Close,
                    label = strings.buttonCancel
                )
            }
        }

        is SyncDialogState.Conflict -> {
            dialogContent = {
                val title: String
                val message: String

                when (currentState.diffType) {
                    SyncDataDiffType.RemoteNewer -> {
                        title = strings.conflictRemoteNewerTitle
                        message = strings.conflictRemoteNewerMessage
                    }

                    SyncDataDiffType.Incompatible -> {
                        title = strings.conflictIncompatibleTitle
                        message = strings.conflictIncompatibleMessage
                    }

                    else -> error("Unexpected diffType[${currentState.diffType}] for conflict state")
                }

                MessageLayout(
                    title = title,
                    message = message,
                    imageVector = Icons.Outlined.CloudSync
                )
            }
            dialogButtons = {
                if (currentState.diffType == SyncDataDiffType.Incompatible) {
                    DialogButton(
                        onClick = { resolveConflict(SyncConflictResolveStrategy.UploadLocal) },
                        imageVector = Icons.Outlined.CloudUpload,
                        label = strings.buttonUpload
                    )
                }

                if (
                    currentState.diffType in setOf(
                        SyncDataDiffType.RemoteNewer,
                        SyncDataDiffType.Incompatible
                    )
                ) {
                    DialogButton(
                        onClick = { resolveConflict(SyncConflictResolveStrategy.DownloadRemote) },
                        imageVector = Icons.Outlined.CloudDownload,
                        label = strings.buttonDownload
                    )
                }
                DialogButton(
                    onClick = cancelSync,
                    imageVector = Icons.Outlined.Close,
                    label = strings.buttonCancel
                )
            }
        }

        is SyncDialogState.Error.Api -> {
            dialogContent = {
                val title: String
                val message: String

                when (val issue = currentState.issue) {
                    ApiRequestIssue.NoConnection -> {
                        title = strings.errorNoNetworkTitle
                        message = strings.errorNoNetworkMessage
                    }

                    ApiRequestIssue.NoSubscription -> {
                        title = strings.errorNoSubscriptionTitle
                        message = strings.errorNoSubscriptionMessage
                    }

                    ApiRequestIssue.NotAuthenticated -> {
                        title = strings.errorNoNetworkTitle
                        message = strings.errorNoNetworkMessage
                    }

                    is ApiRequestIssue.Other -> {
                        title = strings.errorUnexpectedErrorTitle
                        message = issue.throwable.message ?: strings.errorUnexpectedErrorMessage
                    }
                }

                MessageLayout(
                    title = title,
                    message = message,
                    imageVector = Icons.Outlined.SyncProblem
                )
            }
            dialogButtons = {
                when (currentState.issue) {
                    ApiRequestIssue.NoSubscription,
                    ApiRequestIssue.NotAuthenticated -> {
                        DialogButton(
                            onClick = navigateToAccount,
                            imageVector = Icons.Outlined.AccountCircle,
                            label = strings.buttonAccount
                        )
                    }

                    else -> Unit
                }
                DialogButton(
                    onClick = cancelSync,
                    imageVector = Icons.Outlined.Close,
                    label = strings.buttonCancel
                )
            }
        }

        is SyncDialogState.Error.Unsupported -> {
            dialogContent = {
                MessageLayout(
                    title = strings.errorUnsupportedDataTitle,
                    message = strings.errorUnsupportedDataMessage,
                )
            }
            dialogButtons = {
                DialogButton(
                    onClick = { resolveConflict(SyncConflictResolveStrategy.UploadLocal) },
                    imageVector = Icons.Outlined.CloudUpload,
                    label = strings.buttonUpload
                )
                DialogButton(
                    onClick = cancelSync,
                    imageVector = Icons.Outlined.Close,
                    label = strings.buttonCancel
                )
            }
        }
    }

    MultiplatformDialog(
        onDismissRequest = {},
        title = { Text(strings.title) },
        content = {
            Column(
                modifier = Modifier
            ) {
                dialogContent()
                Spacer(Modifier.height(Dimens.ContentPaddingSmall))
                dialogButtons()
            }
        },
        buttons = { }
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DialogButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick)
            .padding(horizontal = Dimens.SpacingBig, vertical = Dimens.SpacingBig),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
        Text(
            text = label,
            modifier = Modifier,
            style = MaterialTheme.typography.labelLarge.copyCentered()
        )
    }
}

@Composable
private fun LoadingLayout(
    imageVector: ImageVector,
    message: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMid),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpacingBig)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        androidx.compose.material.LinearProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
        Text(message)
    }
}

@Composable
private fun MessageLayout(
    title: String,
    message: String,
    imageVector: ImageVector = Icons.Outlined.CloudSync
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpacingBig)
    ) {

        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = Dimens.ContentPaddingSmall)
                .size(60.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = Dimens.SpacingSmall)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}