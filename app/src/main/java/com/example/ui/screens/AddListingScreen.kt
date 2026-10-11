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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FacingDirection
import com.example.model.LandType
import com.example.model.LegalApproval
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMedium
import com.example.ui.theme.SlateTextMuted

@Composable
fun AddListingScreen(
    onDismiss: () -> Unit,
    onSubmitListing: (
        title: String,
        locality: String,
        city: String,
        pincode: String,
        sizeSqFt: Double,
        dimensions: String,
        askingPriceLakhs: Double,
        type: LandType,
        facing: FacingDirection,
        roadWidthFt: Int,
        cornerPlot: Boolean,
        approvals: List<LegalApproval>,
        surveyNo: String,
        description: String,
        sellerName: String,
        sellerPhone: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var locality by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Bengaluru") }
    var pincode by remember { mutableStateOf("") }
    var sizeSqFtInput by remember { mutableStateOf("") }
    var dimensions by remember { mutableStateOf("40 ft x 60 ft") }
    var askingPriceLakhsInput by remember { mutableStateOf("") }
    var selectedLandType by remember { mutableStateOf(LandType.RESIDENTIAL_LAYOUT) }
    var selectedFacing by remember { mutableStateOf(FacingDirection.EAST) }
    var roadWidthInput by remember { mutableStateOf("40") }
    var isCornerPlot by remember { mutableStateOf(false) }
    var selectedApprovals by remember { mutableStateOf(listOf(LegalApproval.RERA, LegalApproval.CLEAR_TITLE)) }
    var surveyNo by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sellerName by remember { mutableStateOf("Aarav Sharma") }
    var sellerPhone by remember { mutableStateOf("+91 98450 77123") }

    val cities = listOf("Bengaluru", "Hyderabad", "Pune", "Chennai", "Delhi-NCR")

    val isFormValid = title.isNotBlank() &&
            locality.isNotBlank() &&
            pincode.isNotBlank() &&
            (sizeSqFtInput.toDoubleOrNull() ?: 0.0) > 0.0 &&
            (askingPriceLakhsInput.toDoubleOrNull() ?: 0.0) > 0.0 &&
            sellerName.isNotBlank() &&
            sellerPhone.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("add_listing_screen")
    ) {
        // Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sell,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Post New Land Listing",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Basic Land Information",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Listing Title (e.g., Corner Villa Plot near Tech Park)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // City Selector
                Text("Metro Region", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cities.forEach { c ->
                        FilterChip(
                            selected = city == c,
                            onClick = { city = c },
                            label = { Text(c, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = locality,
                        onValueChange = { locality = it },
                        label = { Text("Locality / Sector") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("form_locality_input")
                    )

                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text("PIN Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.8f)
                            .testTag("form_pincode_input")
                    )
                }
            }

            // Size & Pricing
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Dimensions & Asking Price",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = sizeSqFtInput,
                        onValueChange = { sizeSqFtInput = it },
                        label = { Text("Plot Area (sq.ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("form_size_input")
                    )

                    OutlinedTextField(
                        value = dimensions,
                        onValueChange = { dimensions = it },
                        label = { Text("Dimensions (W x L)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = askingPriceLakhsInput,
                    onValueChange = { askingPriceLakhsInput = it },
                    label = { Text("Total Asking Price (₹ in Lakhs, e.g. 65 for ₹65L)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_price_input")
                )

                val sizeVal = sizeSqFtInput.toDoubleOrNull() ?: 0.0
                val priceVal = askingPriceLakhsInput.toDoubleOrNull() ?: 0.0
                if (sizeVal > 0 && priceVal > 0) {
                    val rate = (priceVal * 100000.0) / sizeVal
                    Text(
                        text = "Calculated rate: ₹${rate.toInt()} / sq.ft",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Land Category & Facing
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Land Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LandType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedLandType == type,
                            onClick = { selectedLandType = type },
                            label = { Text(type.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = roadWidthInput,
                        onValueChange = { roadWidthInput = it },
                        label = { Text("Road Width (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = surveyNo,
                        onValueChange = { surveyNo = it },
                        label = { Text("Survey / Khasra No.") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Corner plot switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Corner Plot Frontage", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isCornerPlot,
                        onCheckedChange = { isCornerPlot = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldDark)
                    )
                }
            }

            // Description & Seller Contact
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Description & Features", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details on soil, water connectivity, boundary wall, nearby highways...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Seller Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = sellerName,
                        onValueChange = { sellerName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = sellerPhone,
                        onValueChange = { sellerPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val sizeSqFt = sizeSqFtInput.toDoubleOrNull() ?: 2400.0
                        val priceLakhs = askingPriceLakhsInput.toDoubleOrNull() ?: 50.0
                        val roadWidth = roadWidthInput.toIntOrNull() ?: 40
                        onSubmitListing(
                            title,
                            locality,
                            city,
                            pincode,
                            sizeSqFt,
                            dimensions,
                            priceLakhs,
                            selectedLandType,
                            selectedFacing,
                            roadWidth,
                            isCornerPlot,
                            selectedApprovals,
                            if (surveyNo.isNotBlank()) surveyNo else "Sy. No. Verified",
                            if (description.isNotBlank()) description else "Clear title plot with verified road access.",
                            sellerName,
                            sellerPhone
                        )
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("publish_listing_button")
                ) {
                    Text(
                        text = "Publish Land Listing to Live App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
