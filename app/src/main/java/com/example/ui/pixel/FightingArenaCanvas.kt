package com.example.ui.pixel

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FeedbackBannerType
import com.example.model.FighterPose
import com.example.ui.components.SoundToggleButton
import com.example.ui.theme.ArcadeAmber
import com.example.ui.theme.ArcadeCrimson
import com.example.ui.theme.ArcadeCyan
import com.example.ui.theme.ArcadeEmerald
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.ArcadePanelBorderDark
import com.example.ui.theme.ArcadeVoid
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun FightingArenaCanvas(
    player1Pose: FighterPose,
    player2Pose: FighterPose,
    player1OffsetX: Float,
    player2OffsetX: Float,
    player1OffsetY: Float,
    player2OffsetY: Float,
    impactFlashStrength: Float,
    impactTargetPlayer: Int,
    isMissEffectVisible: Boolean,
    isComboAuraActive: Boolean,
    screenShakeTrigger: Int,
    feedbackBanner: FeedbackBannerType,
    isSoundEnabled: Boolean,
    onToggleSound: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    // 2-frame sprite animation ticker (~340ms per frame)
    var frameIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(340L)
            frameIndex = (frameIndex + 1) % 2
        }
    }

    // Subtle screen shake offset when hit occurs
    var shakeOffsetPx by remember { mutableIntStateOf(0) }
    LaunchedEffect(screenShakeTrigger) {
        if (screenShakeTrigger > 0) {
            val pattern = listOf(10, -10, 7, -6, 3, 0)
            for (step in pattern) {
                shakeOffsetPx = step
                delay(32L)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "arena_ambient")
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_drift"
    )
    val dustDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dust_drift"
    )
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    val animP1X by animateFloatAsState(
        targetValue = player1OffsetX,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "p1_x"
    )
    val animP2X by animateFloatAsState(
        targetValue = player2OffsetX,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "p2_x"
    )
    val animP1Y by animateFloatAsState(
        targetValue = player1OffsetY,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
        label = "p1_y"
    )
    val animP2Y by animateFloatAsState(
        targetValue = player2OffsetY,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
        label = "p2_y"
    )

    Box(
        modifier = modifier
            .background(ArcadeVoid)
            .border(3.dp, ArcadePanelBorderDark)
            .testTag("fighting_arena_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val arenaBmp = DesertArenaRenderer.arenaBitmap

            // 1. Draw crisp pixel-art desert background (nearest-neighbor)
            drawImage(
                image = arenaBmp,
                dstOffset = IntOffset(shakeOffsetPx / 2, 0),
                dstSize = IntSize(w.roundToInt(), h.roundToInt()),
                filterQuality = FilterQuality.None
            )

            // 2. Drifting retro pixel clouds in upper sky
            val pixelUnit = (w / DesertArenaRenderer.ARENA_WIDTH).coerceAtLeast(2f)
            val cloud1X = ((cloudDrift * (w + 90f)) - 45f)
            val cloud2X = (((cloudDrift + 0.52f) % 1f) * (w + 90f) - 45f)
            drawPixelCloud(cloud1X, h * 0.16f, pixelUnit)
            drawPixelCloud(cloud2X, h * 0.25f, pixelUnit * 0.85f)

            // 3. Subtle drifting desert dust particles near ground
            for (i in 0 until 8) {
                val seed = i * 0.137f
                val px = (((dustDrift + seed) % 1f) * w)
                val py = h * (0.72f + (i % 4) * 0.055f) + sin((dustDrift * 12f + i).toDouble()).toFloat() * 4f
                drawRect(
                    color = Color(0x66FFE2A8),
                    topLeft = Offset(px, py),
                    size = Size(pixelUnit * 1.6f, pixelUnit * 1.1f)
                )
            }

            // 4. Compute responsive fighter sprite size & ground baseline
            val spriteAspect = 28f / 32f
            val fighterH = (h * 0.45f).coerceIn(72f, h * 0.56f)
            val fighterW = fighterH * spriteAspect
            val groundY = h * 0.87f

            // Base X positions leave generous center space for attack dashes
            val p1BaseX = w * 0.16f
            val p2BaseX = w * 0.84f - fighterW
            val arenaSpan = (p2BaseX - p1BaseX).coerceAtLeast(fighterW * 0.8f)

            val p1DrawX = (p1BaseX + animP1X * arenaSpan + shakeOffsetPx)
                .coerceIn(w * 0.04f, w - fighterW - w * 0.04f)
            val p2DrawX = (p2BaseX + animP2X * arenaSpan + shakeOffsetPx)
                .coerceIn(w * 0.04f, w - fighterW - w * 0.04f)

            val p1DrawY = (groundY - fighterH + animP1Y * h).coerceAtLeast(h * 0.15f)
            val p2DrawY = (groundY - fighterH + animP2Y * h).coerceAtLeast(h * 0.15f)

            // 5. Contact ground shadows under both fighters
            drawRect(
                color = Color(0x552E140B),
                topLeft = Offset(p1DrawX + fighterW * 0.15f, groundY - pixelUnit * 1.5f),
                size = Size(fighterW * 0.70f, pixelUnit * 2.5f)
            )
            drawRect(
                color = Color(0x552E140B),
                topLeft = Offset(p2DrawX + fighterW * 0.15f, groundY - pixelUnit * 1.5f),
                size = Size(fighterW * 0.70f, pixelUnit * 2.5f)
            )

            // 6. Combo aura pixels around Player 1 when 6-hit streak is active
            if (isComboAuraActive) {
                val auraColor = if (frameIndex == 0) {
                    ArcadeGold.copy(alpha = 0.45f * auraPulse)
                } else {
                    ArcadeCyan.copy(alpha = 0.40f * auraPulse)
                }
                drawRect(
                    color = auraColor,
                    topLeft = Offset(p1DrawX - pixelUnit * 2f, p1DrawY - pixelUnit * 2f),
                    size = Size(fighterW + pixelUnit * 4f, fighterH + pixelUnit * 2f)
                )
            }

            // 7. Draw Player 2 first (so P1 appears on top during combo pose over P2)
            val p2Bmp = FighterSprites.getPlayer2Sprite(player2Pose, frameIndex)
            val p2HitTint = if (impactTargetPlayer == 2 && impactFlashStrength > 0.1f) {
                brightFlashColorFilter(impactFlashStrength)
            } else {
                null
            }
            drawImage(
                image = p2Bmp,
                dstOffset = IntOffset(p2DrawX.roundToInt(), p2DrawY.roundToInt()),
                dstSize = IntSize(fighterW.roundToInt(), fighterH.roundToInt()),
                filterQuality = FilterQuality.None,
                colorFilter = p2HitTint
            )

            // 8. Draw Player 1
            val p1Bmp = FighterSprites.getPlayer1Sprite(player1Pose, frameIndex)
            val p1HitTint = if (impactTargetPlayer == 1 && impactFlashStrength > 0.1f) {
                brightFlashColorFilter(impactFlashStrength)
            } else {
                null
            }
            drawImage(
                image = p1Bmp,
                dstOffset = IntOffset(p1DrawX.roundToInt(), p1DrawY.roundToInt()),
                dstSize = IntSize(fighterW.roundToInt(), fighterH.roundToInt()),
                filterQuality = FilterQuality.None,
                colorFilter = p1HitTint
            )

            // 9. Retro pixel impact starburst sparks
            if (impactFlashStrength > 0.1f && impactTargetPlayer != 0) {
                val sparkCx = if (impactTargetPlayer == 2) p2DrawX + fighterW * 0.35f else p1DrawX + fighterW * 0.65f
                val sparkCy = (if (impactTargetPlayer == 2) p2DrawY else p1DrawY) + fighterH * 0.42f
                drawPixelImpactBurst(
                    cx = sparkCx,
                    cy = sparkCy,
                    pixelUnit = pixelUnit * (1.3f + impactFlashStrength * 0.8f),
                    primary = if (isComboAuraActive) ArcadeCyan else ArcadeGold,
                    secondary = Color.White
                )
            }

            // 10. Dodge / Miss puff sparks near Player 2
            if (isMissEffectVisible) {
                val missCx = p2DrawX + fighterW * 0.2f
                val missCy = p2DrawY + fighterH * 0.28f
                for (k in 0 until 5) {
                    val angle = k * 0.9f
                    drawRect(
                        color = Color(0xCCFFF8E7),
                        topLeft = Offset(
                            missCx + kotlin.math.cos(angle) * pixelUnit * 5f,
                            missCy + sin(angle) * pixelUnit * 4f
                        ),
                        size = Size(pixelUnit * 2f, pixelUnit * 2f)
                    )
                }
            }
        }

        // Top HUD: PLAYER 1 badge (left), Sound toggle (center), PLAYER 2 badge (right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FighterNameBadge(
                name = stringResource(R.string.player_1_name),
                accentColor = ArcadeCyan,
                modifier = Modifier.testTag("player_1_label")
            )

            if (onToggleSound != null) {
                SoundToggleButton(
                    isSoundEnabled = isSoundEnabled,
                    onToggle = onToggleSound,
                    compact = true,
                    modifier = Modifier.testTag("arena_sound_toggle")
                )
            }

            FighterNameBadge(
                name = stringResource(R.string.player_2_name),
                accentColor = ArcadeEmerald,
                modifier = Modifier.testTag("player_2_label")
            )
        }

        // Feedback / Streak Banner overlay at bottom-top edge of arena without covering fighters' heads
        if (feedbackBanner != FeedbackBannerType.NONE) {
            val (bannerText, bannerColor) = when (feedbackBanner) {
                FeedbackBannerType.CORRECT -> stringResource(R.string.feedback_correct) to ArcadeEmerald
                FeedbackBannerType.WRONG -> stringResource(R.string.feedback_wrong) to ArcadeCrimson
                FeedbackBannerType.COMBO_6_STREAK -> stringResource(R.string.feedback_streak_6) to ArcadeGold
                FeedbackBannerType.NONE -> "" to ArcadeGold
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 46.dp)
                    .background(ArcadeVoid.copy(alpha = 0.88f))
                    .border(2.dp, bannerColor)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("arena_feedback_banner")
            ) {
                Text(
                    text = bannerText,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                    color = bannerColor
                )
            }
        }
    }
}

