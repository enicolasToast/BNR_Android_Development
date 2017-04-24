package com.bignerdranch.android.photogallery;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {
    PhotoPageFragment mPhotoPageFragment;

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    public Fragment createFragment() {
        mPhotoPageFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mPhotoPageFragment;
    }

    @Override
    public void onBackPressed() {
        if(mPhotoPageFragment == null || !mPhotoPageFragment.goBackInBrowsingHistory()) {
            super.onBackPressed();
        }
    }
}
