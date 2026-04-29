package com.qiblaarfinder.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qiblaarfinder.domain.model.PrayerSchedule
import com.qiblaarfinder.ui.MainUiState
import com.qiblaarfinder.ui.components.KaabaDirectionMarker
import kotlin.math.abs

@Composable
fun HomeScreen(
    uiState: MainUiState,
    onRefreshLocation: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onOpenAr: () -> Unit,
    onSearchCity: (String) -> Unit,
    onSaveCoordinates: (String, String, String) -> Unit,
    onMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var showManualDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            onMessageShown()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                HeroCard(
                    uiState = uiState,
                    onRequestLocationPermission = onRequestLocationPermission,
                    onRefreshLocation = onRefreshLocation,
                )

                CompassCard(uiState = uiState)

                LocationCard(
                    uiState = uiState,
                    onRefreshLocation = onRefreshLocation,
                    onOpenManualInput = { showManualDialog = true },
                )

                PrayerTimesCard(uiState.prayerSchedule, uiState.isLoadingPrayerSchedule)

                ArEntryCard(onOpenAr = onOpenAr)
            }
        }
    }

    if (showManualDialog) {
        ManualLocationDialog(
            onDismiss = { showManualDialog = false },
            onSearchCity = { query ->
                onSearchCity(query)
                showManualDialog = false
            },
            onSaveCoordinates = { cityName, latitude, longitude ->
                onSaveCoordinates(cityName, latitude, longitude)
                showManualDialog = false
            },
        )
    }
}

@Composable
private fun HeroCard(
    uiState: MainUiState,
    onRequestLocationPermission: () -> Unit,
    onRefreshLocation: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.82f),
                        ),
                    ),
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Qibla AR Finder",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = when {
                    uiState.isAligned -> "Arah kamu sudah masuk toleransi < 2 derajat."
                    uiState.directionDelta != null -> guidanceText(uiState.directionDelta)
                    else -> "Aktifkan lokasi untuk mulai mendapatkan arah kiblat otomatis."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(if (uiState.isAligned) "Presisi" else "Butuh Koreksi")
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f),
                        labelColor = MaterialTheme.colorScheme.onPrimary,
                        leadingIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                        )
                    },
                )

                if (uiState.currentLocation != null) {
                    AssistChip(
                        onClick = { },
                        label = { Text(uiState.currentLocation.source.label) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f),
                            labelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                }
            }

            if (uiState.isLoadingLocation) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                )
            }

            if (!uiState.hasLocationPermission) {
                Button(onClick = onRequestLocationPermission) {
                    Text("Aktifkan Lokasi")
                }
            } else {
                ElevatedButton(onClick = onRefreshLocation) {
                    Icon(Icons.Default.GpsFixed, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Refresh GPS")
                }
            }
        }
    }
}

