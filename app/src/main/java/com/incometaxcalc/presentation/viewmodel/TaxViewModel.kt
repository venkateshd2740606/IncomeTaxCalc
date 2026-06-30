package com.incometaxcalc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.incometaxcalc.domain.engine.RegimeResult
import com.incometaxcalc.domain.engine.TaxEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TaxUiState(
    val annualIncome: String = "1200000",
    val deduction80C: String = "150000",
    val oldResult: RegimeResult? = null,
    val newResult: RegimeResult? = null
) {
    val betterRegime: String?
        get() {
            val old = oldResult ?: return null
            val new = newResult ?: return null
            return when {
                old.taxPayable < new.taxPayable -> "Old regime saves more"
                new.taxPayable < old.taxPayable -> "New regime saves more"
                else -> "Both regimes have equal tax"
            }
        }

    val savingsAmount: Double?
        get() {
            val old = oldResult ?: return null
            val new = newResult ?: return null
            return kotlin.math.abs(old.taxPayable - new.taxPayable)
        }
}

@HiltViewModel
class TaxViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(TaxUiState())
    val state = _state.asStateFlow()

    init { recalculate() }

    fun setAnnualIncome(value: String) {
        _state.update { it.copy(annualIncome = value) }
        recalculate()
    }

    fun setDeduction80C(value: String) {
        _state.update { it.copy(deduction80C = value) }
        recalculate()
    }

    private fun recalculate() {
        val income = _state.value.annualIncome.toDoubleOrNull()
        val deduction = _state.value.deduction80C.toDoubleOrNull() ?: 0.0
        if (income == null || income < 0) {
            _state.update { it.copy(oldResult = null, newResult = null) }
            return
        }
        _state.update {
            it.copy(
                oldResult = TaxEngine.calculateOldRegime(income, deduction),
                newResult = TaxEngine.calculateNewRegime(income)
            )
        }
    }
}
