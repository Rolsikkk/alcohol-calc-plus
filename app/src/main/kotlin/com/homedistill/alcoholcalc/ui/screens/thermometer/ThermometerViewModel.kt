package com.homedistill.alcoholcalc.ui.screens.thermometer

import androidx.lifecycle.ViewModel
import com.homedistill.alcoholcalc.core.calculators.correctAlcoholmeterReading
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import com.homedistill.alcoholcalc.ui.common.debouncedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThermometerViewModel : ViewModel() {

    private val _tempText = MutableStateFlow("25")
    private val _apparentText = MutableStateFlow("40")

    val tempText: StateFlow<String> = _tempText
    val apparentText: StateFlow<String> = _apparentText

    val real: StateFlow<Double?> = debouncedResult(_tempText, _apparentText, initial = null) { (t, a) ->
        val temp = parseDecimalInput(t)
        val apparent = parseDecimalInput(a)
        if (temp == null || apparent == null) null else correctAlcoholmeterReading(temp, apparent)
    }

    fun onTempChange(value: String) { _tempText.value = value }
    fun onApparentChange(value: String) { _apparentText.value = value }
}
