package com.example

import com.example.audio.NoOpSoundPlayer
import com.example.domain.AnswerValidator
import com.example.domain.QuizResultCalculator
import com.example.model.ArenaAnimationSequence
import com.example.model.FeedbackBannerType
import com.example.model.FighterPose
import com.example.model.GameScreen
import com.example.viewmodel.QuizFightViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizFightDomainAndViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `answer validator enforces exact trimmed case-insensitive matching`() {
        assertTrue(AnswerValidator.isAnswerCorrect("python", "Python"))
        assertTrue(AnswerValidator.isAnswerCorrect("PYTHON", "Python"))
        assertTrue(AnswerValidator.isAnswerCorrect("  Hyderabad  ", "Hyderabad"))

        assertFalse(AnswerValidator.isAnswerCorrect("Hyderbad", "Hyderabad"))
        assertFalse(AnswerValidator.isAnswerCorrect("Hyder", "Hyderabad"))
        assertFalse(AnswerValidator.isAnswerCorrect("Hyderabad City", "Hyderabad"))
        assertFalse(AnswerValidator.isAnswerCorrect("   ", "Hyderabad"))
        assertFalse(AnswerValidator.isAnswerCorrect("", ""))
    }

    @Test
    fun `result calculator enforces 60 percent ceiling win thresholds`() {
        assertEquals(3, QuizResultCalculator.requiredCorrectAnswers(5))
        assertEquals(4, QuizResultCalculator.requiredCorrectAnswers(6))
        assertEquals(5, QuizResultCalculator.requiredCorrectAnswers(7))
        assertEquals(5, QuizResultCalculator.requiredCorrectAnswers(8))
        assertEquals(6, QuizResultCalculator.requiredCorrectAnswers(9))
        assertEquals(6, QuizResultCalculator.requiredCorrectAnswers(10))

        assertTrue(QuizResultCalculator.evaluate(8, 5, 3, 2).didWin)
        assertFalse(QuizResultCalculator.evaluate(8, 4, 4, 2).didWin)
        assertTrue(QuizResultCalculator.evaluate(5, 3, 2, 3).didWin)
        assertFalse(QuizResultCalculator.evaluate(5, 2, 3, 1).didWin)
    }

    @Test
    fun `question count is clamped between 5 and 10 and preserves edits when navigating back`() {
        val vm = QuizFightViewModel(soundPlayer = NoOpSoundPlayer(), animationDurationScale = 0f)
        vm.onStartGamePressed()
        vm.onEvaluatorHandoffContinue()

        vm.onSelectQuestionCount(2)
        assertEquals(5, vm.uiState.value.selectedQuestionCount)
        vm.onSelectQuestionCount(14)
        assertEquals(10, vm.uiState.value.selectedQuestionCount)

        vm.onQuestionCountContinue()
        vm.onDraftQuestionTextChanged("Q1 text")
        vm.onDraftAnswerTextChanged("A1")
        vm.onQuestionEntryNext()
        assertEquals(1, vm.uiState.value.currentEntryIndex)

        vm.onDraftQuestionTextChanged("Q2 text")
        vm.onDraftAnswerTextChanged("A2")
        vm.onQuestionEntryPrevious()
        assertEquals(0, vm.uiState.value.currentEntryIndex)
        assertEquals("Q1 text", vm.uiState.value.currentDraftQuestion.questionText)

        // Empty submission blocked
        vm.onDraftQuestionTextChanged("   ")
        vm.onQuestionEntryNext()
        assertEquals(0, vm.uiState.value.currentEntryIndex)
        assertNotNull(vm.uiState.value.setupErrorMessage)
    }

    @Test
    fun `10-question quiz activates 6-hit combo without ending early and resets streak on mistake`() {
        var toggleWrong = true
        val vm = QuizFightViewModel(
            soundPlayer = NoOpSoundPlayer(),
            wrongAnimationSelector = {
                toggleWrong = !toggleWrong
                !toggleWrong
            },
            animationDurationScale = 0f
        )

        vm.onStartGamePressed()
        vm.onEvaluatorHandoffContinue()
        vm.onSelectQuestionCount(10)
        vm.onQuestionCountContinue()

        for (i in 1..10) {
            vm.onDraftQuestionTextChanged("Question $i")
            vm.onDraftAnswerTextChanged("Ans$i")
            vm.onQuestionEntryNext()
        }
        assertEquals(GameScreen.REVIEW_QUESTIONS, vm.uiState.value.currentScreen)
        vm.onStartQuizConfirmed()
        assertEquals(GameScreen.STUDENT_HANDOFF, vm.uiState.value.currentScreen)
        vm.onStudentLetsFight()
        assertEquals(GameScreen.GAMEPLAY, vm.uiState.value.currentScreen)

        // Answer 1..5 correctly -> normal attacks
        for (i in 1..5) {
            vm.onStudentAnswerChanged("ans$i")
            vm.onSubmitStudentAnswer()
            assertEquals(ArenaAnimationSequence.CORRECT_NORMAL, vm.uiState.value.activeSequence)
            assertEquals(FeedbackBannerType.CORRECT, vm.uiState.value.feedbackBanner)
            // Double submit is ignored
            vm.onSubmitStudentAnswer()
            assertEquals(i, vm.uiState.value.correctCount)
            vm.onNextQuestionPressed()
            assertEquals(GameScreen.GAMEPLAY, vm.uiState.value.currentScreen)
        }

        // Answer 6 correctly -> triggers 6-hit streak combo mode, does NOT end quiz early!
        vm.onStudentAnswerChanged("ANS6")
        vm.onSubmitStudentAnswer()
        assertEquals(6, vm.uiState.value.currentStreak)
        assertEquals(ArenaAnimationSequence.CORRECT_COMBO_6, vm.uiState.value.activeSequence)
        assertEquals(FeedbackBannerType.COMBO_6_STREAK, vm.uiState.value.feedbackBanner)
        assertEquals(FighterPose.COMBO_POSE, vm.uiState.value.player1Pose)
        assertEquals(FighterPose.DEFEAT, vm.uiState.value.player2Pose)
        assertEquals(GameScreen.GAMEPLAY, vm.uiState.value.currentScreen)
        vm.onNextQuestionPressed()

        // Answer 7 correctly -> continues combo mode
        vm.onStudentAnswerChanged("Ans7")
        vm.onSubmitStudentAnswer()
        assertEquals(7, vm.uiState.value.currentStreak)
        assertEquals(ArenaAnimationSequence.CORRECT_COMBO_6, vm.uiState.value.activeSequence)
        vm.onNextQuestionPressed()

        // Answer 8 & 9 wrong -> resets streak, preserves highestStreak=7, uses both wrong anims
        vm.onStudentAnswerChanged("wrong8")
        vm.onSubmitStudentAnswer()
        assertEquals(0, vm.uiState.value.currentStreak)
        assertEquals(7, vm.uiState.value.highestStreak)
        assertEquals(ArenaAnimationSequence.WRONG_A_MISS_COUNTER, vm.uiState.value.activeSequence)
        vm.onNextQuestionPressed()

        vm.onStudentAnswerChanged("wrong9")
        vm.onSubmitStudentAnswer()
        assertEquals(ArenaAnimationSequence.WRONG_B_DIRECT_ATTACK, vm.uiState.value.activeSequence)
        vm.onNextQuestionPressed()

        // Answer 10 correctly -> returns to normal attack since streak restarted at 1
        vm.onStudentAnswerChanged("Ans10")
        vm.onSubmitStudentAnswer()
        assertEquals(1, vm.uiState.value.currentStreak)
        assertEquals(ArenaAnimationSequence.CORRECT_NORMAL, vm.uiState.value.activeSequence)
        vm.onNextQuestionPressed()

        // Final result: 8 / 10 correct -> WIN
        val finalState = vm.uiState.value
        assertEquals(GameScreen.RESULT, finalState.currentScreen)
        assertTrue(finalState.didStudentWin)
        assertEquals(8, finalState.correctCount)
        assertEquals(2, finalState.wrongCount)
        assertEquals(7, finalState.highestStreak)
        assertEquals(FighterPose.VICTORY, finalState.player1Pose)
        assertEquals(FighterPose.DEFEAT, finalState.player2Pose)
    }
}
