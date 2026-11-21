package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.domain.AnswerValidator
import com.example.model.QuizQuestion
import com.example.ui.components.ArcadeButton
import com.example.ui.components.ArcadePanel
import com.example.ui.components.ArcadeTextField
import com.example.ui.components.PixelDivider
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
fun QuestionEntryScreen(
    currentQuestionNumber: Int,
    totalQuestions: Int,
    questionText: String,
    answerText: String,
    errorMessage: String?,
    onQuestionTextChange: (String) -> Unit,
    onAnswerTextChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .widthIn(max = 560.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.question_entry_instruction),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                color = ArcadeGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArcadePanel)
                    .border(2.dp, ArcadeCyan)
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(
                        R.string.question_entry_progress,
                        currentQuestionNumber,
                        totalQuestions
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcadeCyan,
                    modifier = Modifier.testTag("question_entry_progress")
                )
            }

            ArcadePanel(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 16.dp
            ) {
                Text(
                    text = "QUESTION",
                    style = MaterialTheme.typography.labelMedium,
                    color = ArcadeAmber
                )
                Spacer(modifier = Modifier.height(8.dp))
                ArcadeTextField(
                    value = questionText,
                    onValueChange = onQuestionTextChange,
                    placeholder = stringResource(R.string.hint_enter_question),
                    singleLine = false,
                    maxLines = 4,
                    imeAction = ImeAction.Next,
                    isError = errorMessage != null && questionText.trim().isEmpty(),
                    modifier = Modifier.testTag("question_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CORRECT ANSWER",
                    style = MaterialTheme.typography.labelMedium,
                    color = ArcadeEmerald
                )
                Spacer(modifier = Modifier.height(8.dp))
                ArcadeTextField(
                    value = answerText,
                    onValueChange = onAnswerTextChange,
                    placeholder = stringResource(R.string.hint_enter_answer),
                    singleLine = true,
                    imeAction = ImeAction.Done,
                    onImeAction = onNext,
                    isError = errorMessage != null && answerText.trim().isEmpty(),
                    modifier = Modifier.testTag("answer_input")
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArcadeCrimson.copy(alpha = 0.24f))
                    .border(2.dp, ArcadeCrimson)
                    .padding(10.dp)
                    .testTag("setup_error_message")
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcadeCream
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArcadeButton(
                text = stringResource(R.string.btn_previous),
                onClick = onBack,
                containerColor = ArcadePanel,
                shadowColor = ArcadePanelBorderLight,
                contentColor = ArcadeCream,
                modifier = Modifier
                    .weight(0.38f)
                    .testTag("question_entry_back_button")
            )

            ArcadeButton(
                text = stringResource(R.string.btn_next),
                onClick = onNext,
                modifier = Modifier
                    .weight(0.62f)
                    .testTag("question_entry_next_button")
            )
        }
    }
}

@Composable
fun ReviewQuestionsScreen(
    questions: List<QuizQuestion>,
    onUpdateQuestion: (index: Int, newQuestion: String, newAnswer: String) -> Boolean,
    onStartQuiz: () -> Unit
) {
    var editingIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .widthIn(max = 560.dp)
    ) {
        Text(
            text = stringResource(R.string.review_title),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp, lineHeight = 20.sp),
            color = ArcadeGold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("review_title")
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.review_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = ArcadeMutedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        PixelDivider(color = ArcadeAmber)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(questions) { index, item ->
                ArcadePanel(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Q${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = ArcadeCyan
                        )

                        TextButton(
                            onClick = { editingIndex = index },
                            modifier = Modifier.testTag("edit_question_$index")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.btn_edit),
                                tint = ArcadeGold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.btn_edit),
                                style = MaterialTheme.typography.labelMedium,
                                color = ArcadeGold
                            )
                        }
                    }

                    Text(
                        text = item.questionText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ArcadeCream
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ANSWER: ${item.correctAnswer}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ArcadeEmerald
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ArcadeButton(
            text = stringResource(R.string.btn_start_quiz),
            onClick = onStartQuiz,
            containerColor = ArcadeEmerald,
            shadowColor = ArcadeEmeraldDark,
            contentColor = ArcadeVoid,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("start_quiz_button"),
            minHeight = 56.dp
        )
    }

    if (editingIndex in questions.indices) {
        val target = questions[editingIndex]
        EditQuestionDialog(
            questionNumber = editingIndex + 1,
            initialQuestion = target.questionText,
            initialAnswer = target.correctAnswer,
            onDismiss = { editingIndex = -1 },
            onSave = { newQ, newA ->
                val ok = onUpdateQuestion(editingIndex, newQ, newA)
                if (ok) {
                    editingIndex = -1
                }
                ok
            }
        )
    }
}

@Composable
private fun EditQuestionDialog(
    questionNumber: Int,
    initialQuestion: String,
    initialAnswer: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Boolean
) {
    var draftQ by remember(questionNumber) { mutableStateOf(initialQuestion) }
    var draftA by remember(questionNumber) { mutableStateOf(initialAnswer) }
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        ArcadePanel(
            borderColor = ArcadeGold,
            contentPadding = 16.dp
        ) {
            Text(
                text = stringResource(R.string.dialog_edit_title, questionNumber),
                style = MaterialTheme.typography.titleMedium,
                color = ArcadeGold
            )
            Spacer(modifier = Modifier.height(12.dp))

            ArcadeTextField(
                value = draftQ,
                onValueChange = {
                    draftQ = it
                    showError = false
                },
                placeholder = stringResource(R.string.hint_enter_question),
                maxLines = 3,
                imeAction = ImeAction.Next,
                isError = showError && draftQ.trim().isEmpty(),
                modifier = Modifier.testTag("edit_dialog_question_input")
            )
            Spacer(modifier = Modifier.height(10.dp))

            ArcadeTextField(
                value = draftA,
                onValueChange = {
                    draftA = it
                    showError = false
                },
                placeholder = stringResource(R.string.hint_enter_answer),
                singleLine = true,
                imeAction = ImeAction.Done,
                isError = showError && draftA.trim().isEmpty(),
                modifier = Modifier.testTag("edit_dialog_answer_input")
            )

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.error_empty_question_or_answer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcadeCrimson
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ArcadeButton(
                    text = stringResource(R.string.btn_cancel),
                    onClick = onDismiss,
                    containerColor = ArcadePanel,
                    shadowColor = ArcadePanelBorderLight,
                    contentColor = ArcadeCream,
                    modifier = Modifier.weight(1f)
                )
                ArcadeButton(
                    text = stringResource(R.string.btn_save),
                    onClick = {
                        if (!AnswerValidator.isSetupEntryValid(draftQ, draftA) || !onSave(draftQ, draftA)) {
                            showError = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("edit_dialog_save_button")
                )
            }
        }
    }
}
