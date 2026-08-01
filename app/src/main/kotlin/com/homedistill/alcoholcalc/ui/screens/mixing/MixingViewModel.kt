package com.homedistill.alcoholcalc.ui.screens.mixing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.core.calculators.MixingResult
import com.homedistill.alcoholcalc.core.calculators.SolutionInput
import com.homedistill.alcoholcalc.core.calculators.calculateMixing
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
const val SOLUTION_SLOTS = 3

/**
 * Text-field state for one of the up-to-three solutions being mixed. Volume, mass
 * and ABV% are mutually editable: editing ABV% keeps volume fixed and re-derives
 * mass; editing volume or mass keeps ABV% fixed and re-derives the other.
 */
data class SolutionFields(val volumeText: String, val massText: String, val abvText: String)

private fun solutionFields(volumeText: String, abvText: String): SolutionFields {
    val volume = parseDecimalInput(volumeText)
    val abv = parseDecimalInput(abvText)
    val massText = if (volume != null && abv != null) formatDecimal(massFromVolume(volume, abv), 0) else ""
    return SolutionFields(volumeText, massText, abvText)
}

@OptIn(FlowPreview::class)
class MixingViewModel : ViewModel() {

    private val _solution1 = MutableStateFlow(solutionFields("500", "96"))
    private val _solution2 = MutableStateFlow(solutionFields("500", "0"))
    private val _solution3 = MutableStateFlow(solutionFields("", ""))

    val solution1: StateFlow<SolutionFields> = _solution1
    val solution2: StateFlow<SolutionFields> = _solution2
    val solution3: StateFlow<SolutionFields> = _solution3

    val result: StateFlow<MixingResult> = combine(_solution1, _solution2, _solution3) { s1, s2, s3 ->
        listOf(s1, s2, s3)
    }.debounce(DEBOUNCE_MS).map { fields ->
        val solutions = fields.map { f ->
            val volume = parseDecimalInput(f.volumeText) ?: 0.0
            val abv = parseDecimalInput(f.abvText) ?: 0.0
            SolutionInput(volume, abv)
        }
        calculateMixing(solutions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), calculateMixing(emptyList()))

    /** Volume edited directly: ABV% stays fixed, mass is re-derived. */
    fun onVolumeChange(index: Int, value: String) = updateSolution(index) { current ->
        val abv = parseDecimalInput(current.abvText)
        val volume = parseDecimalInput(value)
        val mass = if (volume != null && abv != null) formatDecimal(massFromVolume(volume, abv), 0) else current.massText
        current.copy(volumeText = value, massText = mass)
    }

    /** Mass edited directly: ABV% stays fixed, volume is re-derived. */
    fun onMassChange(index: Int, value: String) = updateSolution(index) { current ->
        val abv = parseDecimalInput(current.abvText)
        val mass = parseDecimalInput(value)
        val volume = if (mass != null && abv != null) formatDecimal(volumeFromMass(mass, abv), 0) else current.volumeText
        current.copy(volumeText = volume, massText = value)
    }

    /** ABV% edited directly: volume stays fixed, mass is re-derived. */
    fun onAbvChange(index: Int, value: String) = updateSolution(index) { current ->
        val volume = parseDecimalInput(current.volumeText)
        val abv = parseDecimalInput(value)
        val mass = if (volume != null && abv != null) formatDecimal(massFromVolume(volume, abv), 0) else current.massText
        current.copy(massText = mass, abvText = value)
    }

    private fun updateSolution(index: Int, transform: (SolutionFields) -> SolutionFields) {
        when (index) {
            0 -> _solution1.value = transform(_solution1.value)
            1 -> _solution2.value = transform(_solution2.value)
            2 -> _solution3.value = transform(_solution3.value)
        }
    }

    fun reset() {
        _solution1.value = solutionFields("500", "96")
        _solution2.value = solutionFields("500", "0")
        _solution3.value = solutionFields("", "")
    }
}
