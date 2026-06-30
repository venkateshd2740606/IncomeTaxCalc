package com.incometaxcalc.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.incometaxcalc.domain.engine.RegimeResult
import com.incometaxcalc.domain.engine.TaxEngine
import com.incometaxcalc.domain.engine.TaxRegime
import com.incometaxcalc.presentation.viewmodel.TaxViewModel
import com.incometaxcalc.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: TaxViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Income Tax Calculator") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        "Estimate only — not official tax advice. Consult a CA for filing.",
                        Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "India ${TaxEngine.FY_LABEL}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = state.annualIncome,
                            onValueChange = viewModel::setAnnualIncome,
                            label = { Text("Annual Income (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state.deduction80C,
                            onValueChange = viewModel::setDeduction80C,
                            label = { Text("80C Deduction (₹) — old regime only") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            supportingText = { Text("Max ₹1,50,000 under Section 80C") }
                        )
                    }
                }
            }
            item {
                val old = state.oldResult
                val new = state.newResult
                if (old != null && new != null) {
                    RegimeSummaryCard(old, isOld = true)
                    Spacer(Modifier.height(4.dp))
                    RegimeSummaryCard(new, isOld = false)
                    state.betterRegime?.let { verdict ->
                        Card(
                            Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(verdict, fontWeight = FontWeight.Bold)
                                state.savingsAmount?.takeIf { it > 0 }?.let {
                                    Text("Difference: ${FormatUtils.currency(it)} per year")
                                }
                            }
                        }
                    }
                } else {
                    Text("Enter valid annual income", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegimeCompareScreen(viewModel: TaxViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val old = state.oldResult
    val new = state.newResult

    Scaffold(topBar = { TopAppBar(title = { Text("Regime Compare") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Side-by-side comparison for ${TaxEngine.FY_LABEL}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (old != null && new != null) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth()) {
                                Text("", Modifier.weight(1.2f))
                                Text("Old", Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                                Text("New", Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                            }
                            HorizontalDivider()
                            CompareRow("Gross Income", old.grossIncome, new.grossIncome)
                            CompareRow("Std. Deduction", old.standardDeduction, new.standardDeduction)
                            CompareRow("80C Deduction", old.section80C, new.section80C)
                            CompareRow("Taxable Income", old.taxableIncome, new.taxableIncome)
                            HorizontalDivider()
                            CompareRow("Tax Payable", old.taxPayable, new.taxPayable, highlight = true)
                            CompareRow("Monthly TDS", old.monthlyTds, new.monthlyTds)
                        }
                    }
                }
                item {
                    SlabReferenceCard("Old Regime Slabs", TaxEngine.OLD_SLABS)
                }
                item {
                    SlabReferenceCard("New Regime Slabs", TaxEngine.NEW_SLABS)
                }
            } else {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Enter income on Calculator tab first")
                    }
                }
            }
            item {
                DisclaimerCard()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxSavingTipsScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Tax Saving Tips") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Common ways to reduce taxable income in India",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(TaxEngine.TAX_SAVING_TIPS) { tip ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(tip, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item { DisclaimerCard() }
        }
    }
}

@Composable
private fun RegimeSummaryCard(result: RegimeResult, isOld: Boolean) {
    val title = if (isOld) "Old Regime" else "New Regime"
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            ResultLine("Taxable Income", FormatUtils.currency(result.taxableIncome))
            ResultLine("Tax Payable", FormatUtils.currency(result.taxPayable))
            ResultLine("Monthly TDS (est.)", FormatUtils.currency(result.monthlyTds))
        }
    }
}

@Composable
private fun CompareRow(label: String, oldVal: Double, newVal: Double, highlight: Boolean = false) {
    val weight = if (highlight) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth()) {
        Text(label, Modifier.weight(1.2f), fontWeight = weight)
        Text(FormatUtils.currency(oldVal), Modifier.weight(1f), fontWeight = weight, textAlign = TextAlign.End)
        Text(FormatUtils.currency(newVal), Modifier.weight(1f), fontWeight = weight, textAlign = TextAlign.End)
    }
}

@Composable
private fun SlabReferenceCard(title: String, slabs: List<com.incometaxcalc.domain.engine.TaxSlab>) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            var prev = 0.0
            slabs.forEach { slab ->
                val range = slab.upperLimit?.let { limit ->
                    "₹${FormatUtils.number(prev)} – ₹${FormatUtils.number(limit)}"
                } ?: "Above ₹${FormatUtils.number(prev)}"
                Text("$range → ${slab.ratePercent.toInt()}%", style = MaterialTheme.typography.bodySmall)
                prev = slab.upperLimit ?: prev
            }
        }
    }
}

@Composable
private fun DisclaimerCard() {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            "This calculator provides simplified estimates for ${TaxEngine.FY_LABEL} only. " +
                "Actual tax may differ due to cess, surcharge, rebates, and other deductions. " +
                "Not a substitute for professional advice.",
            Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
