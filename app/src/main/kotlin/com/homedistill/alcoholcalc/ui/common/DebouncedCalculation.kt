package com.homedistill.alcoholcalc.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DEFAULT_DEBOUNCE_MS = 150L

/**
 * Shared shape for every calculator screen's result flow: combine the raw text
 * fields, debounce typing, then recompute. Used instead of a bespoke
 * combine/debounce/map/stateIn block in each ViewModel.
 */
@OptIn(FlowPreview::class)
fun <T> ViewModel.debouncedResult(
    vararg inputs: StateFlow<String>,
    initial: T,
    debounceMs: Long = DEFAULT_DEBOUNCE_MS,
    calculate: (List<String>) -> T,
): StateFlow<T> =
    combine(inputs.toList()) { values -> values.toList() }
        .debounce(debounceMs)
        .map(calculate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initial)
