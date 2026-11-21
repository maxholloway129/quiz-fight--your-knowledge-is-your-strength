package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.QuizFightUiState
import com.example.ui.components.ArcadeButton
import com.example.ui.components.ArcadeTextField
import com.example.ui.pixel.FightingArenaCanvas
import com.example.ui.theme.ArcadeAmber
import com.example.ui.theme.ArcadeCream
import com.example.ui.theme.ArcadeCrimson
import com.example.ui.theme.ArcadeCyan
import com.example.ui.theme.ArcadeEmerald
import com.example.ui.theme.ArcadeEmeraldDark
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.ArcadePanel
import com.example.ui.theme.ArcadePanelBorderDark
import com.example.ui.theme.ArcadePanelBorderLight
import com.example.ui.theme.ArcadeVoid

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameplayScreen(
    uiState: QuizFightUiState,
    onToggleSound: () -> Unit,
    onAnswerChanged: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    val isImeOpen = WindowInsets.isImeVisible

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val totalHeight = maxHeight
        // Keep ~56% top arena normally; compact smoothly to ~36% when keyboard is open
        // so both fighters AND the submit button remain clearly visible at all times.
        val arenaHeight = if (isImeOpen) {
            (totalHeight * 0.36f).coerceIn(136.dp, 220.dp)
        } else {
            (totalHeight * 0.56f).coerceAtLeast(220.dp)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // TOP WINDOW — FIGHTING GAME ARENA
            FightingArenaCanvas(
                player1Pose = uiState.player1Pose,
                player2Pose = uiState.player2Pose,
                player1OffsetX = uiState.player1OffsetX,
                player2OffsetX = uiState.player2OffsetX,
                player1OffsetY = uiState.player1OffsetY,
                player2OffsetY = uiState.player2OffsetY,
                impactFlashStrength = uiState.impactFlashStrength,
                impactTargetPlayer = uiState.impactTargetPlayer,
                isMissEffectVisible = uiState.isMissEffectVisible,
                isComboAuraActive = uiState.isComboAuraActive,
                screenShakeTrigger = uiState.screenShakeTrigger,
                feedbackBanner = uiState.feedbackBanner,
                isSoundEnabled = uiState.isSoundEnabled,
                onToggleSound = onToggleSound,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(arenaHeight)
            )

            // Distinct arcade console divider bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(ArcadeGold)
            )

            // BOTTOM WINDOW — QUIZ INTERFACE
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(ArcadePanel)
                    .border(2.dp, ArcadePanelBorderDark)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Score & Progress HUD Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUESTION ${uiState.currentQuestionIndex + 1} / ${uiState.totalQuestions}",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                        color = ArcadeCyan,
                        modifier = Modifier.testTag("gameplay_question_counter")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "CORRECT: ${uiState.correctCount}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = ArcadeEmerald,
                            modifier = Modifier.testTag("gameplay_correct_count")
                        )
                        Text(
                            text = "WRONG: ${uiState.wrongCount}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = ArcadeCrimson,
                            modifier = Modifier.testTag("gameplay_wrong_count")
                        )
                    }
                }

                // Current Question Box (internally scrollable only if an exceptionally long question is entered)
                Box(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(ArcadeVoid.copy(alpha = 0.72f))
                        .border(2.dp, ArcadePanelBorderLight)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = uiState.currentQuizQuestion?.questionText.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 19.sp,
                            lineHeight = 25.sp
                        ),
                        color = ArcadeCream,
                        modifier = Modifier.testTag("gameplay_question_text")
                    )
                }

                // Input + Primary Action Area (always visible above keyboard)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ArcadeTextField(
                        value = uiState.studentAnswerInput,
                        onValueChange = onAnswerChanged,
                        placeholder = stringResource(R.string.hint_gameplay_answer),
                        enabled = !uiState.isCurrentQuestionSubmitted && !uiState.isAnimationPlaying,
                        singleLine = true,
                        imeAction = ImeAction.Send,
                        onImeAction = {
                            if (uiState.canSubmitAnswer) {
                                onSubmitAnswer()
                            }
                        },
                        modifier = Modifier.testTag("gameplay_answer_input")
                    )

                    if (uiState.isNextQuestionReady) {
                        ArcadeButton(
                            text = stringResource(R.string.btn_next_question),
                            onClick = onNextQuestion,
                            containerColor = ArcadeEmerald,
                            shadowColor = ArcadeEmeraldDark,
                            contentColor = ArcadeVoid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("next_question_button"),
                            minHeight = 52.dp
                        )
                    } else {
                        val buttonText = if (uiState.isAnimationPlaying) {
                            "FIGHT IN PROGRESS..."
                        } else {
                            stringResource(R.string.btn_submit_answer)
                        }
                        ArcadeButton(
                            text = buttonText,
                            onClick = onSubmitAnswer,
                            enabled = uiState.canSubmitAnswer,
                            containerColor = if (uiState.isAnimationPlaying) ArcadeAmber else ArcadeGold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_answer_button"),
                            minHeight = 52.dp
                        )
                    }
                }
            }
        }
    }
}
