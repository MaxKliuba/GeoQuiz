package com.maxclub.android.geoquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_QUESTION_BANK = "questionBank"
private const val KEY_INDEX = "index"

class MainActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var resetButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    private val cheatActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                quizViewModel.currentQuestion.isCheat =
                    it.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            }
            updateCheatOption()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (savedInstanceState?.getSerializable(KEY_QUESTION_BANK) as? ArrayList<*>)?.let {
            quizViewModel.questionBank.forEachIndexed { index, _ ->
                quizViewModel.questionBank[index] = it[index] as Question
            }
        }
        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0

        questionTextView = findViewById(R.id.question_text_view)
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheat_button)
        resetButton = findViewById(R.id.reset_button)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)

        trueButton.setOnClickListener {
            checkAnswer(true)
            updateAnswerButtonState()
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            updateAnswerButtonState()
        }

        cheatButton.setOnClickListener {
            val intent = CheatActivity.newIntent(this, quizViewModel.currentQuestion.answer)
            val option = ActivityOptionsCompat
                .makeClipRevealAnimation(it, 0, 0, it.width, it.height)
            cheatActivityResultLauncher.launch(intent, option)
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        questionTextView.setOnClickListener {
            nextButton.callOnClick()
        }

        resetButton.setOnClickListener {
            quizViewModel.resetQuiz()
            updateQuestion()
            updateCheatOption()
            Toast.makeText(this, getString(R.string.reset_toast), Toast.LENGTH_SHORT)
                .show()
        }

        updateQuestion()
        updateCheatOption()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_QUESTION_BANK, quizViewModel.questionBank)
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestion.testResId
        questionTextView.setText(questionTextResId)
        updateAnswerButtonState()
    }

    private fun updateCheatOption() {
        cheatButton.isEnabled = quizViewModel.cheatsLeft > 0
        cheatButton.text = getString(R.string.cheat_button, quizViewModel.cheatsLeft)
    }

    private fun updateAnswerButtonState() {
        if (quizViewModel.hasCurrentQuestionAnswer) {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        } else {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val messageResId = if (quizViewModel.currentQuestion.checkAnswer(userAnswer)) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }
        var message = getString(messageResId)
        if (quizViewModel.currentQuestion.isCheat) {
            message += " (${getString(R.string.judgment_toast)})"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .show()

        if (quizViewModel.hasNoAnswerQuestion) {
            Toast.makeText(
                this,
                getString(R.string.result_toast).format(quizViewModel.correctAnswersPercent),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}