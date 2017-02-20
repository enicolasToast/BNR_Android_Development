package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.app.Activity.RESULT_OK;

public class TimePickerFragment extends DialogFragment {
    private static final String ARGS_DATE = "date";
    private static final String EXTRA_DATE_ID = "com.bignerdranch.android.criminalintent.date_id";

    private TimePicker mTimePicker;
    private Date mDate;

    public static TimePickerFragment newInstance(Date date) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_DATE, date);
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }

    public static Date getDateFromIntent(Intent data) {
        return (Date) data.getSerializableExtra(EXTRA_DATE_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_date_time_picker);

        mDate = (Date) getArguments().getSerializable(ARGS_DATE);
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(mDate);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        mTimePicker.setHour(hour);

        int minute = calendar.get(Calendar.MINUTE);
        mTimePicker.setMinute(minute);

        mTimePicker.setIs24HourView(false);

        return new AlertDialog.Builder(getActivity())
                .setView(mTimePicker)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment targetFragment = getTargetFragment();
                        calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
                        calendar.set(Calendar.MINUTE, mTimePicker.getMinute());
                        Date date = calendar.getTime();
                        Intent data = new Intent();
                        data.putExtra(EXTRA_DATE_ID, date);
                        targetFragment.onActivityResult(getTargetRequestCode(), RESULT_OK, data);
                    }
                })
                .create();
    }
}
