package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.android.geoquiz.answer_shown";

    private static final String TAG = "CheatActivity";

    private boolean mAnswerIsTrue;

    private Button mShowAnswerButton;
    private TextView mAnswerText;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return i;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        final Intent i = new Intent();
        mShowAnswerButton = (Button) findViewById(R.id.showAnswerButton);
        mAnswerText = (TextView) findViewById(R.id.answerTextView);
        setAnswerShownResult(false);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        if(savedInstanceState != null) {
            Log.i(TAG, "Restoring Cheating (or lack thereof) from saved state");
            if (savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN, false)) {
                setAnswerShownResult(true);
                setAnswerText();
            }
        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                setAnswerText();
                setAnswerShownResult(true);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        saveInstanceState.putBoolean(EXTRA_ANSWER_SHOWN, true);
    }

    private void setAnswerText() {
        mAnswerText.setText(
            mAnswerIsTrue ? R.string.true_button : R.string.false_button
        );
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }
}
