package com.example.domain

object AnswerValidator {

    /**
     * Validates the student's answer against the evaluator's stored answer.
     * Rules:
     * - Trims leading and trailing whitespace on both strings
     * - Case-insensitive comparison
     * - Exact spelling required (no fuzzy/partial matching, no autocorrect)
     * - Empty/whitespace-only answers are never valid
     */
    fun isAnswerCorrect(studentAnswer: String, expectedAnswer: String): Boolean {
        val normalizedStudent = studentAnswer.trim()
        val normalizedExpected = expectedAnswer.trim()
        if (normalizedStudent.isEmpty() || normalizedExpected.isEmpty()) {
            return false
        }
        return normalizedStudent.equals(normalizedExpected, ignoreCase = true)
    }

    fun isSetupEntryValid(questionText: String, answerText: String): Boolean {
        return questionText.trim().isNotEmpty() && answerText.trim().isNotEmpty()
    }
}
