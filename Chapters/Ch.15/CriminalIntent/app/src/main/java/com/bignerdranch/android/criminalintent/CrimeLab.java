package com.bignerdranch.android.criminalintent;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable.Cols;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if(sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(null, null);

        try {
            crimeCursorWrapper.moveToFirst();
            while(!crimeCursorWrapper.isAfterLast()) {
                crimes.add(crimeCursorWrapper.getCrime());
                crimeCursorWrapper.moveToNext();
            }
        } finally {
            crimeCursorWrapper.close();
        }

        return crimes;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME, new String[] { "_rowid_", Cols.UUID, Cols.TITLE, Cols.DATE, Cols.SOLVED, Cols.SUSPECT},
                whereClause, whereArgs, null, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    public Crime getCrime(UUID crimeId) {
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(Cols.UUID + " = ?", new String[] { crimeId.toString() });

        try {
            if(crimeCursorWrapper.getCount() == 0) {
                return null;
            }

            crimeCursorWrapper.moveToFirst();
            return crimeCursorWrapper.getCrime();
        } finally {
            crimeCursorWrapper.close();
        }
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(Cols.UUID, crime.getId().toString());
        values.put(Cols.TITLE, crime.getTitle());
        values.put(Cols.DATE, crime.getDate().getTime());
        values.put(Cols.SOLVED, crime.isSolved()? 1 : 0);
        values.put(Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[] { crime.getId().toString() });
    }

    public void removeCrime(Crime crime) {
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[] { crime.getId().toString() });
    }

    public int getPositionFromCrimeId(UUID crimeId) {
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(Cols.UUID + " = ?", new String[] { crimeId.toString() });

        try {
            if(crimeCursorWrapper.getCount() == 0) {
                return -1;
            }

            crimeCursorWrapper.moveToFirst();
            return crimeCursorWrapper.getInt(crimeCursorWrapper.getColumnIndex("rowid")) - 1;
        } finally {
            crimeCursorWrapper.close();
        }
    }
}
