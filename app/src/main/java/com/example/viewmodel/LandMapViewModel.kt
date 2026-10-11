package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.FacingDirection
import com.example.model.LandProperty
import com.example.model.LandType
import com.example.model.LegalApproval
import com.example.model.PriceAssessment
import com.example.model.SampleData
import com.example.model.SiteVisitBooking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class NavTab {
    HOME,
    MAP,
    LISTINGS,
    AI_PRICING,
    DASHBOARD
}

data class FilterState(
    val query: String = "",
    val selectedCity: String = "All Cities",
    val selectedLandType: LandType? = null,
    val maxBudgetLakhs: Double? = null,
    val minSizeSqFt: Double? = null,
    val selectedApproval: LegalApproval? = null,
    val cornerOnly: Boolean = false,
    val sortBy: SortOption = SortOption.RECOMMENDED
)

enum class SortOption(val title: String) {
    RECOMMENDED("AI Recommended"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    SIZE_LARGE_SMALL("Plot Area: Large First"),
    PRICE_PER_SQFT("Best Value (₹/sq.ft)")
}

data class AiCalculatorForm(
    val locality: String = "Devanahalli, Bengaluru",
    val plotSizeSqFt: Double = 2400.0,
    val roadWidthFt: Int = 40,
    val isCornerPlot: Boolean = true,
    val landType: LandType = LandType.RESIDENTIAL_LAYOUT,
    val facing: FacingDirection = FacingDirection.EAST
)

data class AiEstimationOutput(
    val baseRateSqFt: Double,
    val fairMinRateSqFt: Double,
    val fairAvgRateSqFt: Double,
    val fairMaxRateSqFt: Double,
    val estimatedTotalMinLakhs: Double,
    val estimatedTotalAvgLakhs: Double,
    val estimatedTotalMaxLakhs: Double,
    val confidencePercent: Int,
    val drivers: List<Pair<String, String>>,
    val projectedAnnualGrowth: Double
)

data class LandMapUiState(
    val properties: List<LandProperty> = SampleData.sampleProperties,
    val activeTab: NavTab = NavTab.HOME,
    val filterState: FilterState = FilterState(),
    val selectedProperty: LandProperty? = null,
    val savedPropertyIds: Set<String> = setOf("PROP-BLR-01", "PROP-HYD-01"),
    val comparisonPropertyIds: Set<String> = setOf("PROP-BLR-01", "PROP-PUN-01"),
    val siteVisits: List<SiteVisitBooking> = listOf(
        SiteVisitBooking(
            id = "VISIT-101",
            propertyId = "PROP-BLR-01",
            propertyTitle = "RERA Gated Villa Plot near Airport Road",
            buyerName = "S. Ramanathan",
            buyerPhone = "+91 98860 11420",
            preferredDate = "This Saturday, 11:00 AM",
            status = "Confirmed with Developer"
        )
    ),
    val aiCalculator: AiCalculatorForm = AiCalculatorForm(),
    val aiEstimation: AiEstimationOutput? = null,
    val showAddListingSheet: Boolean = false,
    val showCompareSheet: Boolean = false,
    val showFiltersSheet: Boolean = false,
    val showBookingDialog: Boolean = false,
    val userNotificationMessage: String? = null
)

class LandMapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LandMapUiState())
    val uiState: StateFlow<LandMapUiState> = _uiState.asStateFlow()

    init {
        runAiPriceEstimation(_uiState.value.aiCalculator)
    }

    fun setActiveTab(tab: NavTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectProperty(property: LandProperty?) {
        if (property != null) {
            // Increment view count for realistic analytics
            val updatedList = _uiState.value.properties.map {
                if (it.id == property.id) it.copy(viewsCount = it.viewsCount + 1) else it
            }
            _uiState.update {
                it.copy(
                    properties = updatedList,
                    selectedProperty = property.copy(viewsCount = property.viewsCount + 1)
                )
            }
        } else {
            _uiState.update { it.copy(selectedProperty = null) }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(filterState = it.filterState.copy(query = query))
        }
    }

    fun updateCityFilter(city: String) {
        _uiState.update {
            it.copy(filterState = it.filterState.copy(selectedCity = city))
        }
    }

    fun updateLandTypeFilter(type: LandType?) {
        _uiState.update {
            it.copy(filterState = it.filterState.copy(selectedLandType = type))
        }
    }

    fun updateBudgetFilter(maxLakhs: Double?) {
        _uiState.update {
            it.copy(filterState = it.filterState.copy(maxBudgetLakhs = maxLakhs))
        }
    }

    fun updateApprovalFilter(approval: LegalApproval?) {
        _uiState.update {
            it.copy(filterState = it.filterState.copy(selectedApproval = approval))
        }
    }

    fun updateSort(sortOption: SortOption) {
        _uiState.update {
            it.copy(filterState = it.filterState.copy(sortBy = sortOption))
        }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                filterState = FilterState(
                    query = it.filterState.query,
                    selectedCity = "All Cities"
                )
            )
        }
    }

    fun toggleSaveProperty(id: String) {
        _uiState.update { state ->
            val updated = state.savedPropertyIds.toMutableSet()
            val msg = if (updated.contains(id)) {
                updated.remove(id)
                "Removed from Saved Plots"
            } else {
                updated.add(id)
                "Saved to your Shortlist"
            }
            state.copy(savedPropertyIds = updated, userNotificationMessage = msg)
        }
    }

    fun toggleCompareProperty(id: String) {
        _uiState.update { state ->
            val updated = state.comparisonPropertyIds.toMutableSet()
            val msg: String
            if (updated.contains(id)) {
                updated.remove(id)
                msg = "Removed from comparison"
            } else {
                if (updated.size >= 3) {
                    msg = "You can compare up to 3 properties at a time"
                } else {
                    updated.add(id)
                    msg = "Added to comparison (${updated.size}/3)"
                }
            }
            state.copy(comparisonPropertyIds = updated, userNotificationMessage = msg)
        }
    }

    fun setCompareSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showCompareSheet = visible) }
    }

    fun setFiltersSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showFiltersSheet = visible) }
    }

    fun setAddListingSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddListingSheet = visible) }
    }

    fun setBookingDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showBookingDialog = visible) }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotificationMessage = null) }
    }

    fun bookSiteVisit(buyerName: String, buyerPhone: String, preferredDate: String) {
        val selected = _uiState.value.selectedProperty ?: return
        val newBooking = SiteVisitBooking(
            id = "VISIT-${System.currentTimeMillis() % 10000}",
            propertyId = selected.id,
            propertyTitle = selected.title,
            buyerName = buyerName,
            buyerPhone = buyerPhone,
            preferredDate = preferredDate,
            status = "Appointment Scheduled with ${selected.sellerName}"
        )
        _uiState.update {
            it.copy(
                siteVisits = listOf(newBooking) + it.siteVisits,
                showBookingDialog = false,
                userNotificationMessage = "Site visit booked for $preferredDate! The seller will contact you."
            )
        }
    }

    fun addNewListing(
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
    ) {
        val calculatedSqFtRate = (askingPriceLakhs * 100000.0) / sizeSqFt
        val fairRate = calculatedSqFtRate * 1.05
        val newProp = LandProperty(
            id = "PROP-USER-${System.currentTimeMillis() % 10000}",
            title = title,
            locality = locality,
            city = city,
            pincode = pincode,
            state = "India",
            plotSizeSqFt = sizeSqFt,
            dimensions = dimensions,
            askingPriceLakhs = askingPriceLakhs,
            aiEstimatedFairPriceSqFt = fairRate,
            aiConfidenceScore = 91,
            priceAssessment = PriceAssessment.FAIR_MARKET,
            type = type,
            facing = facing,
            roadWidthFt = roadWidthFt,
            cornerPlot = cornerPlot,
            approvals = approvals,
            surveyNumber = surveyNo,
            soilType = "Checked & Verified",
            waterTable = "Municipal / Borewell connection available",
            description = description,
            sellerName = sellerName,
            sellerPhone = sellerPhone,
            sellerRole = "Direct Owner",
            imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800&auto=format&fit=crop",
            mapCoordinatesX = 0.5f + (Math.random().toFloat() * 0.3f - 0.15f),
            mapCoordinatesY = 0.5f + (Math.random().toFloat() * 0.3f - 0.15f),
            isFeatured = false,
            viewsCount = 1,
            isUserListed = true
        )

        _uiState.update {
            it.copy(
                properties = listOf(newProp) + it.properties,
                showAddListingSheet = false,
                userNotificationMessage = "Your land listing '$title' has been published successfully!"
            )
        }
    }

    fun updateAiCalculatorForm(form: AiCalculatorForm) {
        _uiState.update { it.copy(aiCalculator = form) }
        runAiPriceEstimation(form)
    }

    private fun runAiPriceEstimation(form: AiCalculatorForm) {
        // Base rate depending on locality
        val baseRate = when {
            form.locality.contains("Shamshabad", ignoreCase = true) -> 3300.0
            form.locality.contains("Hinjawadi", ignoreCase = true) -> 3100.0
            form.locality.contains("OMR", ignoreCase = true) -> 3500.0
            form.locality.contains("Greater Noida", ignoreCase = true) -> 2050.0
            else -> 2950.0 // Devanahalli / default
        }

        var multiplier = 1.0
        val drivers = mutableListOf<Pair<String, String>>()

        // Road width impact
        if (form.roadWidthFt >= 60) {
            multiplier += 0.12
            drivers.add("Arterial Road (${form.roadWidthFt} ft)" to "+12% Value Lift")
        } else if (form.roadWidthFt >= 40) {
            multiplier += 0.06
            drivers.add("Wide Layout Road (${form.roadWidthFt} ft)" to "+6% Value Lift")
        }

        // Corner plot premium
        if (form.isCornerPlot) {
            multiplier += 0.08
            drivers.add("Dual-Side Corner Plot" to "+8% Premium")
        }

        // Facing direction
        if (form.facing == FacingDirection.EAST || form.facing == FacingDirection.NORTH_EAST) {
            multiplier += 0.05
            drivers.add("${form.facing.label} (High Vaastu Demand)" to "+5% Premium")
        }

        // Land type factor
        if (form.landType == LandType.COMMERCIAL_PLOT) {
            multiplier += 0.25
            drivers.add("Commercial Zoning Potential" to "+25% Demand")
        } else if (form.landType == LandType.AGRICULTURAL_FARM) {
            multiplier *= 0.35 // agricultural is lower ₹/sqft
            drivers.add("Agricultural Land Classification" to "Baseline Farm Rate")
        }

        val estimatedAvgRate = baseRate * multiplier
        val estimatedMinRate = estimatedAvgRate * 0.92
        val estimatedMaxRate = estimatedAvgRate * 1.09

        val totalAvgLakhs = (estimatedAvgRate * form.plotSizeSqFt) / 100000.0
        val totalMinLakhs = (estimatedMinRate * form.plotSizeSqFt) / 100000.0
        val totalMaxLakhs = (estimatedMaxRate * form.plotSizeSqFt) / 100000.0

        val output = AiEstimationOutput(
            baseRateSqFt = baseRate,
            fairMinRateSqFt = estimatedMinRate,
            fairAvgRateSqFt = estimatedAvgRate,
            fairMaxRateSqFt = estimatedMaxRate,
            estimatedTotalMinLakhs = totalMinLakhs,
            estimatedTotalAvgLakhs = totalAvgLakhs,
            estimatedTotalMaxLakhs = totalMaxLakhs,
            confidencePercent = 94,
            drivers = drivers,
            projectedAnnualGrowth = 13.5
        )

        _uiState.update { it.copy(aiEstimation = output) }
    }

    fun getFilteredProperties(): List<LandProperty> {
        val state = _uiState.value
        val filter = state.filterState

        return state.properties.filter { prop ->
            val matchesQuery = if (filter.query.isBlank()) true else {
                val q = filter.query.trim().lowercase()
                prop.title.lowercase().contains(q) ||
                        prop.locality.lowercase().contains(q) ||
                        prop.city.lowercase().contains(q) ||
                        prop.pincode.contains(q)
            }

            val matchesCity = if (filter.selectedCity == "All Cities") true else {
                prop.city.equals(filter.selectedCity, ignoreCase = true)
            }

            val matchesType = if (filter.selectedLandType == null) true else {
                prop.type == filter.selectedLandType
            }

            val matchesBudget = if (filter.maxBudgetLakhs == null) true else {
                prop.askingPriceLakhs <= filter.maxBudgetLakhs
            }

            val matchesMinSize = if (filter.minSizeSqFt == null) true else {
                prop.plotSizeSqFt >= filter.minSizeSqFt
            }

            val matchesApproval = if (filter.selectedApproval == null) true else {
                prop.approvals.contains(filter.selectedApproval)
            }

            val matchesCorner = if (!filter.cornerOnly) true else {
                prop.cornerPlot
            }

            matchesQuery && matchesCity && matchesType && matchesBudget && matchesMinSize && matchesApproval && matchesCorner
        }.let { list ->
            when (filter.sortBy) {
                SortOption.RECOMMENDED -> list.sortedByDescending { it.aiConfidenceScore }
                SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.askingPriceLakhs }
                SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.askingPriceLakhs }
                SortOption.SIZE_LARGE_SMALL -> list.sortedByDescending { it.plotSizeSqFt }
                SortOption.PRICE_PER_SQFT -> list.sortedBy { it.pricePerSqFt }
            }
        }
    }
}
