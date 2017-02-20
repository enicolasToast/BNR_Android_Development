package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    private static final String ARG_FINISH_ON_COMPLETION = "finish_on_completion";
    private static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    private Date mDate;
    private boolean mFinishOnCompletion;
    private DatePicker mDatePicker;
    private Button positiveButton;

    public static DatePickerFragment newInstance(Date date, boolean finishActivityOnCompletion) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putBoolean(ARG_FINISH_ON_COMPLETION, finishActivityOnCompletion);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);

        return datePickerFragment;
    }

    public static Date getDateFromIntent(Intent data) {
        return (Date) data.getSerializableExtra(EXTRA_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(ARG_DATE);
        mFinishOnCompletion = getArguments().getBoolean(ARG_FINISH_ON_COMPLETION);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);
        positiveButton = (Button) v.findViewById(R.id.dialog_date_confirmation_button);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.YEAR, mDatePicker.getYear());
                calendar.set(Calendar.MONTH, mDatePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                Date date = calendar.getTime();
                sendResult(RESULT_OK, date);

                if(mFinishOnCompletion) {
                    getActivity().finish();
                } else {
                    DatePickerFragment.this.dismiss();
                }
            }
        });

        return v;
    }

    private void sendResult(int resultCode, Date date) {
        Fragment targetFragment = getTargetFragment();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        if (targetFragment == null) {
            getActivity().setResult(resultCode, intent);
            return;
        }

        targetFragment.onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
