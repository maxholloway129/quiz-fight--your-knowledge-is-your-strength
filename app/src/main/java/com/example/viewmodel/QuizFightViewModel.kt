package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioTrackSoundPlayer
import com.example.audio.SoundPlayer
import com.example.domain.AnswerValidator
import com.example.domain.QuizResultCalculator
import com.example.model.ArenaAnimationSequence
import com.example.model.FeedbackBannerType
import com.example.model.FighterPose
import com.example.model.GameScreen
import com.example.model.QuizFightUiState
import com.example.model.QuizQuestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class QuizFightViewModel(
    private val soundPlayer: SoundPlayer = AudioTrackSoundPlayer(initialEnabled = true),
    private val wrongAnimationSelector: () -> Boolean = { Random.nextBoolean() },
    private val animationDurationScale: Float = 1f
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizFightUiState())
    val uiState: StateFlow<QuizFightUiState> = _uiState.asStateFlow()

    private var animationJob: Job? = null

    fun toggleSound() {
        _uiState.update { current ->
            val next = !current.isSoundEnabled
            soundPlayer.setEnabled(next)
            if (next) soundPlayer.playButtonClick()
            current.copy(isSoundEnabled = next)
        }
    }

    fun onStartGamePressed() {
        soundPlayer.playConfirm()
        _uiState.update {
            it.copy(
                currentScreen = GameScreen.EVALUATOR_HANDOFF,
                setupErrorMessage = null
            )
        }
    }

    fun onEvaluatorHandoffContinue() {
        soundPlayer.playButtonClick()
        _uiState.update {
            it.copy(
                currentScreen = GameScreen.QUESTION_COUNT,
                setupErrorMessage = null
            )
        }
    }

    fun onSelectQuestionCount(count: Int) {
        val clamped = count.coerceIn(QuizResultCalculator.MIN_QUESTIONS, QuizResultCalculator.MAX_QUESTIONS)
        soundPlayer.playButtonClick()
        _uiState.update { state ->
            val updatedList = List(clamped) { index ->
                state.draftQuestions.getOrNull(index)?.copy(id = index + 1)
                    ?: QuizQuestion(id = index + 1)
            }
            state.copy(
                selectedQuestionCount = clamped,
                draftQuestions = updatedList,
                currentEntryIndex = state.currentEntryIndex.coerceAtMost(clamped - 1),
                setupErrorMessage = null
            )
        }
    }

    fun onQuestionCountContinue() {
        soundPlayer.playConfirm()
        _uiState.update { state ->
            val count = state.selectedQuestionCount.coerceIn(
                QuizResultCalculator.MIN_QUESTIONS,
                QuizResultCalculator.MAX_QUESTIONS
            )
            val normalizedList = List(count) { index ->
                state.draftQuestions.getOrNull(index)?.copy(id = index + 1)
                    ?: QuizQuestion(id = index + 1)
            }
            state.copy(
                selectedQuestionCount = count,
                draftQuestions = normalizedList,
                currentEntryIndex = 0,
                currentScreen = GameScreen.QUESTION_ENTRY,
                setupErrorMessage = null
            )
        }
    }

    fun onDraftQuestionTextChanged(text: String) {
        _uiState.update { state ->
            val idx = state.currentEntryIndex
            val updated = state.draftQuestions.toMutableList()
            val item = updated.getOrNull(idx) ?: QuizQuestion(id = idx + 1)
            if (idx in updated.indices) {
                updated[idx] = item.copy(questionText = text)
            }
            state.copy(draftQuestions = updated, setupErrorMessage = null)
        }
    }

    fun onDraftAnswerTextChanged(text: String) {
        _uiState.update { state ->
            val idx = state.currentEntryIndex
            val updated = state.draftQuestions.toMutableList()
            val item = updated.getOrNull(idx) ?: QuizQuestion(id = idx + 1)
            if (idx in updated.indices) {
                updated[idx] = item.copy(correctAnswer = text)
            }
            state.copy(draftQuestions = updated, setupErrorMessage = null)
        }
    }

    fun onQuestionEntryNext() {
        val state = _uiState.value
        val current = state.currentDraftQuestion
        if (!AnswerValidator.isSetupEntryValid(current.questionText, current.correctAnswer)) {
            _uiState.update {
                it.copy(setupErrorMessage = "Please enter both a question and an answer before continuing.")
            }
            return
        }

        val cleanedList = state.draftQuestions.toMutableList()
        cleanedList[state.currentEntryIndex] = current.copy(
            questionText = current.questionText.trim(),
            correctAnswer = current.correctAnswer.trim()
        )

        if (state.currentEntryIndex < state.selectedQuestionCount - 1) {
            soundPlayer.playButtonClick()
            _uiState.update {
                it.copy(
                    draftQuestions = cleanedList,
                    currentEntryIndex = state.currentEntryIndex + 1,
                    setupErrorMessage = null
                )
            }
        } else {
            soundPlayer.playConfirm()
            _uiState.update {
                it.copy(
                    draftQuestions = cleanedList,
                    currentScreen = GameScreen.REVIEW_QUESTIONS,
                    setupErrorMessage = null
                )
            }
        }
    }

    fun onQuestionEntryPrevious() {
        soundPlayer.playButtonClick()
        _uiState.update { state ->
            if (state.currentEntryIndex > 0) {
                state.copy(
                    currentEntryIndex = state.currentEntryIndex - 1,
                    setupErrorMessage = null
                )
            } else {
                state.copy(
                    currentScreen = GameScreen.QUESTION_COUNT,
                    setupErrorMessage = null
                )
            }
        }
    }

    fun onUpdateReviewedQuestion(index: Int, newQuestion: String, newAnswer: String): Boolean {
        if (!AnswerValidator.isSetupEntryValid(newQuestion, newAnswer)) {
            return false
        }
        soundPlayer.playConfirm()
        _uiState.update { state ->
            val updated = state.draftQuestions.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(
                    questionText = newQuestion.trim(),
                    correctAnswer = newAnswer.trim()
                )
            }
            state.copy(draftQuestions = updated)
        }
        return true
    }

    fun onReviewBackToEntry(questionIndex: Int = _uiState.value.selectedQuestionCount - 1) {
        soundPlayer.playButtonClick()
        _uiState.update { state ->
            state.copy(
                currentScreen = GameScreen.QUESTION_ENTRY,
                currentEntryIndex = questionIndex.coerceIn(0, state.selectedQuestionCount - 1),
                setupErrorMessage = null
            )
        }
    }

    fun onStartQuizConfirmed() {
        val state = _uiState.value
        val allValid = state.draftQuestions.size >= QuizResultCalculator.MIN_QUESTIONS &&
            state.draftQuestions.all { AnswerValidator.isSetupEntryValid(it.questionText, it.correctAnswer) }
        if (!allValid) return

        soundPlayer.playConfirm()
        _uiState.update {
            it.copy(
                currentScreen = GameScreen.STUDENT_HANDOFF,
                setupErrorMessage = null
            )
        }
    }

    fun onStudentLetsFight() {
        soundPlayer.playConfirm()
        startFreshBattleSession(targetScreen = GameScreen.GAMEPLAY)
    }

    fun onStudentAnswerChanged(answer: String) {
        _uiState.update { state ->
            if (state.isCurrentQuestionSubmitted || state.isAnimationPlaying) {
                state
            } else {
                state.copy(studentAnswerInput = answer)
            }
        }
    }

    fun onSubmitStudentAnswer() {
        val state = _uiState.value
        if (!state.canSubmitAnswer) return
        val currentQuestion = state.currentQuizQuestion ?: return

        val isCorrect = AnswerValidator.isAnswerCorrect(
            studentAnswer = state.studentAnswerInput,
            expectedAnswer = currentQuestion.correctAnswer
        )

        if (isCorrect) {
            val newCorrect = state.correctCount + 1
            val newStreak = state.currentStreak + 1
            val newHighest = maxOf(state.highestStreak, newStreak)
            val isSixStreak = newStreak >= QuizResultCalculator.STREAK_COMBO_THRESHOLD

            _uiState.update {
                it.copy(
                    isCurrentQuestionSubmitted = true,
                    isAnimationPlaying = true,
                    isNextQuestionReady = false,
                    correctCount = newCorrect,
                    currentStreak = newStreak,
                    highestStreak = newHighest,
                    feedbackBanner = if (isSixStreak) {
                        FeedbackBannerType.COMBO_6_STREAK
                    } else {
                        FeedbackBannerType.CORRECT
                    },
                    isComboAuraActive = isSixStreak,
                    activeSequence = if (isSixStreak) {
                        ArenaAnimationSequence.CORRECT_COMBO_6
                    } else {
                        ArenaAnimationSequence.CORRECT_NORMAL
                    }
                )
            }
            launchCombatChoreography(
                if (isSixStreak) ArenaAnimationSequence.CORRECT_COMBO_6
                else ArenaAnimationSequence.CORRECT_NORMAL
            )
        } else {
            val newWrong = state.wrongCount + 1
            val useAnimationA = wrongAnimationSelector()
            val sequence = if (useAnimationA) {
                ArenaAnimationSequence.WRONG_A_MISS_COUNTER
            } else {
                ArenaAnimationSequence.WRONG_B_DIRECT_ATTACK
            }

            _uiState.update {
                it.copy(
                    isCurrentQuestionSubmitted = true,
                    isAnimationPlaying = true,
                    isNextQuestionReady = false,
                    wrongCount = newWrong,
                    currentStreak = 0,
                    isComboAuraActive = false,
                    feedbackBanner = FeedbackBannerType.WRONG,
                    activeSequence = sequence
                )
            }
            launchCombatChoreography(sequence)
        }
    }

    fun onNextQuestionPressed() {
        val state = _uiState.value
        if (state.isAnimationPlaying || !state.isNextQuestionReady || !state.isCurrentQuestionSubmitted) {
            return
        }

        soundPlayer.playButtonClick()

        val isLastQuestion = state.currentQuestionIndex >= state.totalQuestions - 1
        if (!isLastQuestion) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = state.currentQuestionIndex + 1,
                    studentAnswerInput = "",
                    isCurrentQuestionSubmitted = false,
                    isNextQuestionReady = false,
                    feedbackBanner = FeedbackBannerType.NONE,
                    player1Pose = FighterPose.IDLE,
                    player2Pose = FighterPose.IDLE,
                    player1OffsetX = 0f,
                    player2OffsetX = 0f,
                    player1OffsetY = 0f,
                    player2OffsetY = 0f,
                    activeSequence = ArenaAnimationSequence.NONE,
                    impactFlashStrength = 0f,
                    impactTargetPlayer = 0,
                    isMissEffectVisible = false
                )
            }
        } else {
            val result = QuizResultCalculator.evaluate(
                totalQuestions = state.totalQuestions,
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                highestStreak = state.highestStreak
            )
            if (result.didWin) {
                soundPlayer.playVictory()
            } else {
                soundPlayer.playDefeat()
            }
            _uiState.update {
                it.copy(
                    currentScreen = GameScreen.RESULT,
                    isQuizComplete = true,
                    didStudentWin = result.didWin,
                    requiredCorrectToWin = result.requiredCorrectCount,
                    feedbackBanner = FeedbackBannerType.NONE,
                    isMissEffectVisible = false,
                    impactFlashStrength = 0f,
                    impactTargetPlayer = 0,
                    isComboAuraActive = result.didWin && it.currentStreak >= QuizResultCalculator.STREAK_COMBO_THRESHOLD,
                    player1Pose = if (result.didWin) FighterPose.VICTORY else FighterPose.DEFEAT,
                    player2Pose = if (result.didWin) FighterPose.DEFEAT else FighterPose.VICTORY,
                    player1OffsetX = 0f,
                    player2OffsetX = 0f,
                    player1OffsetY = 0f,
                    player2OffsetY = 0f,
                    activeSequence = if (result.didWin) {
                        ArenaAnimationSequence.FINAL_VICTORY
                    } else {
                        ArenaAnimationSequence.FINAL_DEFEAT
                    }
                )
            }
        }
    }

    fun onPlayAgain() {
        soundPlayer.playConfirm()
        startFreshBattleSession(targetScreen = GameScreen.GAMEPLAY)
    }

    fun onCreateNewQuizConfirmed() {
        animationJob?.cancel()
        soundPlayer.playButtonClick()
        _uiState.update { state ->
            QuizFightUiState(
                currentScreen = GameScreen.QUESTION_COUNT,
                isSoundEnabled = state.isSoundEnabled,
                selectedQuestionCount = QuizResultCalculator.MIN_QUESTIONS,
                draftQuestions = List(QuizResultCalculator.MIN_QUESTIONS) { idx -> QuizQuestion(id = idx + 1) }
            )
        }
    }

    fun navigateBackInSetup() {
        val state = _uiState.value
        when (state.currentScreen) {
            GameScreen.EVALUATOR_HANDOFF -> {
                soundPlayer.playButtonClick()
                _uiState.update { it.copy(currentScreen = GameScreen.WELCOME) }
            }
            GameScreen.QUESTION_COUNT -> {
                soundPlayer.playButtonClick()
                _uiState.update { it.copy(currentScreen = GameScreen.EVALUATOR_HANDOFF) }
            }
            GameScreen.QUESTION_ENTRY -> onQuestionEntryPrevious()
            GameScreen.REVIEW_QUESTIONS -> onReviewBackToEntry()
            GameScreen.STUDENT_HANDOFF -> {
                soundPlayer.playButtonClick()
                _uiState.update { it.copy(currentScreen = GameScreen.REVIEW_QUESTIONS) }
            }
            else -> Unit
        }
    }

    private fun startFreshBattleSession(targetScreen: GameScreen) {
        animationJob?.cancel()
        _uiState.update { state ->
            state.copy(
                currentScreen = targetScreen,
                currentQuestionIndex = 0,
                studentAnswerInput = "",
                correctCount = 0,
                wrongCount = 0,
                currentStreak = 0,
                highestStreak = 0,
                isCurrentQuestionSubmitted = false,
                isAnimationPlaying = false,
                isNextQuestionReady = false,
                feedbackBanner = FeedbackBannerType.NONE,
                isQuizComplete = false,
                player1Pose = FighterPose.IDLE,
                player2Pose = FighterPose.IDLE,
                player1OffsetX = 0f,
                player2OffsetX = 0f,
                player1OffsetY = 0f,
                player2OffsetY = 0f,
                activeSequence = ArenaAnimationSequence.NONE,
                impactFlashStrength = 0f,
                impactTargetPlayer = 0,
                isMissEffectVisible = false,
                isComboAuraActive = false,
                didStudentWin = false,
                requiredCorrectToWin = QuizResultCalculator.requiredCorrectAnswers(state.totalQuestions)
            )
        }
    }

    private fun launchCombatChoreography(sequence: ArenaAnimationSequence) {
        animationJob?.cancel()
        animationJob = viewModelScope.launch {
            suspend fun waitMs(ms: Long) {
                val scaled = (ms * animationDurationScale).toLong()
                if (scaled > 0L) delay(scaled)
            }

            when (sequence) {
                ArenaAnimationSequence.CORRECT_NORMAL -> {
                    soundPlayer.playAttackWhoosh()
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.ATTACK_WINDUP,
                            player1OffsetX = -0.04f
                        )
                    }
                    waitMs(160L)

                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.ATTACK_STRIKE,
                            player1OffsetX = 0.56f
                        )
                    }
                    waitMs(220L)

                    soundPlayer.playHitImpact()
                    _uiState.update {
                        it.copy(
                            player2Pose = FighterPose.HIT_REACTION,
                            player2OffsetX = 0.10f,
                            impactTargetPlayer = 2,
                            impactFlashStrength = 1.0f,
                            screenShakeTrigger = it.screenShakeTrigger + 1
                        )
                    }
                    waitMs(320L)

                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.IDLE,
                            player2Pose = FighterPose.IDLE,
                            player1OffsetX = 0f,
                            player2OffsetX = 0f,
                            impactTargetPlayer = 0,
                            impactFlashStrength = 0f,
                            isAnimationPlaying = false,
                            isNextQuestionReady = true
                        )
                    }
                }

                ArenaAnimationSequence.CORRECT_COMBO_6 -> {
                    soundPlayer.playComboStreak()
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.COMBO_POSE,
                            player1OffsetX = 0.04f,
                            player1OffsetY = -0.04f,
                            isComboAuraActive = true
                        )
                    }
                    waitMs(240L)

                    soundPlayer.playAttackWhoosh()
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.ATTACK_STRIKE,
                            player1OffsetX = 0.58f,
                            player1OffsetY = 0f
                        )
                    }
                    waitMs(240L)

                    soundPlayer.playHitImpact()
                    _uiState.update {
                        it.copy(
                            player2Pose = FighterPose.DEFEAT,
                            player2OffsetX = 0.12f,
                            impactTargetPlayer = 2,
                            impactFlashStrength = 1.0f,
                            screenShakeTrigger = it.screenShakeTrigger + 1
                        )
                    }
                    waitMs(260L)

                    // Player 1 leaps into dominant victory-combo pose right beside/above defeated P2
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.COMBO_POSE,
                            player1OffsetX = 0.44f,
                            player1OffsetY = -0.11f,
                            impactTargetPlayer = 0,
                            impactFlashStrength = 0f
                        )
                    }
                    waitMs(420L)

                    _uiState.update {
                        it.copy(
                            isAnimationPlaying = false,
                            isNextQuestionReady = true
                        )
                    }
                }

                ArenaAnimationSequence.WRONG_A_MISS_COUNTER -> {
                    // Player 1 attempts to attack -> Player 2 dodges -> Player 1 misses -> Player 2 counterattacks
                    soundPlayer.playAttackWhoosh()
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.ATTACK_STRIKE,
                            player1OffsetX = 0.44f,
                            player2Pose = FighterPose.DODGE,
                            player2OffsetX = 0.16f,
                            player2OffsetY = 0.03f,
                            isMissEffectVisible = true
                        )
                    }
                    soundPlayer.playMissDodge()
                    waitMs(320L)

                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.ATTACK_MISS,
                            player1OffsetX = 0.50f,
                            player2Pose = FighterPose.ATTACK_WINDUP,
                            isMissEffectVisible = false
                        )
                    }
                    waitMs(280L)

                    soundPlayer.playAttackWhoosh()
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.FAILED_RECOVERY,
                            player1OffsetX = 0.16f,
                            player2Pose = FighterPose.ATTACK_STRIKE,
                            player2OffsetX = -0.36f,
                            player2OffsetY = 0f
                        )
                    }
                    waitMs(240L)

                    soundPlayer.playHitImpact()
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.HIT_REACTION,
                            player1OffsetX = -0.10f,
                            impactTargetPlayer = 1,
                            impactFlashStrength = 1.0f,
                            screenShakeTrigger = it.screenShakeTrigger + 1
                        )
                    }
                    waitMs(320L)

                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.IDLE,
                            player2Pose = FighterPose.IDLE,
                            player1OffsetX = 0f,
                            player2OffsetX = 0f,
                            impactTargetPlayer = 0,
                            impactFlashStrength = 0f,
                            isAnimationPlaying = false,
                            isNextQuestionReady = true
                        )
                    }
                }

                ArenaAnimationSequence.WRONG_B_DIRECT_ATTACK -> {
                    // Player 1 in failed/recovery state -> Player 2 charges across and hits Player 1
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.FAILED_RECOVERY,
                            player1OffsetX = -0.02f,
                            player2Pose = FighterPose.ATTACK_WINDUP,
                            player2OffsetX = 0.04f
                        )
                    }
                    waitMs(240L)

                    soundPlayer.playAttackWhoosh()
                    _uiState.update {
                        it.copy(
                            player2Pose = FighterPose.ATTACK_STRIKE,
                            player2OffsetX = -0.52f
                        )
                    }
                    waitMs(240L)

                    soundPlayer.playHitImpact()
                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.HIT_REACTION,
                            player1OffsetX = -0.12f,
                            impactTargetPlayer = 1,
                            impactFlashStrength = 1.0f,
                            screenShakeTrigger = it.screenShakeTrigger + 1
                        )
                    }
                    waitMs(340L)

                    _uiState.update {
                        it.copy(
                            player1Pose = FighterPose.IDLE,
                            player2Pose = FighterPose.IDLE,
                            player1OffsetX = 0f,
                            player2OffsetX = 0f,
                            impactTargetPlayer = 0,
                            impactFlashStrength = 0f,
                            isAnimationPlaying = false,
                            isNextQuestionReady = true
                        )
                    }
                }

                else -> {
                    _uiState.update {
                        it.copy(
                            isAnimationPlaying = false,
                            isNextQuestionReady = true
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        animationJob?.cancel()
    }
}
