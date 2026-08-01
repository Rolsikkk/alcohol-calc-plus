package com.homedistill.alcoholcalc.ui.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.homedistill.alcoholcalc.BuildConfig
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.data.update.DownloadProgress
import com.homedistill.alcoholcalc.data.update.UpdateRepository

@Composable
fun UpdateGate() {
    val context = LocalContext.current.applicationContext
    val viewModel: UpdateViewModel = viewModel(
        factory = viewModelFactory {
            initializer { UpdateViewModel(UpdateRepository(context)) }
        },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.checkOnLaunch(BuildConfig.VERSION_NAME)
    }

    when (val current = state) {
        is UpdateUiState.Available -> {
            AlertDialog(
                onDismissRequest = viewModel::dismiss,
                title = { Text(stringResource(R.string.update_available_title)) },
                text = { Text(stringResource(R.string.update_available_message, current.info.versionTag)) },
                confirmButton = {
                    TextButton(onClick = viewModel::startDownload) {
                        Text(stringResource(R.string.update_action_update))
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismiss) {
                        Text(stringResource(R.string.update_action_later))
                    }
                },
            )
        }

        is UpdateUiState.Downloading -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.update_downloading_title, current.info.versionTag)) },
                text = { DownloadProgressContent(current.progress) },
                confirmButton = {},
            )
        }

        is UpdateUiState.DownloadFailed -> {
            AlertDialog(
                onDismissRequest = viewModel::dismiss,
                title = { Text(stringResource(R.string.update_failed_title)) },
                text = { Text(stringResource(R.string.update_failed_message)) },
                confirmButton = {
                    TextButton(onClick = viewModel::startDownload) {
                        Text(stringResource(R.string.update_action_retry))
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismiss) {
                        Text(stringResource(R.string.update_action_later))
                    }
                },
            )
        }

        UpdateUiState.Idle -> Unit
    }
}

@Composable
private fun DownloadProgressContent(progress: DownloadProgress) {
    val percent = progress.percent
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        if (percent != null) {
            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(8.dp))
        val downloadedMb = progress.bytesRead / 1024f / 1024f
        val totalMb = if (progress.totalBytes > 0) progress.totalBytes / 1024f / 1024f else null
        val statusText = if (percent != null && totalMb != null) {
            stringResource(R.string.update_progress_known, percent, downloadedMb, totalMb)
        } else {
            stringResource(R.string.update_progress_unknown, downloadedMb)
        }
        Text(statusText)
    }
}
