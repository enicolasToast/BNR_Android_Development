package com.bignerdranch.android.criminalintent;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private final static int CRIME_COUNT = 100;
    private List<Crime> mCrimes;

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        for(int i = 0; i < CRIME_COUNT; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + (i + 1));
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
    }

    public static CrimeLab get(Context context) {
        if(sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID crimeId) {
        for(Crime crime : mCrimes) {
            if(crime.getId().equals(crimeId)) {
                return crime;
            }
        }

        return null;
    }
}
