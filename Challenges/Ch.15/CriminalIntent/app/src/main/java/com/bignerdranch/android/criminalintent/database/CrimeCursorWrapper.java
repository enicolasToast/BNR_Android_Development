package com.bignerdranch.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.criminalintent.Crime;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable.Cols;

import java.util.Date;
import java.util.UUID;


public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuid = getWrappedCursor().getString(getColumnIndex(Cols.UUID));
        String title = getWrappedCursor().getString(getColumnIndex(Cols.TITLE));
        long date = getWrappedCursor().getLong(getColumnIndex(Cols.DATE));
        int solved = getWrappedCursor().getInt(getColumnIndex(Cols.SOLVED));
        String suspect = getWrappedCursor().getString(getColumnIndex(Cols.SUSPECT));
        String suspectPhone = getWrappedCursor().getString(getColumnIndex(Cols.SUSPECT_PHONE));

        Crime crime = new Crime(UUID.fromString(uuid), new Date(date));
        crime.setTitle(title);
        crime.setSolved(solved != 0);
        crime.setSuspectName(suspect);
        crime.setSuspectPhone(suspectPhone);

        return crime;
    }
}
