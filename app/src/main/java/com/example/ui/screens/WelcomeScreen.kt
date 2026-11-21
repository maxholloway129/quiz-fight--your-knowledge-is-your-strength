package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FighterPose
import com.example.ui.components.ArcadeButton
import com.example.ui.components.ArcadePanel
import com.example.ui.components.PixelDivider
import com.example.ui.components.SoundToggleButton
import com.example.ui.pixel.FighterSprites
import com.example.ui.theme.ArcadeAmber
import com.example.ui.theme.ArcadeCream
import com.example.ui.theme.ArcadeCyan
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.ArcadeMutedText
import com.example.ui.theme.ArcadePanel
import com.example.ui.theme.ArcadeVoid
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun WelcomeScreen(
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onStartGame: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_pulse"
    )
    val sparkPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spark_phase"
    )

    var spriteFrame by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(360L)
            spriteFrame = (spriteFrame + 1) % 2
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
    ) {
        // Pixel-art desert backdrop image
        Image(
            painter = painterResource(id = R.drawable.img_title_arena_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle atmospheric gradient scrim + floating retro pixel embers
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ArcadeVoid.copy(alpha = 0.78f),
                            ArcadeVoid.copy(alpha = 0.48f),
                            ArcadeVoid.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until 14) {
                val seed = i * 0.071f
                val x = ((seed * 1.7f + sin((sparkPhase * 6.28f + i).toDouble()).toFloat() * 0.04f) % 1f) * size.width
                val y = ((1f - ((sparkPhase + seed) % 1f)) * size.height)
                val color = if (i % 2 == 0) ArcadeGold.copy(alpha = 0.55f) else ArcadeAmber.copy(alpha = 0.45f)
                drawRect(color = color, topLeft = Offset(x, y), size = Size(6f, 6f))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .widthIn(max = 560.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar: Arcade coin/insert badge + Sound toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(ArcadeVoid.copy(alpha = 0.85f))
                        .border(2.dp, ArcadeAmber)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "2P PASS & PLAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArcadeAmber
                    )
                }

                SoundToggleButton(
                    isSoundEnabled = isSoundEnabled,
                    onToggle = onToggleSound,
                    modifier = Modifier.testTag("welcome_sound_toggle")
                )
            }

            // Center Title Logo + Fighters Face-off Preview
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Animated Arcade Logo Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ArcadeVoid.copy(alpha = 0.88f))
                        .border(3.dp, ArcadeGold.copy(alpha = pulse))
                        .padding(vertical = 14.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.title_quiz_fight),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 30.sp,
                                lineHeight = 36.sp
                            ),
                            color = ArcadeGold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("welcome_title")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PixelDivider(color = ArcadeAmber, modifier = Modifier.fillMaxWidth(0.75f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.welcome_tagline),
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                            color = ArcadeCyan,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Live animated pixel face-off preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(ArcadePanel.copy(alpha = 0.72f))
                        .border(2.dp, ArcadeAmber.copy(alpha = 0.7f))
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(68.dp)) {
                        val bmp = FighterSprites.getPlayer1Sprite(FighterPose.IDLE, spriteFrame)
                        drawImage(
                            image = bmp,
                            dstOffset = IntOffset.Zero,
                            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                            filterQuality = FilterQuality.None
                        )
                    }

                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.headlineLarge,
                        color = ArcadeAmber
                    )

                    Canvas(modifier = Modifier.size(68.dp)) {
                        val bmp = FighterSprites.getPlayer2Sprite(FighterPose.IDLE, spriteFrame)
                        drawImage(
                            image = bmp,
                            dstOffset = IntOffset.Zero,
                            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                            filterQuality = FilterQuality.None
                        )
                    }
                }

                // Supporting Copy Panel
                ArcadePanel(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = ArcadePanel.copy(alpha = 0.92f),
                    contentPadding = 12.dp
                ) {
                    Text(
                        text = stringResource(R.string.welcome_supporting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = ArcadeCream,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.welcome_consequence),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ArcadeMutedText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Bottom Primary CTA
            ArcadeButton(
                text = stringResource(R.string.btn_start_game),
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_game_button"),
                minHeight = 56.dp
            )
        }
    }
}
