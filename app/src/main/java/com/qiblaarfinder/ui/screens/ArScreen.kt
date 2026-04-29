package com.qiblaarfinder.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.qiblaarfinder.ui.MainUiState
import com.qiblaarfinder.ui.components.CameraPreview
import com.qiblaarfinder.ui.components.KaabaDirectionMarker
import kotlin.math.abs

@Composable
fun ArScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var hasCameraPermission by rememberSaveable { mutableStateOf(context.hasCameraPermission()) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreview(modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.55f),
                            ),
                        ),
                    ),
            )
            OverlayGuidance(uiState = uiState, onBack = onBack)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    modifier = Modifier.padding(24.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Izin Kamera Dibutuhkan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Layar ini memakai live camera overlay untuk meletakkan marker Ka'bah sesuai azimuth qiblat.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Text("   Aktifkan Kamera")
                        }
                        FilledTonalButton(onClick = onBack) {
                            Text("Kembali")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlayGuidance(uiState: MainUiState, onBack: () -> Unit) {
    val delta = uiState.directionDelta

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        val clamped = (delta ?: 0f).coerceIn(-60f, 60f)
        val markerOffset = (maxWidth / 2 - 56.dp) * (clamped / 60f)

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.28f), CircleShape),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.White,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "AR Overlay",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = uiState.qiblaBearing?.let { "Qiblat ${it.format(1)} deg" } ?: "Menunggu azimuth",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.88f),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(110.dp)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(28.dp),
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(4.dp)
                .size(height = 148.dp, width = 4.dp)
                .background(
                    color = if (uiState.isAligned) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.95f)
                    } else {
                        Color.White.copy(alpha = 0.52f)
                    },
                    shape = RoundedCornerShape(999.dp),
                ),
        )
        Text(
            text = "Atas HP",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-94).dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        if (delta != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = markerOffset)
                    .padding(vertical = 12.dp),
            ) {
                KaabaDirectionMarker(
                    aligned = uiState.isAligned,
                    bodyWidth = 46.dp,
                    bodyHeight = 52.dp,
                    label = "KA'BAH",
                )
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f),
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = when {
                        delta == null -> "Lokasi dan heading sedang disiapkan."
                        uiState.isAligned -> "Simbol Ka'bah sudah lurus dengan arah atas handphone."
                        delta > 0f -> "Geser pandangan ${delta.format(1)} derajat ke kanan sampai marker masuk area tengah."
                        else -> "Geser pandangan ${abs(delta).format(1)} derajat ke kiri sampai marker masuk area tengah."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Heading ${uiState.deviceHeading.format(1)} deg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Text(
                    text = "Pegang handphone portrait lalu sejajarkan simbol Ka'bah dengan garis tengah vertikal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun Context.hasCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA,
    ) == PackageManager.PERMISSION_GRANTED
}

private fun Float.format(decimals: Int): String = "%.${decimals}f".format(this)
