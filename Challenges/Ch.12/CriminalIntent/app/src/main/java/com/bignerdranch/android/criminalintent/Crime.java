package com.bignerdranch.android.criminalintent;

import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private static final String DATE_FORMAT = "EEEE, MMMM, dd, yyyy";
    private static final String TIME_FORMAT = "hh:mm:ss a";

    private UUID mId;
    private String mTitle;

    private Date mDate;
    private boolean mSolved;

    public Crime() {
        //Generate unique identifier
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public CharSequence getDateString() {
        return DateFormat.format(DATE_FORMAT, mDate);
    }

    public CharSequence getTimeString() {
        return DateFormat.format(TIME_FORMAT, mDate);
    }

    public void setDate(Date date) {
        mDate =  date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
