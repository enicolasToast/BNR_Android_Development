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

    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }

    public void removeCrime(Crime crime) {
        mCrimes.remove(crime);
    }

    public int getPositionFromCrimeId(UUID crimeId) {
        int index = 0;
        for(Crime crime : mCrimes) {
            if(crimeId.equals(crime.getId())) {
                return index;
            }

            index++;
        }

        return -1;
    }
}
