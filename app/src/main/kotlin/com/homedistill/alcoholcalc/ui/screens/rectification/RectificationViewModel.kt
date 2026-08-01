package com.homedistill.alcoholcalc.ui.screens.rectification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.DEFAULT_BODY_PCT
import com.homedistill.alcoholcalc.core.calculators.DEFAULT_HEADS_PCT
import com.homedistill.alcoholcalc.core.calculators.DEFAULT_TAILS_PCT
import com.homedistill.alcoholcalc.core.calculators.RectificationResult
import com.homedistill.alcoholcalc.core.calculators.calculateRectification
import com.homedistill.alcoholcalc.core.calculators.formatDecimal
import com.homedistill.alcoholcalc.core.calculators.massFromVolume
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import com.homedistill.alcoholcalc.core.calculators.volumeFromMass
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DEBOUNCE_MS = 150L
private const val DEFAULT_V = "3000"
private const val DEFAULT_P = "40"

/**
 * The raw spirit's mL / g / % triplet is mutually editable, same rule as the
 * dilution screen: editing ABV% keeps volume fixed and re-derives mass; editing
 * volume or mass keeps ABV% fixed and re-derives the other.
 */
@OptIn(FlowPreview::class)
class RectificationViewModel : ViewModel() {

    private val _vText = MutableStateFlow(DEFAULT_V)
    private val _massText = MutableStateFlow(defaultMassText())
    private val _pText = MutableStateFlow(DEFAULT_P)
    private val _headsText = MutableStateFlow(DEFAULT_HEADS_PCT.toString())
    private val _bodyText = MutableStateFlow(DEFAULT_BODY_PCT.toString())
    private val _tailsText = MutableStateFlow(DEFAULT_TAILS_PCT.toString())

    val vText: StateFlow<String> = _vText
    val massText: StateFlow<String> = _massText
    val pText: StateFlow<String> = _pText
    val headsText: StateFlow<String> = _headsText
    val bodyText: StateFlow<String> = _bodyText
    val tailsText: StateFlow<String> = _tailsText

    val result: StateFlow<RectificationResult?> = combine(
        combine(_vText, _pText) { v, p -> v to p },
        combine(_headsText, _bodyText, _tailsText) { h, b, t -> Triple(h, b, t) },
    ) { vp, hbt -> vp to hbt }
        .debounce(DEBOUNCE_MS)
        .map { (vp, hbt) ->
            val v = parseDecimalInput(vp.first)
            val p = parseDecimalInput(vp.second)
            val heads = parseDecimalInput(hbt.first)
            val body = parseDecimalInput(hbt.second)
            val tails = parseDecimalInput(hbt.third)
            if (v == null || p == null || heads == null || body == null || tails == null) {
                null
            } else {
                calculateRectification(v, p, heads, body, tails)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Volume edited directly: ABV% stays fixed, mass is re-derived. */
    fun onVChange(value: String) {
        _vText.value = value
        val v = parseDecimalInput(value) ?: return
        val p = parseDecimalInput(_pText.value) ?: return
        _massText.value = formatDecimal(massFromVolume(v, p), 0)
    }

    /** Mass edited directly: ABV% stays fixed, volume is re-derived. */
    fun onMassChange(value: String) {
        _massText.value = value
        val mass = parseDecimalInput(value) ?: return
        val p = parseDecimalInput(_pText.value) ?: return
        _vText.value = formatDecimal(volumeFromMass(mass, p), 0)
    }

    /** ABV% edited directly: volume stays fixed, mass is re-derived. */
    fun onPChange(value: String) {
        _pText.value = value
        val v = parseDecimalInput(_vText.value) ?: return
        val p = parseDecimalInput(value) ?: return
        _massText.value = formatDecimal(massFromVolume(v, p), 0)
    }

    fun onHeadsChange(value: String) { _headsText.value = value }
    fun onBodyChange(value: String) { _bodyText.value = value }
    fun onTailsChange(value: String) { _tailsText.value = value }

    fun reset() {
        _vText.value = DEFAULT_V
        _pText.value = DEFAULT_P
        _massText.value = defaultMassText()
        _headsText.value = DEFAULT_HEADS_PCT.toString()
        _bodyText.value = DEFAULT_BODY_PCT.toString()
        _tailsText.value = DEFAULT_TAILS_PCT.toString()
    }

    private companion object {
        fun defaultMassText(): String {
            val v = parseDecimalInput(DEFAULT_V) ?: 0.0
            val p = parseDecimalInput(DEFAULT_P) ?: 0.0
            return formatDecimal(massFromVolume(v, p), 0)
        }
    }
}
