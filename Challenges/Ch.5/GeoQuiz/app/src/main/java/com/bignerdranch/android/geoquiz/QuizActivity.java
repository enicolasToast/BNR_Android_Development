package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private Button      mTrueButton;
    private Button      mFalseButton;
    private Button      mNextButton;
    private Button      mPrevButton;
    private Button      mCheatButton;
    private TextView    mQuestionTextView;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String IS_CHEATER_INDEX = "is_cheater_index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };

    private boolean[] mWasQuestionCheatedOn = new boolean[mQuestionBank.length];

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestionTextView();
            }
        });

        if(savedInstanceState != null) {
            Log.i(TAG, "Restoring Q&A from saved state");
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            mWasQuestionCheatedOn = savedInstanceState.getBooleanArray(IS_CHEATER_INDEX);
        }

        updateQuestionTextView();

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                checkAnswer(false);
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestionTextView();
            }
        });

        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                if(mCurrentIndex < 0) {
                    mCurrentIndex += mQuestionBank.length;
                }

                updateQuestionTextView();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
            // Start CheatActivity
            Intent intent = CheatActivity.newIntent(QuizActivity.this,
                    mQuestionBank[mCurrentIndex].isAnswerTrue());
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    private void updateQuestionTextView() {
        int questionResId = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(questionResId);
    }

    private void checkAnswer(boolean userPressedTrue) {
        int answerResId;
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        if(mWasQuestionCheatedOn[mCurrentIndex]) {
            answerResId = R.string.judgement_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                answerResId = R.string.correct_toast;
            } else {
                answerResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(QuizActivity.this, answerResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        saveInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        saveInstanceState.putBooleanArray(IS_CHEATER_INDEX, mWasQuestionCheatedOn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT) {
            if(data == null) {
                return;
            }

            if(!mWasQuestionCheatedOn[mCurrentIndex]) {
                mWasQuestionCheatedOn[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            }
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
