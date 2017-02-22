package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;

    private Date mDate;
    private boolean mSolved;

    private Suspect mSuspect;

    public Crime() {
        //Generate unique identifier
        this(UUID.randomUUID(), new Date());
    }

    public Crime(UUID id) {
        this(id, new Date());
    }

    public Crime(UUID id, Date date) {
        mId = id;
        mDate = date;
        mSuspect = new Suspect();
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

    public void setDate(Date date) {
        mDate =  date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Suspect getSuspect() {
        return mSuspect;
    }

    public void setSuspectName(String suspectName) {
        mSuspect.setName(suspectName);
    }

    public void setSuspectPhone(String suspectPhone) {
        mSuspect.setPhone(suspectPhone);
    }

    public void setSuspectId(long suspectId) {
        mSuspect.setId(suspectId);
    }
}
