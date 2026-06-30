package com.incometaxcalc.util

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
    private val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    fun currency(amount: Double): String = currencyFormat.format(amount)
    fun number(amount: Double): String = numberFormat.format(amount)
    fun percent(value: Double): String = "${numberFormat.format(value)}%"
}
