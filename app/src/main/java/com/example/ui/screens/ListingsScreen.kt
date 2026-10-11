package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LandProperty
import com.example.model.LandType
import com.example.model.LegalApproval
import com.example.ui.components.PropertyCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMedium
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.FilterState
import com.example.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    properties: List<LandProperty>,
    savedPropertyIds: Set<String>,
    comparisonPropertyIds: Set<String>,
    filterState: FilterState,
    onQueryChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onLandTypeChange: (LandType?) -> Unit,
    onBudgetChange: (Double?) -> Unit,
    onApprovalChange: (LegalApproval?) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onResetFilters: () -> Unit,
    onPropertyClick: (LandProperty) -> Unit,
    onSaveToggle: (String) -> Unit,
    onCompareToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val activeFilterCount = (if (filterState.selectedCity != "All Cities") 1 else 0) +
            (if (filterState.selectedLandType != null) 1 else 0) +
            (if (filterState.maxBudgetLakhs != null) 1 else 0) +
            (if (filterState.selectedApproval != null) 1 else 0) +
            (if (filterState.cornerOnly) 1 else 0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("listings_screen")
    ) {
        // Search & Filter header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search location, colony, pincode...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SlateTextMuted
                        )
                    },
                    trailingIcon = {
                        if (filterState.query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F5F9),
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedBorderColor = EmeraldDark,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("listings_search_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Filter Button with Badge
                BadgedBox(
                    badge = {
                        if (activeFilterCount > 0) {
                            Badge(
                                containerColor = EmeraldDark,
                                contentColor = Color.White
                            ) {
                                Text("$activeFilterCount")
                            }
                        }
                    }
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeFilterCount > 0) Color(0xFFE6F4EA) else Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeFilterCount > 0) EmeraldDark else BorderLight
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { showFilterSheet = true }
                            .testTag("filter_sheet_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = if (activeFilterCount > 0) EmeraldDark else NavyPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sort Pills Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = null,
                    tint = SlateTextMuted,
                    modifier = Modifier.size(16.dp)
                )

                SortOption.entries.forEach { option ->
                    val isSelected = filterState.sortBy == option
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) NavyPrimary else Color(0xFFF1F5F9),
                        modifier = Modifier.clickable { onSortChange(option) }
                    ) {
                        Text(
                            text = option.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SlateTextMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Listings Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${properties.size} Land Properties Available",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextDark
            )

            if (activeFilterCount > 0) {
                Row(
                    modifier = Modifier.clickable { onResetFilters() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset",
                        tint = EmeraldDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Reset Filters",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldDark
                    )
                }
            }
        }

        // Listings List
        if (properties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No Land Plots Found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your budget, location or land category filters.",
                        fontSize = 13.sp,
                        color = SlateTextMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onResetFilters,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(properties, key = { it.id }) { property ->
                    PropertyCard(
                        property = property,
                        isSaved = savedPropertyIds.contains(property.id),
                        isCompared = comparisonPropertyIds.contains(property.id),
                        onCardClick = { onPropertyClick(property) },
                        onSaveToggle = { onSaveToggle(property.id) },
                        onCompareToggle = { onCompareToggle(property.id) }
                    )
                }
            }
        }
    }

    // Comprehensive Filter Modal Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Land Listings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Text(
                        text = "Clear All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldDark,
                        modifier = Modifier.clickable {
                            onResetFilters()
                            showFilterSheet = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location City Filter
                Text("City / Metro Hub", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All Cities", "Bengaluru", "Hyderabad", "Pune", "Chennai", "Delhi-NCR").forEach { city ->
                        val selected = filterState.selectedCity == city
                        FilterChip(
                            selected = selected,
                            onClick = { onCityChange(city) },
                            label = { Text(city, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Land Type
                Text("Land Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterState.selectedLandType == null,
                        onClick = { onLandTypeChange(null) },
                        label = { Text("All Categories", fontSize = 12.sp) }
                    )
                    LandType.entries.forEach { type ->
                        FilterChip(
                            selected = filterState.selectedLandType == type,
                            onClick = { onLandTypeChange(if (filterState.selectedLandType == type) null else type) },
                            label = { Text(type.displayName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Budget Selector
                Text("Max Budget", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Any Budget" to null,
                        "Under ₹50 L" to 50.0,
                        "Under ₹75 L" to 75.0,
                        "Under ₹1.2 Cr" to 120.0,
                        "Under ₹2.0 Cr" to 200.0
                    ).forEach { (label, value) ->
                        val selected = filterState.maxBudgetLakhs == value
                        FilterChip(
                            selected = selected,
                            onClick = { onBudgetChange(value) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legal Approval
                Text("Government Sanctions & Authority", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegalApproval.entries.forEach { apprv ->
                        val selected = filterState.selectedApproval == apprv
                        FilterChip(
                            selected = selected,
                            onClick = { onApprovalChange(if (selected) null else apprv) },
                            label = { Text(apprv.fullName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_filters_button")
                ) {
                    Text("Apply & View Results", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
