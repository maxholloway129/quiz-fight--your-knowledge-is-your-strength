package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ArcadeButton
import com.example.ui.components.ArcadePanel
import com.example.ui.components.PixelDivider
import com.example.ui.theme.ArcadeAmber
import com.example.ui.theme.ArcadeCream
import com.example.ui.theme.ArcadeCyan
import com.example.ui.theme.ArcadeEmerald
import com.example.ui.theme.ArcadeEmeraldDark
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.ArcadeMutedText
import com.example.ui.theme.ArcadePanel
import com.example.ui.theme.ArcadePanelBorderDark
import com.example.ui.theme.ArcadePanelBorderLight
import com.example.ui.theme.ArcadeVoid

@Composable
fun EvaluatorHandoffScreen(
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                ArcadePanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ArcadeCyan,
                    contentPadding = 22.dp
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(64.dp)
                            .background(ArcadeVoid)
                            .border(2.dp, ArcadeCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = ArcadeCyan,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.evaluator_handoff_title),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 18.sp),
                        color = ArcadeGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    PixelDivider(color = ArcadeCyan)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.evaluator_handoff_body),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, lineHeight = 28.sp),
                        color = ArcadeCream,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ArcadeButton(
                text = stringResource(R.string.btn_continue),
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("evaluator_handoff_continue_button"),
                minHeight = 56.dp
            )
        }
    }
}

@Composable
fun QuestionCountScreen(
    selectedCount: Int,
    onSelectCount: (Int) -> Unit,
    onContinue: () -> Unit
) {
    val options = listOf(5, 6, 7, 8, 9, 10)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.question_count_title),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 17.sp),
                    color = ArcadeGold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.question_count_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcadeMutedText,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                // Big retro readout of selected count
                ArcadePanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ArcadeAmber,
                    contentPadding = 16.dp
                ) {
                    Text(
                        text = stringResource(R.string.question_count_selected_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = ArcadeAmber,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedCount.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
                        color = ArcadeGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("selected_question_count_value")
                    )
                }

                // 3x2 Grid of options 5..10
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.chunked(3).forEach { rowValues ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowValues.forEach { count ->
                                val isSelected = count == selectedCount
                                val interactionSource = remember { MutableInteractionSource() }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .height(60.dp)
                                        .background(if (isSelected) ArcadeGold else ArcadePanel)
                                        .border(
                                            width = 3.dp,
                                            color = if (isSelected) ArcadeCream else ArcadePanelBorderLight
                                        )
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = ripple(color = ArcadeGold),
                                            onClick = { onSelectCount(count) }
                                        )
                                        .testTag("count_option_$count"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                                        color = if (isSelected) ArcadeVoid else ArcadeCream
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ArcadeButton(
                text = stringResource(R.string.btn_continue),
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("question_count_continue_button"),
                minHeight = 56.dp
            )
        }
    }
}

@Composable
fun StudentHandoffScreen(
    onLetsFight: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                ArcadePanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ArcadeEmerald,
                    contentPadding = 22.dp
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(64.dp)
                            .background(ArcadeVoid)
                            .border(2.dp, ArcadeEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsMma,
                            contentDescription = null,
                            tint = ArcadeEmerald,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.student_handoff_title),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 24.sp),
                        color = ArcadeEmerald,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    PixelDivider(color = ArcadeEmerald)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.student_handoff_body),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, lineHeight = 28.sp),
                        color = ArcadeCream,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.student_handoff_sub),
                        style = MaterialTheme.typography.titleMedium,
                        color = ArcadeGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ArcadeButton(
                text = stringResource(R.string.btn_lets_fight),
                onClick = onLetsFight,
                containerColor = ArcadeEmerald,
                shadowColor = ArcadeEmeraldDark,
                contentColor = ArcadeVoid,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lets_fight_button"),
                minHeight = 56.dp
            )
        }
    }
}
