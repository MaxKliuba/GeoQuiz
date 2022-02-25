package com.maxclub.android.geoquiz

import androidx.annotation.StringRes
import java.io.Serializable

data class Question(
    @StringRes val testResId: Int,
    val answer: Boolean,
    var state: QuestionState = QuestionState.NO_ANSWER,
    var isCheat: Boolean = false,
) : Serializable {
    fun checkAnswer(userAnswer: Boolean) =
        if (userAnswer == answer) {
            state = QuestionState.CORRECT_ANSWER
            true
        } else {
            state = QuestionState.INCORRECT_ANSWER
            false
        }
}

enum class QuestionState {
    NO_ANSWER,
    CORRECT_ANSWER,
    INCORRECT_ANSWER,
}