@Composable
private fun FighterNameBadge(
    name: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(ArcadeVoid.copy(alpha = 0.82f))
            .border(2.dp, accentColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = accentColor
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPixelCloud(
    x: Float,
    y: Float,
    unit: Float
) {
    val cloudColor = Color(0x4DFFE4C4)
    drawRect(cloudColor, Offset(x, y), Size(unit * 14f, unit * 3f))
    drawRect(cloudColor, Offset(x + unit * 3f, y - unit * 2f), Size(unit * 8f, unit * 2f))
    drawRect(cloudColor, Offset(x + unit * 2f, y + unit * 3f), Size(unit * 10f, unit * 1.5f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPixelImpactBurst(
    cx: Float,
    cy: Float,
    pixelUnit: Float,
    primary: Color,
    secondary: Color
) {
    // Crisp cross + diagonal retro pixel starburst
    drawRect(secondary, Offset(cx - pixelUnit * 2f, cy - pixelUnit * 2f), Size(pixelUnit * 4f, pixelUnit * 4f))
    drawRect(primary, Offset(cx - pixelUnit * 6f, cy - pixelUnit), Size(pixelUnit * 12f, pixelUnit * 2f))
    drawRect(primary, Offset(cx - pixelUnit, cy - pixelUnit * 6f), Size(pixelUnit * 2f, pixelUnit * 12f))
    drawRect(ArcadeAmber, Offset(cx - pixelUnit * 4.5f, cy - pixelUnit * 4.5f), Size(pixelUnit * 2f, pixelUnit * 2f))
    drawRect(ArcadeAmber, Offset(cx + pixelUnit * 2.5f, cy - pixelUnit * 4.5f), Size(pixelUnit * 2f, pixelUnit * 2f))
    drawRect(ArcadeAmber, Offset(cx - pixelUnit * 4.5f, cy + pixelUnit * 2.5f), Size(pixelUnit * 2f, pixelUnit * 2f))
    drawRect(ArcadeAmber, Offset(cx + pixelUnit * 2.5f, cy + pixelUnit * 2.5f), Size(pixelUnit * 2f, pixelUnit * 2f))
}

private fun brightFlashColorFilter(strength: Float): ColorFilter {
    val boost = (strength * 140f).coerceIn(0f, 180f)
    return ColorFilter.colorMatrix(
        ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, boost,
                0f, 1f, 0f, 0f, boost * 0.85f,
                0f, 0f, 1f, 0f, boost * 0.85f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    )
}
