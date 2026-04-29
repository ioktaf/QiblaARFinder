package com.qiblaarfinder.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KaabaDirectionMarker(
    aligned: Boolean,
    modifier: Modifier = Modifier,
    bodyWidth: Dp = 40.dp,
    bodyHeight: Dp = 46.dp,
    showStem: Boolean = false,
    stemHeight: Dp = 84.dp,
    label: String? = null,
    labelColor: Color = Color.White,
) {
    val accentColor by animateColorAsState(
        targetValue = if (aligned) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.tertiary
        },
        animationSpec = tween(durationMillis = 280),
        label = "kaaba_marker_color",
    )

    val pulseTransition = rememberInfiniteTransition(label = "kaaba_marker_pulse")
    val alignedPulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "kaaba_marker_pulse_scale",
    )

    val scale by animateFloatAsState(
        targetValue = if (aligned) alignedPulse else 1f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 220f,
        ),
        label = "kaaba_marker_scale",
    )

    Column(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(accentColor.copy(alpha = if (aligned) 0.22f else 0.16f))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(bodyWidth)
                    .height(bodyHeight)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF151515)),
            ) {
                Box(
                    modifier = Modifier
                        .width(bodyWidth * 0.72f)
                        .height(7.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE5C98D)),
                )
                Box(
                    modifier = Modifier
                        .width(bodyWidth * 0.25f)
                        .height(bodyHeight * 0.34f)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFBE8C2F)),
                )
            }
        }

        if (showStem) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(4.dp)
                    .height(stemHeight)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (aligned) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        } else {
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                        },
                    ),
            )
        }

        if (label != null) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                )
                Text(
                    text = "  $label",
                    color = labelColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
