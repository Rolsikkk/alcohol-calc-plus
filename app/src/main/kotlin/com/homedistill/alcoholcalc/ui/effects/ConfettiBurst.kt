package com.homedistill.alcoholcalc.ui.effects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val PARTICLE_COUNT = 26
private const val BURST_DURATION_MS = 850

private data class Particle(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val spin: Float,
)

private fun randomParticles(colors: List<Color>): List<Particle> = List(PARTICLE_COUNT) {
    Particle(
        angle = Random.nextFloat() * 360f,
        speed = 80f + Random.nextFloat() * 160f,
        color = colors.random(),
        size = 3f + Random.nextFloat() * 5f,
        spin = (Random.nextFloat() - 0.5f) * 20f,
    )
}

/**
 * A one-shot confetti burst from the center of this composable's bounds. Increment [trigger]
 * (e.g. a counter) to fire a new burst; re-fires whenever the value changes.
 */
@Composable
fun ConfettiBurst(trigger: Int, modifier: Modifier = Modifier) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        Color(0xFF2F80ED),
        Color(0xFFFFC107),
        Color(0xFF27AE60),
        Color(0xFFEB5757),
    )
    var particles by remember { mutableStateOf(emptyList<Particle>()) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger <= 0) return@LaunchedEffect
        particles = randomParticles(colors)
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(BURST_DURATION_MS, easing = LinearOutSlowInEasing))
    }

    if (particles.isNotEmpty() && progress.value < 1f) {
        Canvas(modifier = modifier) {
            val t = progress.value
            val fade = (1f - t).coerceIn(0f, 1f)
            val center = Offset(size.width / 2f, size.height / 2f)
            particles.forEach { p ->
                val rad = Math.toRadians(p.angle.toDouble())
                val distance = p.speed * t
                val gravity = 260f * t * t
                val x = center.x + (cos(rad) * distance).toFloat()
                val y = center.y + (sin(rad) * distance).toFloat() + gravity
                rotate(degrees = p.spin * t * 40, pivot = Offset(x, y)) {
                    drawRect(
                        color = p.color.copy(alpha = fade),
                        topLeft = Offset(x - p.size / 2, y - p.size / 2),
                        size = androidx.compose.ui.geometry.Size(p.size, p.size),
                    )
                }
            }
        }
    }
}
