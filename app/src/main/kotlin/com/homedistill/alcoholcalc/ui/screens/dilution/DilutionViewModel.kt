package com.homedistill.alcoholcalc.ui.screens.dilution

import androidx.lifecycle.ViewModel
import com.homedistill.alcoholcalc.core.calculators.DilutionResult
import com.homedistill.alcoholcalc.core.calculators.calculateDilution
import com.homedistill.alcoholcalc.core.calculators.formatDecimal
import com.homedistill.alcoholcalc.core.calculators.massFromVolume
import com.homedistill.alcoholcalc.core.calculators.parseDecimalInput
import com.homedistill.alcoholcalc.core.calculators.stepDecimalText
import com.homedistill.alcoholcalc.core.calculators.volumeFromMass
import com.homedistill.alcoholcalc.ui.common.debouncedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val DEFAULT_V1 = "600"
private const val DEFAULT_P1 = "96"
private const val DEFAULT_TARGET = "40"

/**
 * The mL / g / % triplet for the starting spirit is mutually editable: editing ABV%
 * keeps mL fixed and re-derives g; editing mL or g keeps ABV% fixed and re-derives
 * the other. This lets the user characterize their spirit either by measured volume
 * or by weighed mass.
 */
class DilutionViewModel : ViewModel() {

    private val _v1Text = MutableStateFlow(DEFAULT_V1)
    private val _g1Text = MutableStateFlow(defaultG1Text())
    private val _p1Text = MutableStateFlow(DEFAULT_P1)
    private val _targetText = MutableStateFlow(DEFAULT_TARGET)

    val v1Text: StateFlow<String> = _v1Text
    val g1Text: StateFlow<String> = _g1Text
    val p1Text: StateFlow<String> = _p1Text
    val targetText: StateFlow<String> = _targetText

    val result: StateFlow<DilutionResult> =
        debouncedResult(_v1Text, _p1Text, _targetText, initial = DilutionResult.Invalid) { (v1Text, p1Text, targetText) ->
            val v1 = parseDecimalInput(v1Text)
            val p1 = parseDecimalInput(p1Text)
            val target = parseDecimalInput(targetText)
            if (v1 == null || p1 == null || target == null) {
                DilutionResult.Invalid
            } else {
                calculateDilution(v1, p1, target)
            }
        }

    /** Volume edited directly: ABV% stays fixed, mass is re-derived. */
    fun onV1Change(value: String) {
        _v1Text.value = value
        val v1 = parseDecimalInput(value) ?: return
        val p1 = parseDecimalInput(_p1Text.value) ?: return
        _g1Text.value = formatDecimal(massFromVolume(v1, p1), 0)
    }

    /** Mass edited directly: ABV% stays fixed, volume is re-derived. */
    fun onG1Change(value: String) {
        _g1Text.value = value
        val g1 = parseDecimalInput(value) ?: return
        val p1 = parseDecimalInput(_p1Text.value) ?: return
        _v1Text.value = formatDecimal(volumeFromMass(g1, p1), 0)
    }

    /** ABV% edited directly: volume stays fixed, mass is re-derived. */
    fun onP1Change(value: String) {
        _p1Text.value = value
        val v1 = parseDecimalInput(_v1Text.value) ?: return
        val p1 = parseDecimalInput(value) ?: return
        _g1Text.value = formatDecimal(massFromVolume(v1, p1), 0)
    }

    fun onTargetChange(value: String) { _targetText.value = value }

    fun stepP1(delta: Double) { onP1Change(stepDecimalText(_p1Text.value, delta)) }
    fun stepTarget(delta: Double) { _targetText.value = stepDecimalText(_targetText.value, delta) }

    fun reset() {
        _v1Text.value = DEFAULT_V1
        _p1Text.value = DEFAULT_P1
        _g1Text.value = defaultG1Text()
        _targetText.value = DEFAULT_TARGET
    }

    private companion object {
        fun defaultG1Text(): String {
            val v1 = parseDecimalInput(DEFAULT_V1) ?: 0.0
            val p1 = parseDecimalInput(DEFAULT_P1) ?: 0.0
            return formatDecimal(massFromVolume(v1, p1), 0)
        }
    }
}
