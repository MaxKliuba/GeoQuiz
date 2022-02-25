package com.maxclub.android.geoquiz

import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
private const val CHEAT_LIMIT = 3

class QuizViewModel : ViewModel() {
    var currentIndex = 0

    val questionBank = arrayListOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    val currentQuestion: Question
        get() = questionBank[currentIndex]

    val hasCurrentQuestionAnswer: Boolean
        get() = currentQuestion.state != QuestionState.NO_ANSWER

    val hasNoAnswerQuestion: Boolean
        get() = questionBank.all { it.state != QuestionState.NO_ANSWER }

    val cheatsLeft: Int
        get() = CHEAT_LIMIT - questionBank.count { it.isCheat }

    val correctAnswersPercent: Double
        get() = questionBank.count {
            it.state == QuestionState.CORRECT_ANSWER
        } * 100.0 / questionBank.size

    fun moveToPrev() {
        currentIndex = if (currentIndex > 0) --currentIndex else questionBank.lastIndex
    }

    fun moveToNext() {
        currentIndex = if (currentIndex < questionBank.lastIndex) ++currentIndex else 0
    }

    fun resetQuiz() {
        currentIndex = 0
        questionBank.forEachIndexed { index, _ ->
            questionBank[index].state = QuestionState.NO_ANSWER
            questionBank[index].isCheat = false
        }
    }
}