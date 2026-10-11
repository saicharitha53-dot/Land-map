package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.LandProperty
import com.example.model.PriceAssessment
import com.example.ui.theme.BorderLight
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMedium
import com.example.ui.theme.SlateTextMuted
import kotlin.math.roundToInt

@Composable
fun MapScreen(
    properties: List<LandProperty>,
    selectedProperty: LandProperty?,
    onSelectProperty: (LandProperty?) -> Unit,
    onViewPropertyDetails: (LandProperty) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var selectedRegion by remember { mutableStateOf("Bengaluru") }
    val regions = listOf("All Regions", "Bengaluru", "Hyderabad", "Pune", "Chennai", "Delhi-NCR")

    var showCadastralGrid by remember { mutableStateOf(true) }
    var showInfraCorridors by remember { mutableStateOf(true) }

    val filteredList = properties.filter {
        selectedRegion == "All Regions" || it.city.equals(selectedRegion, ignoreCase = true)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
            .testTag("interactive_map_screen")
    ) {
        val mapWidth = maxWidth.value
        val mapHeight = maxHeight.value
        // Interactive Canvas Layer with gesture detection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.7f, 2.5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
            ) {
                val w = size.width
                val h = size.height

                // Background land color
                drawRect(
                    color = Color(0xFFF1F5F9),
                    topLeft = Offset.Zero,
                    size = size
                )

                // Draw Topographic / Green belts
                drawCircle(
                    color = Color(0xFFDCFCE7),
                    radius = w * 0.35f,
                    center = Offset(w * 0.2f, h * 0.75f)
                )
                drawCircle(
                    color = Color(0xFFDCFCE7),
                    radius = w * 0.25f,
                    center = Offset(w * 0.8f, h * 0.25f)
                )

                // Water body (lake / river)
                val riverPath = Path().apply {
                    moveTo(0f, h * 0.45f)
                    cubicTo(w * 0.3f, h * 0.42f, w * 0.6f, h * 0.52f, w, h * 0.48f)
                    lineTo(w, h * 0.54f)
                    cubicTo(w * 0.6f, h * 0.58f, w * 0.3f, h * 0.48f, 0f, h * 0.51f)
                    close()
                }
                drawPath(path = riverPath, color = Color(0xFFBAE6FD))

                // Cadastral Survey Grid
                if (showCadastralGrid) {
                    val step = 70f
                    var x = 0f
                    while (x < w) {
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1f
                        )
                        x += step
                    }
                    var y = 0f
                    while (y < h) {
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }
                }

                // Arterial Highway Corridors
                if (showInfraCorridors) {
                    // National Highway NH-44 / Express corridor
                    drawLine(
                        color = Color(0xFF94A3B8),
                        start = Offset(w * 0.1f, 0f),
                        end = Offset(w * 0.85f, h),
                        strokeWidth = 16f
                    )
                    drawLine(
                        color = Color(0xFFF8FAFC),
                        start = Offset(w * 0.1f, 0f),
                        end = Offset(w * 0.85f, h),
                        strokeWidth = 4f
                    )

                    // Outer Ring Road corridor
                    val ringPath = Path().apply {
                        moveTo(w * 0.1f, h * 0.8f)
                        quadraticTo(w * 0.5f, h * 0.2f, w * 0.9f, h * 0.7f)
                    }
                    drawPath(
                        path = ringPath,
                        color = Color(0xFF38BDF8),
                        style = Stroke(width = 8f)
                    )
                }

                // Selected property parcel boundary highlight
                selectedProperty?.let { prop ->
                    val px = prop.mapCoordinatesX * w
                    val py = prop.mapCoordinatesY * h

                    // Parcel boundary box
                    drawRect(
                        color = Color(0x3310B981),
                        topLeft = Offset(px - 35f, py - 35f),
                        size = Size(70f, 70f)
                    )
                    drawRect(
                        color = EmeraldDark,
                        topLeft = Offset(px - 35f, py - 35f),
                        size = Size(70f, 70f),
                        style = Stroke(width = 3f)
                    )
                }
            }

            // Interactive Pin Markers rendered above Canvas
            filteredList.forEach { prop ->
                val isSelected = selectedProperty?.id == prop.id
                val pinLeft = (prop.mapCoordinatesX * mapWidth * scale + offsetX).dp
                val pinTop = (prop.mapCoordinatesY * mapHeight * scale + offsetY).dp

                Box(
                    modifier = Modifier
                        .offset(pinLeft, pinTop)
                        .clickable {
                            onSelectProperty(if (isSelected) null else prop)
                        }
                        .testTag("map_marker_${prop.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Price tag badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldDark else NavyPrimary,
                            shadowElevation = if (isSelected) 6.dp else 2.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color.White else BorderLight
                            )
                        ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else EmeraldAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = prop.formattedPrice,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Pointer dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) EmeraldDark else NavyPrimary)
                            )
                        }
                    }
                }
            }

        // Top Filter Bar & Region Switcher
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Region Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                regions.forEach { reg ->
                    val isCur = selectedRegion == reg
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isCur) NavyPrimary else Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable {
                            selectedRegion = reg
                            onSelectProperty(null)
                        }
                    ) {
                        Text(
                            text = reg,
                            fontSize = 12.sp,
                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCur) Color.White else SlateTextDark,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Layer Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showCadastralGrid) "Grid: On" else "Grid: Off",
                            fontSize = 11.sp,
                            color = if (showCadastralGrid) EmeraldDark else SlateTextMuted,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showCadastralGrid = !showCadastralGrid }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                        Box(modifier = Modifier.width(1.dp).height(12.dp).background(BorderLight))
                        Text(
                            text = if (showInfraCorridors) "Highways: On" else "Highways: Off",
                            fontSize = 11.sp,
                            color = if (showInfraCorridors) NavyPrimary else SlateTextMuted,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showInfraCorridors = !showInfraCorridors }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Map Zoom Controls
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                IconButton(
                    onClick = { scale = (scale + 0.25f).coerceAtMost(2.5f) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = NavyPrimary
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                IconButton(
                    onClick = { scale = (scale - 0.25f).coerceAtLeast(0.7f) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = NavyPrimary
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                IconButton(
                    onClick = {
                        scale = 1.0f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Reset Center",
                        tint = EmeraldDark
                    )
                }
            }
        }

        // Bottom Selected Property Sheet Preview Card
        selectedProperty?.let { prop ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .testTag("map_property_preview_sheet")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = prop.imageUrl,
                                contentDescription = prop.title,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = prop.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextDark,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${prop.locality}, ${prop.city}",
                                    fontSize = 11.sp,
                                    color = SlateTextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = prop.formattedPrice,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NavyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "₹${prop.pricePerSqFt.toInt()}/sq.ft",
                                        fontSize = 11.sp,
                                        color = EmeraldDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { onSelectProperty(null) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close preview",
                                tint = SlateTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cadastral Survey & Road dimensions highlight
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Survey: ${prop.surveyNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateTextMedium
                        )
                        Text(
                            text = "Dims: ${prop.dimensions}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateTextMedium
                        )
                        Text(
                            text = "Road: ${prop.roadWidthFt} ft",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onViewPropertyDetails(prop) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("view_details_from_map")
                    ) {
                        Text(
                            text = "View Full Land Dossier & AI Report",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
