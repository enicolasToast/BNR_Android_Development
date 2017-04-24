package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";
    private static final String PARCELABLE_PARENT_VIEW = "parentView";
    private static final String PARCELABLE_BOXES = "boxes";

    private Box mCurrentBox;

    int mPointerId1 = -1, mPointerId2 = -1;

    private List<Box> mBoxes = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    // Used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    // Used when inflating the view from XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                action = "ACTION_DOWN";
                mCurrentBox = new Box(current);
                mBoxes.add(mCurrentBox);

                int pointerIndx = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndx);

                if(mPointerId1 == -1) {
                    mPointerId1 = pointerId;
                } else if(mPointerId2 == -1)
                {
                    mPointerId2 = pointerId;
                    mCurrentBox.setCurrentRotationOrigin(event.getX(pointerIndx),
                            event.getY(pointerIndx));
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                action = "ACTION_MOVE";
                int pointerIndx = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndx);
                if(mCurrentBox != null) {
                    if(mPointerId1 == mPointerId1) {
                        mCurrentBox.setCurrent(current);
                    } else if(pointerId == mPointerId2)
                    {
                        mCurrentBox.setCurrentRotationPoint(event.getX(pointerIndx),
                                event.getY(pointerIndx));
                    }

                    invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                action = "ACTION_UP";
                int pointerIndx = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndx);

                if(mPointerId1 == mPointerId1) {
                    mPointerId1 = -1;
                } else if(pointerId == mPointerId2)
                {
                    mPointerId2 = -1;
                }
                mCurrentBox = null;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                action = "ACTION_CANCEL";
                int pointerIndx = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndx);

                if(mPointerId1 == mPointerId1) {
                    mPointerId1 = -1;
                } else if(pointerId == mPointerId2)
                {
                    mPointerId2 = -1;
                }
                mCurrentBox = null;
                break;
            }
        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);

        return true;
    }

    private float calculateRotationDegrees(PointF origin, PointF target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - origin.y, target.x - origin.x));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Fill the background
        canvas.drawPaint(mBackgroundPaint);

        for(Box box : mBoxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            float degreesOfRotation = calculateRotationDegrees(box.getCurrentRotationOrigin(), box.getCurrentRotationPoint());
            canvas.rotate(degreesOfRotation);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.restore();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parentParcelable = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE_PARENT_VIEW, parentParcelable);
        bundle.putSerializable(PARCELABLE_BOXES, (ArrayList<Box>) mBoxes);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcelableBundle) {
        Bundle bundle = (Bundle) parcelableBundle;
        super.onRestoreInstanceState(bundle.getParcelable(PARCELABLE_PARENT_VIEW));
        mBoxes = (ArrayList<Box>) bundle.getSerializable(PARCELABLE_BOXES);
    }
}
