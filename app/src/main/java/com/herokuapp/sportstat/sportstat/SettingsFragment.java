package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;


/*
 *Created by John Rigby on 2/27/15
 *
 *
 *A class to allow users to log a basketball game after the fact.
 *
 *
 */
public class SettingsFragment extends Fragment {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_CROP_PHOTO = 2;
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private static final String SAVED_PREFERENCES = "saved_prefs";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String NAME_TEXT = "saved_name";
    private static final String EMAIL_TEXT = "saved_email";


    private static final String HANDLE = "user_handle";

    Uri mImageCaptureUri;
    ImageView mImageView;
    private static EditText mNameEditText;
    private static EditText mEmailEditText;
    private static EditText mHandleEditText;

    private boolean isTakenFromCamera;

    private boolean loadExceptionThrown = false;
    private boolean saveExceptionThrown = false;
    private int classYr;

    private static SharedPreferences sharedPref;
    private boolean copyExceptionThrown = false;


    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "";

    private int mSectionNumber;



    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }


    }

    //This method is based on the onSavedClicked method from the Camera example app
    public static void onSaveClicked(View v, Context context) {
        // Save picture
        //saveSnap();
        // Making a "toast" informing the user the picture is saved.

         saveProfile();

        Toast.makeText(context,
                "Changes Saved!",
                Toast.LENGTH_SHORT).show();

    }

    public static void onCancelClicked(View v, Context context) {
        Toast.makeText(context,
                "Changes Cancelled",
                Toast.LENGTH_SHORT).show();
//        finish();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);


    }


    @Override
    public void onResume() {
        super.onResume();

        mImageView = (ImageView) getView().findViewById(R.id.imageProfile);

        mNameEditText = (EditText) getView().findViewById(R.id.editName);
        mHandleEditText = (EditText) getView().findViewById(R.id.editHandle);
        mEmailEditText = (EditText) getView().findViewById(R.id.editEmail);

        loadProfile();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }





    //This method was based on the example sharedpreferences app found at http://www.tutorialspoint.com/android/android_shared_preferences.htm
    private static void saveProfile() {
        //When the user clicks Save, save entered information to the SharedPreferences file

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(Globals.USERNAME, mNameEditText.getText().toString());
        editor.putString(Globals.USER_EMAIL, mEmailEditText.getText().toString());
        editor.putString(Globals.USER_HANDLE, mHandleEditText.getText().toString());

        editor.apply();

    }

    //This method was based on the example sharedpreferences app found at http://www.tutorialspoint.com/android/android_shared_preferences.htm
    private void loadProfile() {

        //loadSnap(mImageCaptureUri);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        if(sharedPref.contains(Globals.USERNAME)){
            mNameEditText.setText(sharedPref.getString(Globals.USERNAME, ""));
        }
        if (sharedPref.contains(Globals.USER_EMAIL)) {
            mEmailEditText.setText(sharedPref.getString(Globals.USER_EMAIL, ""));
        }
        if (sharedPref.contains(Globals.USER_HANDLE)) {
            mHandleEditText.setText(sharedPref.getString(Globals.USER_HANDLE, ""));
        }

    }

    @Override
    //Save entered fields
    public void onSaveInstanceState(Bundle outState) {

        boolean instanceExceptionThrown = false;
        super.onSaveInstanceState(outState);

        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
        outState.putString(Globals.USERNAME, mNameEditText.getText().toString());
        outState.putString(Globals.USER_EMAIL, mEmailEditText.getText().toString());
        outState.putString(Globals.USER_HANDLE, mHandleEditText.getText().toString());

    }



}
