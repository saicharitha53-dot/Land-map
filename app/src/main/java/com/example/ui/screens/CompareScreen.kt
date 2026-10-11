package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.LandProperty
import com.example.ui.theme.BorderLight
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMedium
import com.example.ui.theme.SlateTextMuted

@Composable
fun CompareScreen(
    propertiesToCompare: List<LandProperty>,
    onRemoveFromCompare: (String) -> Unit,
    onViewProperty: (LandProperty) -> Unit,
    onCloseCompare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("compare_screen")
    ) {
        if (propertiesToCompare.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No Properties Selected for Comparison",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap 'Compare' on any land listing to view detailed spec & AI value comparison.",
                        fontSize = 13.sp,
                        color = SlateTextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onCloseCompare,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Explore Land Listings")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Land Property Comparison",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "Comparing ${propertiesToCompare.size} of 3 properties",
                                fontSize = 12.sp,
                                color = SlateTextMuted
                            )
                        }

                        IconButton(onClick = onCloseCompare) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                // AI Best Value Winner Badge
                item {
                    val bestValue = propertiesToCompare.minByOrNull { it.pricePerSqFt }
                    bestValue?.let { best ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "AI Value Winner: ${best.title}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                    Text(
                                        text = "Lowest entry rate of ₹${best.pricePerSqFt.toInt()}/sq.ft with verified ${best.approvals.firstOrNull()?.shortCode ?: "Clear Title"}.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    }
                }

                // Horizontal comparison table
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        propertiesToCompare.forEach { prop ->
                            ComparisonCard(
                                property = prop,
                                onRemove = { onRemoveFromCompare(prop.id) },
                                onView = { onViewProperty(prop) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonCard(
    property: LandProperty,
    onRemove: () -> Unit,
    onView: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(260.dp)
            .testTag("compare_card_${property.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Photo & Remove button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = property.imageUrl,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = CircleShape,
                    color = Color(0xCC000000),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = property.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextDark,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${property.locality}, ${property.city}",
                fontSize = 11.sp,
                color = SlateTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Specs comparison rows
            ComparisonMetricRow("Asking Price", property.formattedPrice, isHighlight = true)
            ComparisonMetricRow("Rate per Sq.Ft", "₹${property.pricePerSqFt.toInt()}/sq.ft")
            ComparisonMetricRow("AI Fair Estimate", property.formattedAiFairPrice, isGreen = true)
            ComparisonMetricRow("Plot Area", "${property.plotSizeSqFt.toInt()} sq.ft")
            ComparisonMetricRow("In Cents / Gaj", String.format("%.1f Cents / %.0f Gaj", property.plotSizeCents, property.plotSizeSqYards))
            ComparisonMetricRow("Dimensions", property.dimensions)
            ComparisonMetricRow("Approach Road", "${property.roadWidthFt} Feet")
            ComparisonMetricRow("Facing", property.facing.label)
            ComparisonMetricRow("Corner Plot", if (property.cornerPlot) "Yes" else "No")
            ComparisonMetricRow("Approval", property.approvals.joinToString { it.shortCode })

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onView,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Full Listing", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ComparisonMetricRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isGreen: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = SlateTextMuted)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = if (isHighlight || isGreen) FontWeight.Bold else FontWeight.Medium,
            color = if (isGreen) EmeraldDark else if (isHighlight) NavyPrimary else SlateTextDark
        )
    }
}
