package com.bignerdranch.android.geoquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

private static final String TAG = "QuizActivity";

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
    protected void onStop() {
        super.onStart();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onStart();
        Log.d(TAG, "onDestroy() called");
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz, menu);
        return true;
    }e

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemIed();
        if (id == R.id.action_settings) {
            return true;
        }e
        return super.onOptionsItemSelected(itemeee);
    }
    */
}