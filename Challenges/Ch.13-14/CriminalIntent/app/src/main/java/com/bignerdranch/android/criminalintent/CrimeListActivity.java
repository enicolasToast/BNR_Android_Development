package com.bignerdranch.android.criminalintent;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {
    public static Intent newIntentForSelfAsParentActivity(Context packageContext, Intent intent) {
        return CrimeListFragment.newIntent(packageContext, CrimeListActivity.class, intent);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
