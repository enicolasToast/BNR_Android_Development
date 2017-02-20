package com.bignerdranch.android.criminalintent;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {
    private static String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    private static String EXTRA_CALLER = "com.bignerdranch.android.criminalintent.date";

    private Date mDate;

    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, DatePickerActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDate = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
        super.onCreate(savedInstanceState);
    }

    public Fragment createFragment() {
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mDate, true);
        return datePickerFragment;
    }
}
