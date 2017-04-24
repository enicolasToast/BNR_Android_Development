package com.bignerdranch.android.locatr;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public class LocatrActivity extends SingleFragmentActivity {
    private static int REQUEST_ERROR = 0;

    public LocatrFragment createFragment() {
        return new LocatrFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GooglePlayServicesUtil
                    .getErrorDialog(errorCode, this, REQUEST_ERROR,
                            new OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    //Leave if services are unavailable
                                    finish();
                                }
                            });

            errorDialog.show();
        }
    }
}
