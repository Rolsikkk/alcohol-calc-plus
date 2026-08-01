package com.homedistill.alcoholcalc.ui.screens.pressure

import androidx.lifecycle.ViewModel
import com.homedistill.alcoholcalc.core.calculators.PressureResult
import com.homedistill.alcoholcalc.core.calculators.calculatePressureCorrection
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import com.homedistill.alcoholcalc.ui.common.debouncedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PressureViewModel : ViewModel() {

    private val _pressureText = MutableStateFlow("760")
    private val _cubeAbvText = MutableStateFlow("40")

    val pressureText: StateFlow<String> = _pressureText
    val cubeAbvText: StateFlow<String> = _cubeAbvText

    val result: StateFlow<PressureResult?> = debouncedResult(_pressureText, _cubeAbvText, initial = null) { (p, abv) ->
        val pressure = parseDecimalInput(p)
        val cubeAbv = parseDecimalInput(abv)
        if (pressure == null || cubeAbv == null) null else calculatePressureCorrection(pressure, cubeAbv)
    }

    fun onPressureChange(value: String) { _pressureText.value = value }
    fun onCubeAbvChange(value: String) { _cubeAbvText.value = value }
}
