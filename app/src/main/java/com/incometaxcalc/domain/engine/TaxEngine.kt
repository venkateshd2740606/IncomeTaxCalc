package com.incometaxcalc.domain.engine

data class TaxSlab(val upperLimit: Double?, val ratePercent: Double)

data class RegimeResult(
    val regime: TaxRegime,
    val grossIncome: Double,
    val standardDeduction: Double,
    val section80C: Double,
    val taxableIncome: Double,
    val taxPayable: Double,
    val monthlyTds: Double
)

enum class TaxRegime { OLD, NEW }

object TaxEngine {
    const val FY_LABEL = "FY 2025-26"
    const val OLD_STANDARD_DEDUCTION = 50_000.0
    const val NEW_STANDARD_DEDUCTION = 75_000.0
    const val MAX_80C = 150_000.0

    val OLD_SLABS = listOf(
        TaxSlab(250_000.0, 0.0),
        TaxSlab(500_000.0, 5.0),
        TaxSlab(1_000_000.0, 20.0),
        TaxSlab(null, 30.0)
    )

    val NEW_SLABS = listOf(
        TaxSlab(400_000.0, 0.0),
        TaxSlab(800_000.0, 5.0),
        TaxSlab(1_200_000.0, 10.0),
        TaxSlab(1_600_000.0, 15.0),
        TaxSlab(2_000_000.0, 20.0),
        TaxSlab(2_400_000.0, 25.0),
        TaxSlab(null, 30.0)
    )

    val TAX_SAVING_TIPS = listOf(
        "Invest up to ₹1.5 lakh in ELSS, PPF, or NSC under Section 80C (old regime).",
        "Pay health insurance premiums — Section 80D allows deductions for self and family.",
        "Home loan principal repayment qualifies under 80C; interest under Section 24(b).",
        "NPS contributions get an extra ₹50,000 deduction under Section 80CCD(1B).",
        "Donations to eligible charities qualify under Section 80G.",
        "Compare old vs new regime annually — the better option depends on your deductions.",
        "HRA exemption can significantly reduce taxable income in the old regime.",
        "New regime has higher standard deduction (₹75,000) but fewer exemptions."
    )

    fun calculateOldRegime(annualIncome: Double, deduction80C: Double): RegimeResult {
        val capped80C = deduction80C.coerceIn(0.0, MAX_80C)
        val taxable = (annualIncome - OLD_STANDARD_DEDUCTION - capped80C).coerceAtLeast(0.0)
        val tax = computeSlabTax(taxable, OLD_SLABS)
        return RegimeResult(
            regime = TaxRegime.OLD,
            grossIncome = annualIncome,
            standardDeduction = OLD_STANDARD_DEDUCTION,
            section80C = capped80C,
            taxableIncome = taxable,
            taxPayable = tax,
            monthlyTds = tax / 12.0
        )
    }

    fun calculateNewRegime(annualIncome: Double): RegimeResult {
        val taxable = (annualIncome - NEW_STANDARD_DEDUCTION).coerceAtLeast(0.0)
        val tax = computeSlabTax(taxable, NEW_SLABS)
        return RegimeResult(
            regime = TaxRegime.NEW,
            grossIncome = annualIncome,
            standardDeduction = NEW_STANDARD_DEDUCTION,
            section80C = 0.0,
            taxableIncome = taxable,
            taxPayable = tax,
            monthlyTds = tax / 12.0
        )
    }

    fun computeSlabTax(taxableIncome: Double, slabs: List<TaxSlab>): Double {
        if (taxableIncome <= 0) return 0.0
        var tax = 0.0
        var prevLimit = 0.0
        for (slab in slabs) {
            val ceiling = slab.upperLimit ?: Double.MAX_VALUE
            if (taxableIncome <= prevLimit) break
            val amountInSlab = minOf(taxableIncome, ceiling) - prevLimit
            if (amountInSlab > 0) tax += amountInSlab * slab.ratePercent / 100.0
            prevLimit = ceiling
            if (slab.upperLimit == null) break
        }
        return tax
    }
}
