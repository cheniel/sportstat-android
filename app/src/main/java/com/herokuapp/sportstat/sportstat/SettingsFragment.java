package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/*
 *Created by John Rigby on 2/27/15
 *
 *
 *A class to allow users to log a basketball game after the fact.
 *
 *
 */
public class SettingsFragment extends Fragment {


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d(TAG, "Settings FRAGMENT DISPLAYING");


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_game, container, false);
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

}
