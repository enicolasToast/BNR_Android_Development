package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String TAG = CheatActivity.class.toString();
    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.android.geoquiz.answer_shown";

    private boolean mAnswerIsTrue;

    private Button mShowAnswerButton;
    private TextView mAnswerText;

    private static final String ANSWER_SHOWN = "answer_shown";
    public boolean answerShown = false;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return i;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        mShowAnswerButton = (Button) findViewById(R.id.showAnswerButton);
        mAnswerText = (TextView) findViewById(R.id.answerTextView);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        if(savedInstanceState != null) {
            answerShown = savedInstanceState.getBoolean(ANSWER_SHOWN);
        }

        setAnswerShownResult(answerShown);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                mAnswerText.setText(
                    mAnswerIsTrue ? R.string.true_button : R.string.false_button
                );

                setAnswerShownResult(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnswerText.setVisibility(View.VISIBLE);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mAnswerText.setVisibility(View.VISIBLE);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");

        setAnswerShownResult(answerShown);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");

        saveInstanceState.putBoolean(ANSWER_SHOWN, answerShown);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        answerShown = isAnswerShown;
        data.putExtra(EXTRA_ANSWER_SHOWN, answerShown);
        setResult(RESULT_OK, data);
    }
}
