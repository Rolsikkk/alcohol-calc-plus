package com.homedistill.alcoholcalc.ui.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.data.update.DownloadProgress
import com.homedistill.alcoholcalc.data.update.UpdateInfo
import com.homedistill.alcoholcalc.data.update.UpdateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UpdateUiState {
    object Idle : UpdateUiState()
    data class Available(val info: UpdateInfo) : UpdateUiState()
    data class Downloading(val info: UpdateInfo, val progress: DownloadProgress) : UpdateUiState()
    data class DownloadFailed(val info: UpdateInfo) : UpdateUiState()
}

class UpdateViewModel(private val repository: UpdateRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState: StateFlow<UpdateUiState> = _uiState

    /** Checks once per process lifetime; safe to call repeatedly, only the first call does anything. */
    private var checked = false

    fun checkOnLaunch(currentVersionName: String) {
        if (checked) return
        checked = true
        viewModelScope.launch {
            val info = repository.checkForUpdate(currentVersionName) ?: return@launch
            _uiState.value = UpdateUiState.Available(info)
        }
    }

    fun startDownload() {
        val info = (_uiState.value as? UpdateUiState.Available)?.info
            ?: (_uiState.value as? UpdateUiState.DownloadFailed)?.info
            ?: return
        _uiState.value = UpdateUiState.Downloading(info, DownloadProgress(0L, -1L))
        viewModelScope.launch {
            val file = repository.downloadApk(info) { progress ->
                _uiState.value = UpdateUiState.Downloading(info, progress)
            }
            if (file == null) {
                _uiState.value = UpdateUiState.DownloadFailed(info)
            } else {
                repository.installApk(file)
                _uiState.value = UpdateUiState.Idle
            }
        }
    }

    /** Dismisses for the rest of this app session; the check runs again on the next cold start. */
    fun dismiss() {
        _uiState.value = UpdateUiState.Idle
    }
}
