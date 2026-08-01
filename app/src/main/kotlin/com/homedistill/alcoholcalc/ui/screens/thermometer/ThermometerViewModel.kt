package com.homedistill.alcoholcalc.ui.screens.thermometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.correctAlcoholmeterReading
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
class ThermometerViewModel : ViewModel() {

    private val _tempText = MutableStateFlow("25")
    private val _apparentText = MutableStateFlow("40")

    val tempText: StateFlow<String> = _tempText
    val apparentText: StateFlow<String> = _apparentText

    val real: StateFlow<Double?> = combine(_tempText, _apparentText) { t, a -> t to a }
        .debounce(DEBOUNCE_MS)
        .map { (t, a) ->
            val temp = parseDecimalInput(t)
            val apparent = parseDecimalInput(a)
            if (temp == null || apparent == null) null else correctAlcoholmeterReading(temp, apparent)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onTempChange(value: String) { _tempText.value = value }
    fun onApparentChange(value: String) { _apparentText.value = value }
}
