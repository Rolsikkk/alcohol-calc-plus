package com.homedistill.alcoholcalc.ui.screens.hydrometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.HydrometerResult
import com.homedistill.alcoholcalc.core.calculators.correctHydrometerReading
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
class HydrometerViewModel : ViewModel() {

    private val _tempText = MutableStateFlow("25")
    private val _sgText = MutableStateFlow("1040")

    val tempText: StateFlow<String> = _tempText
    val sgText: StateFlow<String> = _sgText

    val result: StateFlow<HydrometerResult?> = combine(_tempText, _sgText) { t, sg -> t to sg }
        .debounce(DEBOUNCE_MS)
        .map { (t, sg) ->
            val temp = parseDecimalInput(t)
            val sgValue = parseDecimalInput(sg)
            if (temp == null || sgValue == null) null else correctHydrometerReading(temp, sgValue)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onTempChange(value: String) { _tempText.value = value }
    fun onSgChange(value: String) { _sgText.value = value }
}
