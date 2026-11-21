package com.example.model

data class QuizQuestion(
    val id: Int,
    val questionText: String = "",
    val correctAnswer: String = ""
)

enum class GameScreen {
    WELCOME,
    EVALUATOR_HANDOFF,
    QUESTION_COUNT,
    QUESTION_ENTRY,
    REVIEW_QUESTIONS,
    STUDENT_HANDOFF,
    GAMEPLAY,
    RESULT
}

enum class FighterPose {
    IDLE,
    ATTACK_WINDUP,
    ATTACK_STRIKE,
    ATTACK_MISS,
    DODGE,
    HIT_REACTION,
    FAILED_RECOVERY,
    COMBO_POSE,
    VICTORY,
    DEFEAT
}

enum class ArenaAnimationSequence {
    NONE,
    CORRECT_NORMAL,
    CORRECT_COMBO_6,
    WRONG_A_MISS_COUNTER,
    WRONG_B_DIRECT_ATTACK,
    FINAL_VICTORY,
    FINAL_DEFEAT
}

enum class FeedbackBannerType {
    NONE,
    CORRECT,
    WRONG,
    COMBO_6_STREAK
}

data class QuizFightUiState(
    val currentScreen: GameScreen = GameScreen.WELCOME,
    val isSoundEnabled: Boolean = true,

    // Evaluator setup
    val selectedQuestionCount: Int = 5,
    val draftQuestions: List<QuizQuestion> = List(5) { index -> QuizQuestion(id = index + 1) },
    val currentEntryIndex: Int = 0,
    val setupErrorMessage: String? = null,

    // Active gameplay
    val currentQuestionIndex: Int = 0,
    val studentAnswerInput: String = "",
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val currentStreak: Int = 0,
    val highestStreak: Int = 0,
    val isCurrentQuestionSubmitted: Boolean = false,
    val isAnimationPlaying: Boolean = false,
    val isNextQuestionReady: Boolean = false,
    val feedbackBanner: FeedbackBannerType = FeedbackBannerType.NONE,
    val isQuizComplete: Boolean = false,

    // Fighter visual choreography
    val player1Pose: FighterPose = FighterPose.IDLE,
    val player2Pose: FighterPose = FighterPose.IDLE,
    val player1OffsetX: Float = 0f, // Normalized -1f..1f across arena
    val player2OffsetX: Float = 0f,
    val player1OffsetY: Float = 0f,
    val player2OffsetY: Float = 0f,
    val activeSequence: ArenaAnimationSequence = ArenaAnimationSequence.NONE,
    val impactFlashStrength: Float = 0f,
    val impactTargetPlayer: Int = 0, // 0 = none, 1 = P1, 2 = P2
    val isMissEffectVisible: Boolean = false,
    val isComboAuraActive: Boolean = false,
    val screenShakeTrigger: Int = 0,

    // Result summary
    val didStudentWin: Boolean = false,
    val requiredCorrectToWin: Int = 3
) {
    val totalQuestions: Int
        get() = draftQuestions.size

    val currentQuizQuestion: QuizQuestion?
        get() = draftQuestions.getOrNull(currentQuestionIndex)

    val currentDraftQuestion: QuizQuestion
        get() = draftQuestions.getOrElse(currentEntryIndex) { QuizQuestion(id = currentEntryIndex + 1) }

    val canSubmitAnswer: Boolean
        get() = currentScreen == GameScreen.GAMEPLAY &&
            !isCurrentQuestionSubmitted &&
            !isAnimationPlaying &&
            !isQuizComplete &&
            studentAnswerInput.trim().isNotEmpty()
}