@Composable
private fun CompassCard(uiState: MainUiState) {
    val angle by animateFloatAsState(
        targetValue = uiState.directionDelta ?: 0f,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = 210f,
        ),
        label = "qibla_compass_angle",
    )
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    val tickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
    val guideColor = if (uiState.isAligned) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "2D Compass",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(
                        color = outlineColor,
                        style = Stroke(width = strokeWidth),
                    )

                    repeat(24) { index ->
                        val angleRad = Math.toRadians((index * 15).toDouble())
                        val startRadius = if (index % 6 == 0) size.minDimension * 0.36f else size.minDimension * 0.40f
                        val endRadius = size.minDimension * 0.45f
                        val start = Offset(
                            x = center.x + startRadius * kotlin.math.sin(angleRad).toFloat(),
                            y = center.y - startRadius * kotlin.math.cos(angleRad).toFloat(),
                        )
                        val end = Offset(
                            x = center.x + endRadius * kotlin.math.sin(angleRad).toFloat(),
                            y = center.y - endRadius * kotlin.math.cos(angleRad).toFloat(),
                        )
                        drawLine(
                            color = tickColor,
                            start = start,
                            end = end,
                            strokeWidth = if (index % 6 == 0) 6f else 3f,
                            cap = StrokeCap.Round,
                        )
                    }

                    drawLine(
                        color = guideColor,
                        start = center,
                        end = Offset(center.x, size.height * 0.14f),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }

                Text(
                    text = "Atas HP",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = guideColor,
                    fontWeight = FontWeight.Bold,
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(angle),
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 34.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        KaabaDirectionMarker(
                            aligned = uiState.isAligned,
                            showStem = true,
                            stemHeight = 84.dp,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }

            Text(
                text = uiState.qiblaBearing?.let { "Azimuth Qiblat ${it.format(1)} deg" }
                    ?: "Azimuth Qiblat menunggu lokasi aktif",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Heading perangkat ${uiState.deviceHeading.format(1)} deg",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Pegang handphone tegak portrait, lalu luruskan simbol Ka'bah ke arah atas HP.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = when {
                    uiState.directionDelta == null -> "Butuh lokasi untuk mengunci arah kiblat."
                    uiState.isAligned -> "Arah tegak handphone sudah lurus ke kiblat."
                    else -> guidanceText(uiState.directionDelta)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun LocationCard(
    uiState: MainUiState,
    onRefreshLocation: () -> Unit,
    onOpenManualInput: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Lokasi Aktif",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if (uiState.currentLocation == null) {
                Text(
                    text = "Belum ada lokasi aktif. Kamu bisa pakai GPS, cari kota, atau isi koordinat manual.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.currentLocation.cityName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Text(
                            text = "${uiState.currentLocation.latitude.format(5)}, ${uiState.currentLocation.longitude.format(5)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    AssistChip(
                        onClick = { },
                        label = { Text(uiState.currentLocation.source.label) },
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(onClick = onRefreshLocation) {
                    Icon(Icons.Default.GpsFixed, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GPS")
                }
                FilledTonalButton(onClick = onOpenManualInput) {
                    Icon(Icons.Default.EditLocationAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manual")
                }
            }
        }
    }
}

@Composable
private fun PrayerTimesCard(schedule: PrayerSchedule?, isLoading: Boolean) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Jadwal Salat",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                    Text("Menghitung jadwal salat lokal...")
                }
            } else if (schedule == null) {
                Text(
                    text = "Jadwal salat akan muncul setelah lokasi aktif.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                val nextPrayer = schedule.nextPrayer()

                AssistChip(
                    onClick = { },
                    label = { Text("Berikutnya ${nextPrayer.first} ${schedule.formatted(nextPrayer.second)}") },
                    leadingIcon = {
                        Icon(Icons.Default.Explore, contentDescription = null)
                    },
                )

                Text(
                    text = "Sunrise ${schedule.sunriseLabel()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                schedule.prayerEntries().forEachIndexed { index, (name, time) ->
                    val highlighted = name == nextPrayer.first
                    PrayerRow(
                        name = name,
                        time = schedule.formatted(time),
                        highlighted = highlighted,
                    )
                    if (index != schedule.prayerEntries().lastIndex) {
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayerRow(name: String, time: String, highlighted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (highlighted) FontWeight.SemiBold else FontWeight.Normal,
        )
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (highlighted) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun ArEntryCard(onOpenAr: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "AR Preview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Buka kamera untuk melihat marker Ka'bah bergerak mengikuti azimuth qiblat. Ini jadi pondasi transisi ke ARCore penuh di sprint berikutnya.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FilledTonalButton(onClick = onOpenAr) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka AR Overlay")
            }
        }
    }
}

@Composable
private fun ManualLocationDialog(
    onDismiss: () -> Unit,
    onSearchCity: (String) -> Unit,
    onSaveCoordinates: (String, String, String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var cityName by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        },
        title = {
            Text("Input Manual")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Cari Kota") },
                    singleLine = true,
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = { onSearchCity(query) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Gunakan Hasil Kota")
                }

                Divider()

                OutlinedTextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    label = { Text("Nama Kota / Label") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = { onSaveCoordinates(cityName, latitude, longitude) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Simpan Koordinat")
                }
            }
        },
    )
}

private fun guidanceText(delta: Float): String {
    if (abs(delta) <= 2f) return "Arah kamu sudah presisi ke kiblat."
    return if (delta > 0f) {
        "Putar ${delta.format(1)} derajat ke kanan."
    } else {
        "Putar ${abs(delta).format(1)} derajat ke kiri."
    }
}

private fun Float.format(decimals: Int): String = "%.${decimals}f".format(this)

private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
