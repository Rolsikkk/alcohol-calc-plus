package com.homedistill.alcoholcalc.ui.screens.heater

import androidx.lifecycle.ViewModel
import com.homedistill.alcoholcalc.core.calculators.HeaterResult
import com.homedistill.alcoholcalc.core.calculators.calculateHeaterPower
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import com.homedistill.alcoholcalc.ui.common.debouncedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HeaterViewModel : ViewModel() {

    private val _ratedVoltageText = MutableStateFlow("220")
    private val _ratedPowerText = MutableStateFlow("2000")
    private val _realVoltageText = MutableStateFlow("230")

    val ratedVoltageText: StateFlow<String> = _ratedVoltageText
    val ratedPowerText: StateFlow<String> = _ratedPowerText
    val realVoltageText: StateFlow<String> = _realVoltageText

    val result: StateFlow<HeaterResult?> = debouncedResult(
        _ratedVoltageText, _ratedPowerText, _realVoltageText, initial = null,
    ) { (vr, pr, vreal) ->
        val ratedVoltage = parseDecimalInput(vr)
        val ratedPower = parseDecimalInput(pr)
        val realVoltage = parseDecimalInput(vreal)
        if (ratedVoltage == null || ratedPower == null || realVoltage == null) {
            null
        } else {
            calculateHeaterPower(ratedVoltage, ratedPower, realVoltage)
        }
    }

    fun onRatedVoltageChange(value: String) { _ratedVoltageText.value = value }
    fun onRatedPowerChange(value: String) { _ratedPowerText.value = value }
    fun onRealVoltageChange(value: String) { _realVoltageText.value = value }
}
