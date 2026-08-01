package com.homedistill.alcoholcalc.ui.screens.hydrometer

import androidx.lifecycle.ViewModel
import com.homedistill.alcoholcalc.core.calculators.HydrometerResult
import com.homedistill.alcoholcalc.core.calculators.correctHydrometerReading
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import com.homedistill.alcoholcalc.ui.common.debouncedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HydrometerViewModel : ViewModel() {

    private val _tempText = MutableStateFlow("25")
    private val _sgText = MutableStateFlow("1040")

    val tempText: StateFlow<String> = _tempText
    val sgText: StateFlow<String> = _sgText

    val result: StateFlow<HydrometerResult?> = debouncedResult(_tempText, _sgText, initial = null) { (t, sg) ->
        val temp = parseDecimalInput(t)
        val sgValue = parseDecimalInput(sg)
        if (temp == null || sgValue == null) null else correctHydrometerReading(temp, sgValue)
    }

    fun onTempChange(value: String) { _tempText.value = value }
    fun onSgChange(value: String) { _sgText.value = value }
}
