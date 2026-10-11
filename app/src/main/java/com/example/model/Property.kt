package com.example.model

enum class LandType(val displayName: String) {
    RESIDENTIAL_LAYOUT("Residential Layout"),
    GATED_COMMUNITY("Gated Community"),
    AGRICULTURAL_FARM("Agricultural / Farm Land"),
    COMMERCIAL_PLOT("Commercial Plot"),
    INDUSTRIAL_PLOT("Industrial Land")
}

enum class LegalApproval(val shortCode: String, val fullName: String) {
    RERA("RERA", "RERA Registered"),
    BDA("BDA", "BDA Approved"),
    HMDA("HMDA", "HMDA Approved"),
    DTCP("DTCP", "DTCP Approved"),
    PMRDA("PMRDA", "PMRDA Approved"),
    CLEAR_TITLE("Clear Title", "Freehold Clear Title")
}

enum class FacingDirection(val label: String) {
    EAST("East Facing"),
    NORTH("North Facing"),
    NORTH_EAST("North-East Facing"),
    WEST("West Facing"),
    SOUTH("South Facing")
}

enum class PriceAssessment(val label: String, val badgeColorHex: Long) {
    BELOW_MARKET("Great Deal (~8% Below)", 0xFF10B981),
    FAIR_MARKET("Fair Market Price", 0xFF0284C7),
    PREMIUM_ZONE("High Growth Premium", 0xFF8B5CF6)
}

data class LandProperty(
    val id: String,
    val title: String,
    val locality: String,
    val city: String,
    val pincode: String,
    val state: String,
    val plotSizeSqFt: Double,
    val dimensions: String,
    val askingPriceLakhs: Double, // e.g., 45.0 = ₹45 Lakhs, 120.0 = ₹1.20 Crore
    val aiEstimatedFairPriceSqFt: Double,
    val aiConfidenceScore: Int,
    val priceAssessment: PriceAssessment,
    val type: LandType,
    val facing: FacingDirection,
    val roadWidthFt: Int,
    val cornerPlot: Boolean,
    val approvals: List<LegalApproval>,
    val surveyNumber: String,
    val soilType: String,
    val waterTable: String,
    val description: String,
    val sellerName: String,
    val sellerPhone: String,
    val sellerRole: String, // "Direct Owner", "RERA Broker", "Developer"
    val imageUrl: String,
    val mapCoordinatesX: Float, // Normalized 0..1 for city map coordinate representation
    val mapCoordinatesY: Float,
    val isFeatured: Boolean = false,
    var viewsCount: Int = 142,
    val isUserListed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val pricePerSqFt: Double
        get() = (askingPriceLakhs * 100000.0) / plotSizeSqFt

    val plotSizeCents: Double
        get() = plotSizeSqFt / 435.6 // 1 Cent = 435.6 sq ft (standard South India metric)

    val plotSizeSqYards: Double
        get() = plotSizeSqFt / 9.0 // 1 Sq Yard / Gaj = 9 sq ft (standard North/Central India metric)

    val formattedPrice: String
        get() {
            return if (askingPriceLakhs >= 100.0) {
                String.format("₹%.2f Cr", askingPriceLakhs / 100.0)
            } else {
                String.format("₹%.1f L", askingPriceLakhs)
            }
        }

    val formattedAiFairPrice: String
        get() {
            val totalFairLakhs = (aiEstimatedFairPriceSqFt * plotSizeSqFt) / 100000.0
            return if (totalFairLakhs >= 100.0) {
                String.format("₹%.2f Cr", totalFairLakhs / 100.0)
            } else {
                String.format("₹%.1f L", totalFairLakhs)
            }
        }
}

data class SiteVisitBooking(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val buyerName: String,
    val buyerPhone: String,
    val preferredDate: String,
    val status: String = "Confirmed with Seller"
)

data class PriceIntelligenceSummary(
    val locality: String,
    val city: String,
    val averageRateSqFt: Double,
    val govtCircleRateSqFt: Double,
    val fairRangeMinSqFt: Double,
    val fairRangeMaxSqFt: Double,
    val yearOverYearGrowthPct: Double,
    val confidencePct: Int,
    val priceDrivers: List<Pair<String, String>>,
    val quarterlyTrends: List<Pair<String, Double>> // Quarter label to ₹/sqft
)
