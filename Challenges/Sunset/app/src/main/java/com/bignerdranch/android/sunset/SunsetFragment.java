package com.bignerdranch.android.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import java.util.concurrent.TimeUnit;


public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mReflectedSunView;
    private View mSkyView;
    private View mWaterView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private AnimatorSet mSunsetAnimator;
    private AnimatorSet mSunriseAnimator;
    private ObjectAnimator mNightSkyAnimator;
    private ObjectAnimator mMorningSkyAnimator;

    private boolean isDayTime = true;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @TargetApi(19)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = v;

        mSceneView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDayTime) {
                    boolean isSunInView = true;
                    if(mSunriseAnimator != null && mSunriseAnimator.isRunning()) {
                        isSunInView = !mMorningSkyAnimator.isRunning();
                        mSunriseAnimator.pause();
                    }
                    startSunsetAnimation(isSunInView);
                } else {
                    boolean isSunInView = false;
                    if(mSunsetAnimator != null && mSunsetAnimator.isRunning()) {
                        isSunInView = !mNightSkyAnimator.isRunning();
                        mSunsetAnimator.pause();
                    }
                    startSunriseAnimation(isSunInView);
                }

                isDayTime = !isDayTime;
            }
        });

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        mSunView = v.findViewById(R.id.sun);
        mReflectedSunView = v.findViewById(R.id.reflected_sun);
        mSkyView = v.findViewById(R.id.sky);
        mWaterView = v.findViewById(R.id.water);

        return v;
    }

    private void startSunsetAnimation(boolean isSunInView) {
        float sunYStart = mSunView.getY();
        float sunYEnd = mSkyView.getBottom();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(TimeUnit.SECONDS.toMillis(3));

        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", ((ColorDrawable) mSkyView.getBackground()).getColor(), mSunsetSkyColor)
                .setDuration(TimeUnit.SECONDS.toMillis(3));
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        mNightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(TimeUnit.MILLISECONDS.toMillis(1500));
        mNightSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator pulsatingWidthSunAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleX", 1f, 1.2f)
                .setDuration(TimeUnit.MILLISECONDS.toMillis(500));
        pulsatingWidthSunAnimator.setRepeatCount(6);

        float reflectedSunYStart = mReflectedSunView.getY();
        float reflectedSunYEnd = -mReflectedSunView.getHeight();

        ObjectAnimator reflectedHeightAnimator = ObjectAnimator
                .ofFloat(mReflectedSunView, "y", reflectedSunYStart, reflectedSunYEnd)
                .setDuration(TimeUnit.SECONDS.toMillis(3));
        reflectedHeightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator pulsatingWidthReflectingSunAnimator = ObjectAnimator
                .ofFloat(mReflectedSunView, "scaleX", 1f, 1.2f)
                .setDuration(TimeUnit.MILLISECONDS.toMillis(500));
        pulsatingWidthReflectingSunAnimator.setRepeatCount(6);

        mSunsetAnimator = new AnimatorSet();

        if(isSunInView) {
            mSunsetAnimator.play(heightAnimator)
                    .with(reflectedHeightAnimator)
                    .with(sunsetSkyAnimator)
                    .with(pulsatingWidthSunAnimator)
                    .with(pulsatingWidthReflectingSunAnimator)
                    .before(mNightSkyAnimator);
        } else {
            mNightSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", ((ColorDrawable) mSkyView.getBackground()).getColor(), mNightSkyColor)
                    .setDuration(TimeUnit.MILLISECONDS.toMillis(1500));
            mNightSkyAnimator.setEvaluator(new ArgbEvaluator());

            mSunsetAnimator.play(mNightSkyAnimator);
        }

        mSunsetAnimator.start();
    }

    private void startSunriseAnimation(boolean isSunInView) {
        float sunYStart = isSunInView ? mSunView.getY() : mSkyView.getBottom();
        float sunYEnd = mSunView.getTop();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(TimeUnit.SECONDS.toMillis(3));
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunriseSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                .setDuration(TimeUnit.SECONDS.toMillis(3));
        sunriseSkyAnimator.setEvaluator(new ArgbEvaluator());

        mMorningSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", ((ColorDrawable) mSkyView.getBackground()).getColor(), mSunsetSkyColor)
                .setDuration(TimeUnit.MILLISECONDS.toMillis(1500));
        mMorningSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator pulsatingWidthSunAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleX", 1f, 1.2f)
                .setDuration(TimeUnit.MILLISECONDS.toMillis(500));
        pulsatingWidthSunAnimator.setRepeatCount(6);

        float reflectedSunYStart = isSunInView ? mReflectedSunView.getY() : -mReflectedSunView.getHeight();
        float reflectedSunYEnd = mReflectedSunView.getTop();

        ObjectAnimator reflectedHeightAnimator = ObjectAnimator
                .ofFloat(mReflectedSunView, "y", reflectedSunYStart, reflectedSunYEnd)
                .setDuration(TimeUnit.SECONDS.toMillis(3));
        reflectedHeightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator pulsatingWidthReflectingSunAnimator = ObjectAnimator
                .ofFloat(mReflectedSunView, "scaleX", 1f, 1.2f)
                .setDuration(TimeUnit.MILLISECONDS.toMillis(500));
        pulsatingWidthReflectingSunAnimator.setRepeatCount(6);

        mSunriseAnimator = new AnimatorSet();

        if(isSunInView) {
            sunriseSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", ((ColorDrawable) mSkyView.getBackground()).getColor(), mBlueSkyColor)
                    .setDuration(TimeUnit.SECONDS.toMillis(3));
            sunriseSkyAnimator.setEvaluator(new ArgbEvaluator());

            mSunriseAnimator.play(heightAnimator)
                    .with(reflectedHeightAnimator)
                    .with(sunriseSkyAnimator)
                    .with(pulsatingWidthSunAnimator)
                    .with(pulsatingWidthReflectingSunAnimator);
        } else {
            mSunriseAnimator.play(heightAnimator)
                    .with(reflectedHeightAnimator)
                    .with(sunriseSkyAnimator)
                    .with(pulsatingWidthSunAnimator)
                    .with(pulsatingWidthReflectingSunAnimator)
                    .after(mMorningSkyAnimator);
        }

        mSunriseAnimator.start();
    }
}
