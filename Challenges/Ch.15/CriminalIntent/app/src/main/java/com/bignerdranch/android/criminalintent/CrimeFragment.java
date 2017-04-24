package com.bignerdranch.android.criminalintent;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String EXTRA_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private EditText mEditText;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;

    private Crime mCrime;

    public static CrimeFragment newInstance(UUID crimeID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crime_id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crime_id);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mEditText = (EditText) view.findViewById(R.id.crime_title);
        mEditText.setText(mCrime.getTitle());
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateButton = (Button) view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.show(fragmentManager, DIALOG_DATE);
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentBuilder ib = ShareCompat.IntentBuilder.from(getActivity());
                ib.setChooserTitle(getString(R.string.send_report));
                ib.setSubject(getString(R.string.crime_report_subject));
                ib.setType("text/plain");
                ib.setText(getCrimeReport());
                ib.startChooser();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        mCallSuspectButton = (Button) view.findViewById(R.id.crime_call_suspect);
        mCallSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCrime.getSuspect().getPhone() == null) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{permission.READ_CONTACTS},
                                REQUEST_CONTACT);
                        return;
                    }
                    querySuspectPhone();
                }

                callSuspect();
            }
        });

        if (mCrime.getSuspect() != null && mCrime.getSuspect().getName() != null) {
            mSuspectButton.setText(mCrime.getSuspect().getName());
        } else {
            mCallSuspectButton.setEnabled(false);
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
            mCallSuspectButton.setEnabled(false);
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CONTACT && grantResults[0] == PERMISSION_GRANTED) {
            querySuspectPhone();
            callSuspect();
        }
    }

    private void querySuspectPhone() {
        Cursor cursor = getActivity().getContentResolver().query(
                CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        Phone.NUMBER
                },
                CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{Long.toString(mCrime.getSuspect().getId())}, null);

        try {
            if (cursor.getCount() == 0) {
                Toast.makeText(getActivity(), "No Phone number found for Suspect!", Toast.LENGTH_LONG).show();
                return;
            }

            cursor.moveToFirst();
            String phoneNumber = cursor.getString(0);
            mCrime.setSuspectPhone(phoneNumber);
        } finally {
            cursor.close();
        }
    }

    private void callSuspect() {
        if(mCrime.getSuspect().getPhone() != null) {
            Uri suspectPhone = Uri.parse("tel:" + mCrime.getSuspect().getPhone());
            Intent i = new Intent(Intent.ACTION_DIAL, suspectPhone);
            startActivity(i);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId())
        {
            case R.id.menu_item_delete_crime:
            {
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                getActivity().finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_DATE) {
            Date date = DatePickerFragment.getDateFromIntent(data);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return values for.
            String[] queryFields = new String[] {
                Contacts.DISPLAY_NAME,
                Contacts._ID
            };
            //Perform you query - the contract uri is like a "where" clause here
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                //Pull out the first column of the first row of data - that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspectName(suspect);
                long id = c.getLong(1);
                mCrime.setSuspectId(id);
                mSuspectButton.setText(suspect);
                mCallSuspectButton.setEnabled(true);
            } finally {
                c.close();
            }
        }
    }

    private String getCrimeReport() {
        String solvedString;
        if(mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = null;
        if(mCrime.getSuspect() != null) {
            suspect = getString(R.string.crime_report_suspect, suspect);
        } else {
            suspect = getString(R.string.crime_report_no_suspect);
        }

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }
}
