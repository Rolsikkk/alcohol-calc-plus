package com.homedistill.alcoholcalc.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.SelectionRate
import com.homedistill.alcoholcalc.core.calculators.calculateSelectionRate
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TICK_MS = 1000L

/**
 * Volume is the only manual input. Pressing Start runs a real stopwatch (wall-clock
 * based, so it stays accurate across pauses), and the mL/h and mL/min rates are
 * derived live from volume ÷ elapsed stopwatch time — there's no separate "time"
 * field to fill in by hand.
 */
class TimerViewModel : ViewModel() {

    private val _volumeText = MutableStateFlow("500")
    val volumeText: StateFlow<String> = _volumeText
    fun onVolumeChange(value: String) { _volumeText.value = value }

    private val _isRunning = MutableStateFlow(false)
    private val _elapsedSeconds = MutableStateFlow(0L)
    val isRunning: StateFlow<Boolean> = _isRunning
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds

    val rate: StateFlow<SelectionRate?> = combine(_volumeText, _elapsedSeconds) { volumeStr, elapsed ->
        val volume = parseDecimalInput(volumeStr)
        if (volume == null) null else calculateSelectionRate(volume, elapsed.toDouble())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private var startEpochMs: Long = 0L
    private var accumulatedMs: Long = 0L
    private var tickJob: Job? = null

    fun startStopwatch() {
        if (_isRunning.value) return
        startEpochMs = System.currentTimeMillis()
        _isRunning.value = true
        tickJob = viewModelScope.launch {
            while (isActive) {
                _elapsedSeconds.value = (accumulatedMs + (System.currentTimeMillis() - startEpochMs)) / 1000
                delay(TICK_MS)
            }
        }
    }

    fun stopStopwatch() {
        if (!_isRunning.value) return
        accumulatedMs += System.currentTimeMillis() - startEpochMs
        _isRunning.value = false
        tickJob?.cancel()
        tickJob = null
        _elapsedSeconds.value = accumulatedMs / 1000
    }

    fun resetStopwatch() {
        tickJob?.cancel()
        tickJob = null
        accumulatedMs = 0L
        startEpochMs = 0L
        _isRunning.value = false
        _elapsedSeconds.value = 0L
    }

    override fun onCleared() {
        tickJob?.cancel()
        super.onCleared()
    }
}
