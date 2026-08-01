package com.homedistill.alcoholcalc.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.SelectionRate
import com.homedistill.alcoholcalc.core.calculators.calculateSelectionRate
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import com.homedistill.alcoholcalc.ui.common.debouncedResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TICK_MS = 1000L

class TimerViewModel : ViewModel() {

    // --- Selection rate calculator ---
    private val _volumeText = MutableStateFlow("500")
    private val _timeText = MutableStateFlow("600")

    val volumeText: StateFlow<String> = _volumeText
    val timeText: StateFlow<String> = _timeText

    val rate: StateFlow<SelectionRate?> = debouncedResult(_volumeText, _timeText, initial = null) { (v, t) ->
        val volume = parseDecimalInput(v)
        val time = parseDecimalInput(t)
        if (volume == null || time == null) null else calculateSelectionRate(volume, time)
    }

    fun onVolumeChange(value: String) { _volumeText.value = value }
    fun onTimeChange(value: String) { _timeText.value = value }

    // --- Real stopwatch, computed from wall-clock deltas so it stays accurate across pauses ---
    private val _isRunning = MutableStateFlow(false)
    private val _elapsedSeconds = MutableStateFlow(0L)
    val isRunning: StateFlow<Boolean> = _isRunning
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds

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
