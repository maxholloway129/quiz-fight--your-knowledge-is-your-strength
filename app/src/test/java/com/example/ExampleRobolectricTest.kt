package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import com.example.audio.NoOpSoundPlayer
import com.example.ui.theme.QuizFightTheme
import com.example.viewmodel.QuizFightViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `app name matches Quiz Fight`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Quiz Fight", appName)
    }

    @Test
    fun `complete 5-question evaluator setup, gameplay win, and play again flow`() {
        val viewModel = QuizFightViewModel(
            soundPlayer = NoOpSoundPlayer(),
            wrongAnimationSelector = { true },
            animationDurationScale = 0f
        )

        composeTestRule.setContent {
            QuizFightTheme {
                QuizFightApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Screen 1: Welcome + sound toggle
        composeTestRule.onNodeWithTag("welcome_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("welcome_sound_toggle").performClick()
        composeTestRule.waitForIdle()
        assertEquals(false, viewModel.uiState.value.isSoundEnabled)
        composeTestRule.onNodeWithTag("welcome_sound_toggle").performClick()
        composeTestRule.waitForIdle()
        assertEquals(true, viewModel.uiState.value.isSoundEnabled)

        composeTestRule.onNodeWithTag("start_game_button").performClick()
        composeTestRule.waitForIdle()
        assertEquals(com.example.model.GameScreen.EVALUATOR_HANDOFF, viewModel.uiState.value.currentScreen)
        composeTestRule.mainClock.advanceTimeBy(300)
        composeTestRule.waitForIdle()

        // Screen 2: Evaluator handoff
        composeTestRule.onNodeWithTag("evaluator_handoff_continue_button").performClick()
        composeTestRule.waitForIdle()

        // Screen 3: Question count defaults to 5
        composeTestRule.onNodeWithTag("selected_question_count_value").assertTextContains("5")
        composeTestRule.onNodeWithTag("question_count_continue_button").performClick()
        composeTestRule.waitForIdle()

        // Screen 4: Verify empty submission is rejected
        composeTestRule.onNodeWithTag("question_entry_next_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("setup_error_message").assertIsDisplayed()

        // Enter all 5 questions
        val qa = listOf(
            "Capital of France?" to "Paris",
            "2 + 2?" to "4",
            "Red planet?" to "Mars",
            "Language of Android?" to "Kotlin",
            "Largest ocean?" to "Pacific"
        )
        qa.forEachIndexed { idx, (q, a) ->
            composeTestRule.onNodeWithTag("question_entry_progress")
                .assertTextContains("QUESTION ${idx + 1} OF 5")
            composeTestRule.onNodeWithTag("question_input").performTextReplacement(q)
            composeTestRule.onNodeWithTag("answer_input").performTextReplacement(a)
            composeTestRule.onNodeWithTag("question_entry_next_button").performClick()
            composeTestRule.waitForIdle()
        }

        // Screen 5: Review screen + edit Q2
        composeTestRule.onNodeWithTag("review_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("edit_question_1").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("edit_dialog_answer_input").performTextReplacement("four")
        composeTestRule.onNodeWithTag("edit_dialog_save_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("start_quiz_button").performClick()
        composeTestRule.waitForIdle()

        // Screen 6: Student handoff
        composeTestRule.onNodeWithTag("lets_fight_button").performClick()
        composeTestRule.waitForIdle()

        // Screen 7: Gameplay — 3 correct (with case/whitespace), 2 wrong (misspelling)
        composeTestRule.onNodeWithTag("player_1_label").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_2_label").assertIsDisplayed()
        composeTestRule.onNodeWithTag("submit_answer_button").assertIsNotEnabled()

        val studentInputs = listOf(
            "  paris  ", // correct (trimmed + lowercase)
            "FOUR",      // correct (uppercase)
            "Mras",      // wrong (misspelled)
            "kotln",     // wrong (misspelled)
            "Pacific"    // correct -> total 3/5 = WIN
        )

        studentInputs.forEach { input ->
            composeTestRule.onNodeWithTag("gameplay_answer_input").performTextInput(input)
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("submit_answer_button").assertIsEnabled()
            composeTestRule.onNodeWithTag("submit_answer_button").performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("next_question_button").performClick()
            composeTestRule.waitForIdle()
        }

        // Screen 8: Victory result
        composeTestRule.onNodeWithTag("result_title").assertTextContains("YOU WIN!")
        composeTestRule.onNodeWithTag("result_score_text").assertTextContains("3 / 5 CORRECT")

        // Play again preserves questions & resets scores
        composeTestRule.onNodeWithTag("play_again_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("gameplay_question_counter").assertTextContains("QUESTION 1 / 5")
        composeTestRule.onNodeWithTag("gameplay_correct_count").assertTextContains("CORRECT: 0")
    }

    @Test
    fun `create new quiz resets setup after confirmation`() {
        val viewModel = QuizFightViewModel(
            soundPlayer = NoOpSoundPlayer(),
            animationDurationScale = 0f
        )

        // Drive viewModel quickly to RESULT screen
        viewModel.onStartGamePressed()
        viewModel.onEvaluatorHandoffContinue()
        viewModel.onQuestionCountContinue()
        for (i in 1..5) {
            viewModel.onDraftQuestionTextChanged("Q$i")
            viewModel.onDraftAnswerTextChanged("A$i")
            viewModel.onQuestionEntryNext()
        }
        viewModel.onStartQuizConfirmed()
        viewModel.onStudentLetsFight()
        for (i in 1..5) {
            viewModel.onStudentAnswerChanged("wrong")
            viewModel.onSubmitStudentAnswer()
            viewModel.onNextQuestionPressed()
        }

        composeTestRule.setContent {
            QuizFightTheme {
                QuizFightApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("result_title").assertTextContains("GAME OVER!")
        composeTestRule.onNodeWithTag("create_new_quiz_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("confirm_new_quiz_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("selected_question_count_value").assertTextContains("5")
        composeTestRule.onNodeWithText("SELECT QUESTION COUNT").assertIsDisplayed()
    }
}
