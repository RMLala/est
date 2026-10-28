package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AudioPlayerManager
import com.example.util.AudioPlayerState
import com.example.util.AudioRecordingState
import kotlin.random.Random

@Composable
fun AudioRecordingWidget(
    recordingState: AudioRecordingState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_recording_widget"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF2F2) // Light red alert background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (recordingState.isPaused) Color.Gray
                                else Color.Red.copy(alpha = pulseAlpha)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (recordingState.isPaused) "Enregistrement en pause" else "Enregistrement audio en cours...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (recordingState.isPaused) Color.DarkGray else Color(0xFFB91C1C)
                    )
                }

                Text(
                    text = AudioPlayerManager.formatTime(recordingState.elapsedSeconds),
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF991B1B)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic live waveform visualizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val barCount = 20
                for (i in 0 until barCount) {
                    val baseHeight = if (recordingState.isPaused) 4.dp else {
                        val factor = (Math.sin((i.toDouble() + System.currentTimeMillis() / 200.0)).toFloat() + 1f) / 2f
                        val effectiveAmp = recordingState.amplitudeLevel.coerceIn(0.1f, 1f)
                        (6 + (effectiveAmp * 26 * factor)).dp
                    }
                    Box(
                        modifier = Modifier
                            .width(3.5.dp)
                            .height(baseHeight)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (recordingState.isPaused) Color.Gray else Color(0xFFDC2626))
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB91C1C))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Annuler", fontSize = 12.sp)
                }

                if (recordingState.isPaused) {
                    Button(
                        onClick = onResume,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reprendre", fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onPause,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B5563))
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause", fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Valider & Lier", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AudioPlayerCard(
    filePath: String,
    title: String = "Enregistrement audio",
    durationSeconds: Int = 0,
    playerState: AudioPlayerState,
    onTogglePlay: () -> Unit,
    onSeek: (Int) -> Unit,
    onSetSpeed: (Float) -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isCurrentAudio = playerState.playingPath == filePath
    val isPlaying = isCurrentAudio && playerState.isPlaying

    val currentMs = if (isCurrentAudio) playerState.currentPositionMs else 0
    val totalMs = if (isCurrentAudio && playerState.totalDurationMs > 0) {
        playerState.totalDurationMs
    } else {
        (durationSeconds * 1000).coerceAtLeast(1000)
    }

    val sliderProgress = (currentMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_player_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        IconButton(onClick = onTogglePlay) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Lecture audio",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${AudioPlayerManager.formatTimeMs(currentMs)} / ${AudioPlayerManager.formatTimeMs(totalMs)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer l'audio",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Scrub Slider
            Slider(
                value = sliderProgress,
                onValueChange = { frac ->
                    val newPos = (frac * totalMs).toInt()
                    onSeek(newPos)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            )

            // Speed selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vitesse d'écoute :",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(1.0f to "1x", 1.25f to "1.25x", 1.5f to "1.5x", 2.0f to "2x").forEach { (speedVal, label) ->
                        val isSpeedActive = isCurrentAudio && playerState.speed == speedVal
                        Surface(
                            onClick = { onSetSpeed(speedVal) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSpeedActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(width = 38.dp, height = 24.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSpeedActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
