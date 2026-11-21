package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameScreen
import com.example.ui.screens.EvaluatorHandoffScreen
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.QuestionCountScreen
import com.example.ui.screens.QuestionEntryScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.ReviewQuestionsScreen
import com.example.ui.screens.StudentHandoffScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.QuizFightTheme
import com.example.viewmodel.QuizFightViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            QuizFightTheme {
                QuizFightApp()
            }
        }
    }
}

@Composable
fun QuizFightApp(
    viewModel: QuizFightViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val canNavigateBack = uiState.currentScreen in setOf(
        GameScreen.EVALUATOR_HANDOFF,
        GameScreen.QUESTION_COUNT,
        GameScreen.QUESTION_ENTRY,
        GameScreen.REVIEW_QUESTIONS,
        GameScreen.STUDENT_HANDOFF
    )

    BackHandler(enabled = canNavigateBack) {
        viewModel.navigateBackInSetup()
    }

    Crossfade(
        targetState = uiState.currentScreen,
        animationSpec = tween(durationMillis = 180),
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            GameScreen.WELCOME -> WelcomeScreen(
                isSoundEnabled = uiState.isSoundEnabled,
                onToggleSound = viewModel::toggleSound,
                onStartGame = viewModel::onStartGamePressed
            )

            GameScreen.EVALUATOR_HANDOFF -> EvaluatorHandoffScreen(
                onContinue = viewModel::onEvaluatorHandoffContinue
            )

            GameScreen.QUESTION_COUNT -> QuestionCountScreen(
                selectedCount = uiState.selectedQuestionCount,
                onSelectCount = viewModel::onSelectQuestionCount,
                onContinue = viewModel::onQuestionCountContinue
            )

            GameScreen.QUESTION_ENTRY -> QuestionEntryScreen(
                currentQuestionNumber = uiState.currentEntryIndex + 1,
                totalQuestions = uiState.selectedQuestionCount,
                questionText = uiState.currentDraftQuestion.questionText,
                answerText = uiState.currentDraftQuestion.correctAnswer,
                errorMessage = uiState.setupErrorMessage,
                onQuestionTextChange = viewModel::onDraftQuestionTextChanged,
                onAnswerTextChange = viewModel::onDraftAnswerTextChanged,
                onNext = viewModel::onQuestionEntryNext,
                onBack = viewModel::onQuestionEntryPrevious
            )

            GameScreen.REVIEW_QUESTIONS -> ReviewQuestionsScreen(
                questions = uiState.draftQuestions,
                onUpdateQuestion = viewModel::onUpdateReviewedQuestion,
                onStartQuiz = viewModel::onStartQuizConfirmed
            )

            GameScreen.STUDENT_HANDOFF -> StudentHandoffScreen(
                onLetsFight = viewModel::onStudentLetsFight
            )

            GameScreen.GAMEPLAY -> GameplayScreen(
                uiState = uiState,
                onToggleSound = viewModel::toggleSound,
                onAnswerChanged = viewModel::onStudentAnswerChanged,
                onSubmitAnswer = viewModel::onSubmitStudentAnswer,
                onNextQuestion = viewModel::onNextQuestionPressed
            )

            GameScreen.RESULT -> ResultScreen(
                uiState = uiState,
                onToggleSound = viewModel::toggleSound,
                onPlayAgain = viewModel::onPlayAgain,
                onCreateNewQuizConfirmed = viewModel::onCreateNewQuizConfirmed
            )
        }
    }
}
