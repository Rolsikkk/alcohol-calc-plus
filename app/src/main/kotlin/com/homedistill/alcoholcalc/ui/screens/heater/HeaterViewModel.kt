package com.homedistill.alcoholcalc.ui.screens.heater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.HeaterResult
import com.homedistill.alcoholcalc.core.calculators.calculateHeaterPower
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
class HeaterViewModel : ViewModel() {

    private val _ratedVoltageText = MutableStateFlow("220")
    private val _ratedPowerText = MutableStateFlow("2000")
    private val _realVoltageText = MutableStateFlow("230")

    val ratedVoltageText: StateFlow<String> = _ratedVoltageText
    val ratedPowerText: StateFlow<String> = _ratedPowerText
    val realVoltageText: StateFlow<String> = _realVoltageText

    val result: StateFlow<HeaterResult?> = combine(
        _ratedVoltageText, _ratedPowerText, _realVoltageText,
    ) { vr, pr, vreal -> Triple(vr, pr, vreal) }
        .debounce(DEBOUNCE_MS)
        .map { (vr, pr, vreal) ->
            val ratedVoltage = parseDecimalInput(vr)
            val ratedPower = parseDecimalInput(pr)
            val realVoltage = parseDecimalInput(vreal)
            if (ratedVoltage == null || ratedPower == null || realVoltage == null) {
                null
            } else {
                calculateHeaterPower(ratedVoltage, ratedPower, realVoltage)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onRatedVoltageChange(value: String) { _ratedVoltageText.value = value }
    fun onRatedPowerChange(value: String) { _ratedPowerText.value = value }
    fun onRealVoltageChange(value: String) { _realVoltageText.value = value }
}
