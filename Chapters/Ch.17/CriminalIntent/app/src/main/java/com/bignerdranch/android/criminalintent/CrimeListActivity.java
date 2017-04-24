package com.bignerdranch.android.criminalintent;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    public static Intent newIntentForSelfAsParentActivity(Context packageContext, Intent intent) {
        return CrimeListFragment.newIntent(packageContext, CrimeListActivity.class, intent);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        View v = findViewById(R.id.detail_fragment_container);

        if(v == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            fm.beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment crimeListFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();

        CrimeFragment crimeFragment = (CrimeFragment) fm.findFragmentById(R.id.detail_fragment_container);
        fm.beginTransaction()
                .remove(crimeFragment)
                .commit();
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment crimeListFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();
    }
}
