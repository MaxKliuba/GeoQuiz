package com.maxclub.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible

const val EXTRA_ANSWER_SHOWN = "com.maxclub.android.geoquiz.answer_shown"
private const val EXTRA_CORRECT_ANSWER = "com.maxclub.android.geoquiz.correct_answer"
private const val KEY_ANSWER_SHOWN = "isAnswerShown"

class CheatActivity : AppCompatActivity() {
    private lateinit var cheatQuestionTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var answerTextView: TextView
    private lateinit var apiLevelTextView: TextView

    private var correctAnswer = false

    companion object {
        fun newIntent(packageContext: Context, correctAnswer: Boolean) =
            Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_CORRECT_ANSWER, correctAnswer)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        correctAnswer = intent.getBooleanExtra(EXTRA_CORRECT_ANSWER, false)

        cheatQuestionTextView = findViewById(R.id.cheat_question_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        answerTextView = findViewById(R.id.answer_text_view)
        apiLevelTextView = findViewById(R.id.api_level_text_view)

        showAnswerButton.setOnClickListener {
            val answerText = if (correctAnswer) {
                R.string.true_button
            } else {
                R.string.false_button
            }
            cheatQuestionTextView.isVisible = false
            showAnswerButton.isVisible = false
            answerTextView.isVisible = true
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }

        savedInstanceState?.getBoolean(KEY_ANSWER_SHOWN, false)?.let {
            if (it) showAnswerButton.callOnClick()
        }

        apiLevelTextView.text = getString(R.string.api_level, Build.VERSION.SDK_INT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ANSWER_SHOWN, answerTextView.isVisible)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }
}