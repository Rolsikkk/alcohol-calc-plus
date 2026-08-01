package com.homedistill.alcoholcalc.ui.screens.pressure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.PressureResult
import com.homedistill.alcoholcalc.core.calculators.calculatePressureCorrection
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DEBOUNCE_MS = 150L

@OptIn(FlowPreview::class)
class PressureViewModel : ViewModel() {

    private val _pressureText = MutableStateFlow("760")
    private val _cubeAbvText = MutableStateFlow("40")

    val pressureText: StateFlow<String> = _pressureText
    val cubeAbvText: StateFlow<String> = _cubeAbvText

    val result: StateFlow<PressureResult?> = combine(_pressureText, _cubeAbvText) { p, abv -> p to abv }
        .debounce(DEBOUNCE_MS)
        .map { (p, abv) ->
            val pressure = parseDecimalInput(p)
            val cubeAbv = parseDecimalInput(abv)
            if (pressure == null || cubeAbv == null) null else calculatePressureCorrection(pressure, cubeAbv)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onPressureChange(value: String) { _pressureText.value = value }
    fun onCubeAbvChange(value: String) { _cubeAbvText.value = value }
}
