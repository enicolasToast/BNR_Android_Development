package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class PhotoViewerFragment extends DialogFragment {
    private static final String ARG_PHOTO = "photo";
    private static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent";

    private ImageView mPhotoView;

    public static PhotoViewerFragment newInstance(String photoFilePath) {
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO, photoFilePath);
        PhotoViewerFragment photoViewerFragment = new PhotoViewerFragment();
        photoViewerFragment.setArguments(args);

        return photoViewerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String photoFilePath = getArguments().getString(ARG_PHOTO);

        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFilePath, getActivity());

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        mPhotoView = (ImageView) v.findViewById(R.id.dialog_crime_photo);
        mPhotoView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(mPhotoView)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
