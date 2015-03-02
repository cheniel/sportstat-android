package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


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
    private static final java.lang.String GET_FROM_PREFS = "get_img_from_prefs";
    private static int mImageId;

    private static final String HANDLE = "user_handle";
    private static final int PHOTO_SELECTED = 99;

    Uri mImageCaptureUri;
    private SharedPreferences mImgSharedPref;
    private static ImageView mImageView;
    private static EditText mNameEditText;
    private static EditText mEmailEditText;
    private static EditText mHandleEditText;

    private boolean isTakenFromCamera;

    private boolean loadExceptionThrown = false;
    private boolean saveExceptionThrown = false;
    private int classYr;

    private static SharedPreferences sharedPref;
    private boolean copyExceptionThrown = false;
    public static boolean getFromSharedPrefs;


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

        if(getFromSharedPrefs){
            Log.d(TAG, "fuck me 1");
        }



        if (savedInstanceState != null) {

            getFromSharedPrefs =
                    savedInstanceState.getBoolean(GET_FROM_PREFS, false);

        }


    }

    //This method is based on the onSavedClicked method from the Camera example app
    public static void onSaveClicked(View v, MainActivity act) {

        // Making a "toast" informing the user the picture is saved.

        saveProfile(act);

        Toast.makeText(act.getApplicationContext(),
                "Changes Saved!",
                Toast.LENGTH_SHORT).show();

    }

    public static void onCancelClicked(View v, Context context) {
        Toast.makeText(context,
                "Changes Cancelled",
                Toast.LENGTH_SHORT).show();
//        finish();
    }


    //When the user clicks Change Photo, bring up a gallery of standard avatars. Allow
    //the user to choose one of them.
    public static void onChangePhotoClicked(View v, MainActivity context){
        Intent i = new Intent(context, AvatarGalleryActivity.class);

        context.startActivity(i);

    }

    public static void setImage(int pos){

        switch(pos){
            case 0:
                mImageId = R.drawable.sample_1;
                break;
            case 1:
                mImageId = R.drawable.sample_2;
                break;
            case 2:
                mImageId = R.drawable.sample_3;
                break;
            case 3:
                mImageId = R.drawable.sample_4;
                break;
            case 4:
                mImageId = R.drawable.sample_5;
                break;
            case 5:
                mImageId = R.drawable.sample_6;
                break;
            default:
                mImageId = R.drawable.blank_profile;
                break;

        }
        mImageView.setImageResource(mImageId);
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
        mNameEditText.setKeyListener(null);
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
    private static void saveProfile(MainActivity act) {
        //When the user clicks Save, save entered information to the SharedPreferences file

        SharedPreferences.Editor editor = sharedPref.edit();


        editor.putInt(Globals.USER_PROFILE_IMG_ID, mImageId);
        editor.putString(Globals.USERNAME, mNameEditText.getText().toString());
        editor.putString(Globals.USER_EMAIL, mEmailEditText.getText().toString());
        editor.putString(Globals.USER_HANDLE, mHandleEditText.getText().toString());

        editor.apply();

    }

    //This method was based on the example sharedpreferences app found at http://www.tutorialspoint.com/android/android_shared_preferences.htm
    private void loadProfile() {

        //loadSnap(mImageCaptureUri);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        if(sharedPref.contains(Globals.USER_PROFILE_IMG_ID)&&getFromSharedPrefs){
            mImageId = sharedPref.getInt(Globals.USER_PROFILE_IMG_ID, 99);
            mImageView.setImageResource(mImageId);
        }
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

        outState.putBoolean(GET_FROM_PREFS, getFromSharedPrefs);
        outState.putInt(Globals.USER_PROFILE_IMG_ID, mImageId);
        outState.putString(Globals.USERNAME, mNameEditText.getText().toString());
        outState.putString(Globals.USER_EMAIL, mEmailEditText.getText().toString());
        outState.putString(Globals.USER_HANDLE, mHandleEditText.getText().toString());

    }



}
