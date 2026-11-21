package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.FeedbackBannerType
import com.example.model.QuizFightUiState
import com.example.ui.components.ArcadeButton
import com.example.ui.components.ArcadePanel
import com.example.ui.components.PixelDivider
import com.example.ui.pixel.FightingArenaCanvas
import com.example.ui.theme.ArcadeAmber
import com.example.ui.theme.ArcadeCream
import com.example.ui.theme.ArcadeCrimson
import com.example.ui.theme.ArcadeCyan
import com.example.ui.theme.ArcadeEmerald
import com.example.ui.theme.ArcadeEmeraldDark
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.ArcadeMutedText
import com.example.ui.theme.ArcadePanel
import com.example.ui.theme.ArcadePanelBorderLight
import com.example.ui.theme.ArcadeVoid

@Composable
fun ResultScreen(
    uiState: QuizFightUiState,
    onToggleSound: () -> Unit,
    onPlayAgain: () -> Unit,
    onCreateNewQuizConfirmed: () -> Unit
) {
    var showNewQuizConfirm by remember { mutableStateOf(false) }
    val didWin = uiState.didStudentWin
    val accentColor = if (didWin) ArcadeEmerald else ArcadeCrimson

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Upper arena displaying continuous Victory & Defeat poses
        FightingArenaCanvas(
            player1Pose = uiState.player1Pose,
            player2Pose = uiState.player2Pose,
            player1OffsetX = uiState.player1OffsetX,
            player2OffsetX = uiState.player2OffsetX,
            player1OffsetY = uiState.player1OffsetY,
            player2OffsetY = uiState.player2OffsetY,
            impactFlashStrength = 0f,
            impactTargetPlayer = 0,
            isMissEffectVisible = false,
            isComboAuraActive = uiState.isComboAuraActive,
            screenShakeTrigger = 0,
            feedbackBanner = FeedbackBannerType.NONE,
            isSoundEnabled = uiState.isSoundEnabled,
            onToggleSound = onToggleSound,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f)
        )

        PixelDivider(color = accentColor)

        Column(
            modifier = Modifier
                .weight(0.58f)
                .fillMaxWidth()
                .background(ArcadePanel)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (didWin) {
                        stringResource(R.string.result_win_title)
                    } else {
                        stringResource(R.string.result_lose_title)
                    },
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 26.sp),
                    color = accentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("result_title")
                )

                Text(
                    text = if (didWin) {
                        stringResource(R.string.result_win_subtitle)
                    } else {
                        stringResource(R.string.result_lose_subtitle)
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                    color = ArcadeGold,
                    textAlign = TextAlign.Center
                )

                ArcadePanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = accentColor,
                    backgroundColor = ArcadeVoid,
                    contentPadding = 14.dp
                ) {
                    Text(
                        text = "${uiState.correctCount} / ${uiState.totalQuestions} CORRECT",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 18.sp),
                        color = ArcadeCream,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("result_score_text")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip(
                            label = "CORRECT",
                            value = uiState.correctCount.toString(),
                            color = ArcadeEmerald
                        )
                        StatChip(
                            label = "WRONG",
                            value = uiState.wrongCount.toString(),
                            color = ArcadeCrimson
                        )
                        StatChip(
                            label = "MAX STREAK",
                            value = uiState.highestStreak.toString(),
                            color = ArcadeCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "WIN TARGET: ${uiState.requiredCorrectToWin} / ${uiState.totalQuestions} (60%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArcadeMutedText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ArcadeButton(
                    text = stringResource(R.string.btn_play_again),
                    onClick = onPlayAgain,
                    containerColor = ArcadeEmerald,
                    shadowColor = ArcadeEmeraldDark,
                    contentColor = ArcadeVoid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("play_again_button"),
                    minHeight = 52.dp
                )

                ArcadeButton(
                    text = stringResource(R.string.btn_create_new_quiz),
                    onClick = { showNewQuizConfirm = true },
                    containerColor = ArcadeAmber,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_new_quiz_button"),
                    minHeight = 50.dp
                )
            }
        }
    }

    if (showNewQuizConfirm) {
        Dialog(onDismissRequest = { showNewQuizConfirm = false }) {
            ArcadePanel(
                borderColor = ArcadeAmber,
                contentPadding = 18.dp
            ) {
                Text(
                    text = stringResource(R.string.dialog_new_quiz_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcadeGold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.dialog_new_quiz_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcadeCream
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ArcadeButton(
                        text = stringResource(R.string.btn_cancel),
                        onClick = { showNewQuizConfirm = false },
                        containerColor = ArcadePanel,
                        shadowColor = ArcadePanelBorderLight,
                        contentColor = ArcadeCream,
                        modifier = Modifier.weight(1f)
                    )
                    ArcadeButton(
                        text = stringResource(R.string.btn_confirm),
                        onClick = {
                            showNewQuizConfirm = false
                            onCreateNewQuizConfirmed()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_new_quiz_button")
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = ArcadeMutedText
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}
