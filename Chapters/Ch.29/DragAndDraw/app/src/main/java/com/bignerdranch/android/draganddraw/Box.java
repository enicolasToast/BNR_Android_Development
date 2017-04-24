package com.bignerdranch.android.draganddraw;


import android.graphics.PointF;

import java.io.Serializable;

public class Box implements Serializable {
    private PointF mOrigin;
    private PointF mCurrent;

    private PointF mCurrentRotationOrigin;
    private PointF mCurrentRotationPoint;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
        mCurrentRotationOrigin = origin;
        mCurrentRotationPoint = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public PointF getCurrentRotationOrigin() {
        return mCurrentRotationOrigin;
    }

    public void setCurrentRotationOrigin(float X, float Y) {
        mCurrentRotationOrigin = new PointF(X, Y);
    }

    public PointF getCurrentRotationPoint() {
        return mCurrentRotationPoint;
    }

    public void setCurrentRotationPoint(float X, float Y) {
        mCurrentRotationPoint = new PointF(X, Y);
    }
}
