package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FacingDirection
import com.example.model.LandType
import com.example.model.SampleData
import com.example.ui.components.AiDisclaimerCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMedium
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.AiCalculatorForm
import com.example.viewmodel.AiEstimationOutput

@Composable
fun PriceIntelligenceScreen(
    calculatorForm: AiCalculatorForm,
    estimationOutput: AiEstimationOutput?,
    onFormChange: (AiCalculatorForm) -> Unit,
    modifier: Modifier = Modifier
) {
    val localities = listOf(
        "Devanahalli, Bengaluru",
        "Shamshabad, Hyderabad",
        "Hinjawadi, Pune",
        "OMR, Chennai",
        "Greater Noida, NCR"
    )

    var plotSizeInput by remember(calculatorForm.plotSizeSqFt) {
        mutableStateOf(calculatorForm.plotSizeSqFt.toInt().toString())
    }
    var roadWidthInput by remember(calculatorForm.roadWidthFt) {
        mutableStateOf(calculatorForm.roadWidthFt.toString())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("price_intelligence_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x3310B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = EmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "LandMap AI Price Engine",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Fair Market Valuation Model",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Estimate fair market rates for any plot across India using road width, zoning type, corner premiums and historical registry benchmarks.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Interactive Land Valuation Inputs
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Growth Hub / Locality",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        localities.forEach { loc ->
                            val isSel = calculatorForm.locality == loc
                            FilterChip(
                                selected = isSel,
                                onClick = { onFormChange(calculatorForm.copy(locality = loc)) },
                                label = { Text(loc, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plot size and road width text fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = plotSizeInput,
                            onValueChange = {
                                plotSizeInput = it
                                it.toDoubleOrNull()?.let { size ->
                                    if (size > 0) onFormChange(calculatorForm.copy(plotSizeSqFt = size))
                                }
                            },
                            label = { Text("Plot Area (sq.ft)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_plot_size_input")
                        )

                        OutlinedTextField(
                            value = roadWidthInput,
                            onValueChange = {
                                roadWidthInput = it
                                it.toIntOrNull()?.let { road ->
                                    if (road > 0) onFormChange(calculatorForm.copy(roadWidthFt = road))
                                }
                            },
                            label = { Text("Road Width (ft)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_road_width_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Corner plot switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Corner Plot Frontage",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "Dual-side road access adds ~8% market premium",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }

                        Switch(
                            checked = calculatorForm.isCornerPlot,
                            onCheckedChange = { isCorner ->
                                onFormChange(calculatorForm.copy(isCornerPlot = isCorner))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Land Type Selector
                    Text("Zoning / Land Type", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            LandType.RESIDENTIAL_LAYOUT,
                            LandType.GATED_COMMUNITY,
                            LandType.COMMERCIAL_PLOT,
                            LandType.AGRICULTURAL_FARM
                        ).forEach { type ->
                            val isSel = calculatorForm.landType == type
                            FilterChip(
                                selected = isSel,
                                onClick = { onFormChange(calculatorForm.copy(landType = type)) },
                                label = { Text(type.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // AI Valuation Output Result
        estimationOutput?.let { out ->
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_valuation_output_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estimated Fair Market Valuation",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldLight
                            ) {
                                Text(
                                    text = "${out.confidencePercent}% Confidence",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large Fair Value Total
                        val avgTotalStr = if (out.estimatedTotalAvgLakhs >= 100.0) {
                            String.format("₹%.2f Cr", out.estimatedTotalAvgLakhs / 100.0)
                        } else {
                            String.format("₹%.1f L", out.estimatedTotalAvgLakhs)
                        }

                        val minTotalStr = if (out.estimatedTotalMinLakhs >= 100.0) {
                            String.format("₹%.2f Cr", out.estimatedTotalMinLakhs / 100.0)
                        } else {
                            String.format("₹%.1f L", out.estimatedTotalMinLakhs)
                        }

                        val maxTotalStr = if (out.estimatedTotalMaxLakhs >= 100.0) {
                            String.format("₹%.2f Cr", out.estimatedTotalMaxLakhs / 100.0)
                        } else {
                            String.format("₹%.1f L", out.estimatedTotalMaxLakhs)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp))
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Median Fair Value",
                                fontSize = 12.sp,
                                color = EmeraldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = avgTotalStr,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = NavyPrimary
                            )
                            Text(
                                text = "Estimated Fair Band: $minTotalStr – $maxTotalStr",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = SlateTextMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rate per sq.ft band
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            RateColumn("Lower Band", "₹${out.fairMinRateSqFt.toInt()}/sq.ft")
                            Box(modifier = Modifier.width(1.dp).height(28.dp).background(BorderLight))
                            RateColumn("AI Fair Rate", "₹${out.fairAvgRateSqFt.toInt()}/sq.ft")
                            Box(modifier = Modifier.width(1.dp).height(28.dp).background(BorderLight))
                            RateColumn("Upper Band", "₹${out.fairMaxRateSqFt.toInt()}/sq.ft")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Value Drivers Breakdown
                        Text(
                            text = "Model Valuation Drivers & Adjustments",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        out.drivers.forEach { (driver, impact) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = EmeraldDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = driver, fontSize = 12.sp, color = SlateTextDark)
                                }
                                Text(
                                    text = impact,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // Government Circle Rate vs Market Asking Comparison Benchmarks
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Benchmark: Govt Circle Rate vs Market Asking",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sample registry benchmarks for ${calculatorForm.locality}",
                        fontSize = 11.sp,
                        color = SlateTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BenchmarkRow(
                        title = "Govt Guidance / Circle Rate",
                        rate = "₹1,850/sq.ft",
                        tag = "Minimum Stamp Duty Base",
                        tagColor = SlateTextMuted
                    )
                    BenchmarkRow(
                        title = "Sample Nearby Asking Listings",
                        rate = "₹3,150/sq.ft",
                        tag = "Current Seller Quotation",
                        tagColor = Color(0xFF0284C7)
                    )
                    BenchmarkRow(
                        title = "AI Fair Valuation Model",
                        rate = "₹2,950/sq.ft",
                        tag = "Fair Price Recommendation",
                        tagColor = EmeraldDark
                    )
                }
            }
        }

        // Historical 5-Quarter Price Appreciation Trend Chart
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historical Asking Rate Trend",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+14.8% YoY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Trend Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val rates = listOf(2200f, 2400f, 2600f, 2780f, 2950f)
                            val minVal = 2000f
                            val maxVal = 3200f
                            val range = maxVal - minVal

                            val points = rates.mapIndexed { idx, rate ->
                                val x = (idx.toFloat() / (rates.size - 1)) * w
                                val y = h - ((rate - minVal) / range) * h
                                Offset(x, y)
                            }

                            // Fill path
                            val fillPath = Path().apply {
                                moveTo(0f, h)
                                points.forEach { lineTo(it.x, it.y) }
                                lineTo(w, h)
                                close()
                            }
                            drawPath(fillPath, Color(0x3310B981))

                            // Stroke path
                            val strokePath = Path().apply {
                                points.forEachIndexed { idx, pt ->
                                    if (idx == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                                }
                            }
                            drawPath(strokePath, EmeraldDark, style = Stroke(width = 4f))

                            // Draw point circles
                            points.forEach { pt ->
                                drawCircle(Color.White, radius = 6f, center = pt)
                                drawCircle(EmeraldDark, radius = 4f, center = pt)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Q1 2025", "Q2 2025", "Q3 2025", "Q4 2025", "Q1 2026").forEach { quarter ->
                            Text(text = quarter, fontSize = 10.sp, color = SlateTextMuted)
                        }
                    }
                }
            }
        }

        // Disclaimer Card
        item {
            AiDisclaimerCard()
        }
    }
}

@Composable
fun RateColumn(title: String, rate: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, color = SlateTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = rate, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
    }
}

@Composable
fun BenchmarkRow(title: String, rate: String, tag: String, tagColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateTextDark)
            Text(text = tag, fontSize = 10.sp, color = tagColor, fontWeight = FontWeight.Medium)
        }
        Text(text = rate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
    }
}
