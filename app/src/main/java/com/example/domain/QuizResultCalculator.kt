package com.example.domain

import kotlin.math.ceil

data class QuizFinalResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val highestStreak: Int,
    val requiredCorrectCount: Int,
    val didWin: Boolean
)

object QuizResultCalculator {
    const val MIN_QUESTIONS = 5
    const val MAX_QUESTIONS = 10
    const val WIN_RATIO = 0.60
    const val STREAK_COMBO_THRESHOLD = 6

    fun requiredCorrectAnswers(totalQuestions: Int): Int {
        return ceil(totalQuestions * WIN_RATIO).toInt()
    }

    fun evaluate(
        totalQuestions: Int,
        correctCount: Int,
        wrongCount: Int,
        highestStreak: Int
    ): QuizFinalResult {
        val required = requiredCorrectAnswers(totalQuestions)
        return QuizFinalResult(
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            wrongCount = wrongCount,
            highestStreak = highestStreak,
            requiredCorrectCount = required,
            didWin = correctCount >= required
        )
    }
}
