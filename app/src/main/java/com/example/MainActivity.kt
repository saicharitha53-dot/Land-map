package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.LandProperty
import com.example.ui.components.BookingVisitDialog
import com.example.ui.components.LandMapHeader
import com.example.ui.screens.AddListingScreen
import com.example.ui.screens.CompareScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListingsScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.PriceIntelligenceScreen
import com.example.ui.screens.PropertyDetailScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.LandMapViewModel
import com.example.viewmodel.NavTab

class MainActivity : ComponentActivity() {
    private val viewModel: LandMapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LandMapApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LandMapApp(viewModel: LandMapViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.userNotificationMessage) {
        uiState.userNotificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    // Handle Hardware Back Button
    BackHandler(
        enabled = uiState.selectedProperty != null || uiState.showCompareSheet || uiState.showAddListingSheet
    ) {
        when {
            uiState.selectedProperty != null -> viewModel.selectProperty(null)
            uiState.showCompareSheet -> viewModel.setCompareSheetVisible(false)
            uiState.showAddListingSheet -> viewModel.setAddListingSheetVisible(false)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (uiState.selectedProperty == null && !uiState.showAddListingSheet && !uiState.showCompareSheet) {
                LandMapHeader(
                    onAddListingClick = { viewModel.setAddListingSheetVisible(true) },
                    comparisonCount = uiState.comparisonPropertyIds.size,
                    onCompareClick = { viewModel.setCompareSheetVisible(true) }
                )
            }
        },
        bottomBar = {
            if (uiState.selectedProperty == null && !uiState.showAddListingSheet && !uiState.showCompareSheet) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = androidx.compose.ui.unit.Dp(8f),
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    val tabs = listOf(
                        NavTab.HOME to ("Home" to (Icons.Filled.Home to Icons.Outlined.Home)),
                        NavTab.MAP to ("Map" to (Icons.Filled.Map to Icons.Outlined.Map)),
                        NavTab.LISTINGS to ("Listings" to (Icons.Filled.FormatListBulleted to Icons.Outlined.FormatListBulleted)),
                        NavTab.AI_PRICING to ("AI Pricing" to (Icons.Filled.Psychology to Icons.Outlined.Psychology)),
                        NavTab.DASHBOARD to ("Dashboard" to (Icons.Filled.Person to Icons.Outlined.Person))
                    )

                    tabs.forEach { (tab, details) ->
                        val (label, iconPair) = details
                        val isSelected = uiState.activeTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setActiveTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) iconPair.first else iconPair.second,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                selectedTextColor = EmeraldDark,
                                indicatorColor = EmeraldLight,
                                unselectedIconColor = SlateTextMuted,
                                unselectedTextColor = SlateTextMuted
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.showAddListingSheet -> {
                    AddListingScreen(
                        onDismiss = { viewModel.setAddListingSheetVisible(false) },
                        onSubmitListing = { title, locality, city, pincode, sizeSqFt, dimensions, askingPriceLakhs, type, facing, roadWidthFt, cornerPlot, approvals, surveyNo, description, sellerName, sellerPhone ->
                            viewModel.addNewListing(
                                title,
                                locality,
                                city,
                                pincode,
                                sizeSqFt,
                                dimensions,
                                askingPriceLakhs,
                                type,
                                facing,
                                roadWidthFt,
                                cornerPlot,
                                approvals,
                                surveyNo,
                                description,
                                sellerName,
                                sellerPhone
                            )
                        }
                    )
                }

                uiState.showCompareSheet -> {
                    val comparedList = uiState.properties.filter {
                        uiState.comparisonPropertyIds.contains(it.id)
                    }
                    CompareScreen(
                        propertiesToCompare = comparedList,
                        onRemoveFromCompare = { id -> viewModel.toggleCompareProperty(id) },
                        onViewProperty = { prop ->
                            viewModel.setCompareSheetVisible(false)
                            viewModel.selectProperty(prop)
                        },
                        onCloseCompare = { viewModel.setCompareSheetVisible(false) }
                    )
                }

                uiState.selectedProperty != null -> {
                    val selected = uiState.selectedProperty!!
                    PropertyDetailScreen(
                        property = selected,
                        isSaved = uiState.savedPropertyIds.contains(selected.id),
                        isCompared = uiState.comparisonPropertyIds.contains(selected.id),
                        onBackClick = { viewModel.selectProperty(null) },
                        onSaveToggle = { viewModel.toggleSaveProperty(selected.id) },
                        onCompareToggle = { viewModel.toggleCompareProperty(selected.id) },
                        onScheduleVisitClick = { viewModel.setBookingDialogVisible(true) }
                    )
                }

                else -> {
                    when (uiState.activeTab) {
                        NavTab.HOME -> {
                            HomeScreen(
                                properties = uiState.properties,
                                savedPropertyIds = uiState.savedPropertyIds,
                                comparisonPropertyIds = uiState.comparisonPropertyIds,
                                searchQuery = uiState.filterState.query,
                                selectedCity = uiState.filterState.selectedCity,
                                onSearchChange = { q ->
                                    viewModel.updateSearchQuery(q)
                                    if (q.isNotBlank()) viewModel.setActiveTab(NavTab.LISTINGS)
                                },
                                onCityChange = { c ->
                                    viewModel.updateCityFilter(c)
                                    viewModel.setActiveTab(NavTab.LISTINGS)
                                },
                                onLandTypeSelect = { type ->
                                    viewModel.updateLandTypeFilter(type)
                                    viewModel.setActiveTab(NavTab.LISTINGS)
                                },
                                onPropertyClick = { prop -> viewModel.selectProperty(prop) },
                                onSaveToggle = { id -> viewModel.toggleSaveProperty(id) },
                                onCompareToggle = { id -> viewModel.toggleCompareProperty(id) },
                                onNavigateTab = { tab -> viewModel.setActiveTab(tab) }
                            )
                        }

                        NavTab.MAP -> {
                            MapScreen(
                                properties = uiState.properties,
                                selectedProperty = uiState.selectedProperty,
                                onSelectProperty = { prop -> viewModel.selectProperty(prop) },
                                onViewPropertyDetails = { prop -> viewModel.selectProperty(prop) }
                            )
                        }

                        NavTab.LISTINGS -> {
                            ListingsScreen(
                                properties = viewModel.getFilteredProperties(),
                                savedPropertyIds = uiState.savedPropertyIds,
                                comparisonPropertyIds = uiState.comparisonPropertyIds,
                                filterState = uiState.filterState,
                                onQueryChange = { q -> viewModel.updateSearchQuery(q) },
                                onCityChange = { c -> viewModel.updateCityFilter(c) },
                                onLandTypeChange = { t -> viewModel.updateLandTypeFilter(t) },
                                onBudgetChange = { b -> viewModel.updateBudgetFilter(b) },
                                onApprovalChange = { a -> viewModel.updateApprovalFilter(a) },
                                onSortChange = { s -> viewModel.updateSort(s) },
                                onResetFilters = { viewModel.resetFilters() },
                                onPropertyClick = { prop -> viewModel.selectProperty(prop) },
                                onSaveToggle = { id -> viewModel.toggleSaveProperty(id) },
                                onCompareToggle = { id -> viewModel.toggleCompareProperty(id) }
                            )
                        }

                        NavTab.AI_PRICING -> {
                            PriceIntelligenceScreen(
                                calculatorForm = uiState.aiCalculator,
                                estimationOutput = uiState.aiEstimation,
                                onFormChange = { form -> viewModel.updateAiCalculatorForm(form) }
                            )
                        }

                        NavTab.DASHBOARD -> {
                            val savedList = uiState.properties.filter {
                                uiState.savedPropertyIds.contains(it.id)
                            }
                            val myListings = uiState.properties.filter {
                                it.isUserListed || it.id == "PROP-BLR-01"
                            }
                            DashboardScreen(
                                savedProperties = savedList,
                                myListings = myListings,
                                siteVisits = uiState.siteVisits,
                                comparisonPropertyIds = uiState.comparisonPropertyIds,
                                onAddListingClick = { viewModel.setAddListingSheetVisible(true) },
                                onPropertyClick = { prop -> viewModel.selectProperty(prop) },
                                onSaveToggle = { id -> viewModel.toggleSaveProperty(id) },
                                onCompareToggle = { id -> viewModel.toggleCompareProperty(id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Site Visit Booking Dialog
    if (uiState.showBookingDialog && uiState.selectedProperty != null) {
        val selected = uiState.selectedProperty!!
        BookingVisitDialog(
            propertyTitle = selected.title,
            sellerName = selected.sellerName,
            onDismiss = { viewModel.setBookingDialogVisible(false) },
            onConfirm = { name, phone, date ->
                viewModel.bookSiteVisit(name, phone, date)
            }
        )
    }
}
