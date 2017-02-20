package com.bignerdranch.android.criminalintent;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public abstract class CriminalIntentActivity extends AppCompatActivity {
    public static Intent newIntent(Context packageContext, Object... values) {
        return null;
    }

    public static Intent newIntentForSelfAsParentActivity(Context packageContext, Object... values) {
        return null;
    }
}
