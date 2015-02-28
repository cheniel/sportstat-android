package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/*
 *Created by John Rigby on 2/27/15
 *
 *
 *A class to allow users to log a basketball game after the fact.
 *
 *
 */
public class LogGameFragment extends ListFragment {


    private static final String ARG_SECTION_NUMBER = "section_number";


    private int mSectionNumber;


    static final String[] MANUAL_ITEM_TERMS = {"Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment"};
    private static final String TAG = "tag";
    private String mSelectedListItem;
    static Integer numEntry = 0;

    private BasketballGame mGame;





    public static LogGameFragment newInstance(int sectionNumber) {
        LogGameFragment fragment = new LogGameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public LogGameFragment() {
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



        //Create a default date string (current time and date)
        Date defTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss MMM d yyyy");

        //Create a blank new game
        mGame = new BasketballGame();


        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this.getActivity(),
                R.layout.plain_textview, MANUAL_ITEM_TERMS);

        // Assign the adapter to ListView
        setListAdapter(mAdapter);

        // Define the listener interface
        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                mSelectedListItem = ((TextView) view).getText().toString();

                if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[0])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_DATE_PICKER);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[1])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_TIME_PICKER);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[2])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_DURATION_ALERT);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[3])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_DISTANCE_ALERT);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[4])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_CALORIES_ALERT);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[5])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_HEARTRATE_ALERT);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[6])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_COMMENT_ALERT);
                }

            }
        };

        // Get the ListView and wired the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(mListener);



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_game, container, false);
    }


    public void displayDialog(int id) {
        android.app.DialogFragment fragment = SportStatDialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_list_item_tag));
    }


    //onClick methods for the save and cancel buttons; they return the user to the Start fragment.
    //onSaveClicked saves the entry to the database
    public void onSaveClicked(View v) {

        //String saveToast = "Game Saved!";

        //Tell the user that the entry was saved, indicating the number entry
        //Toast.makeText(this, saveToast, Toast.LENGTH_SHORT).show();
        //finish();
    }

    public void onCancelClicked(View v) {
        //Tell the user any changes they made have not been saved
        //Toast.makeText(this.getApplicationContext(), R.string.discard_entry_text, Toast.LENGTH_SHORT).show();

        //finish();
    }

    //Methods to set fields on a potential ExerciseEntry--------------------------------------

    public void setBasketBallGameAssists(int as) {
        mGame.setAssists(as);
    }

    public void setExerciseEntryDistance(double distance) {
        newEntry.setmDistance(distance);
    }

    public void setExerciseEntryCalories(int calories) {
        newEntry.setmCalorie(calories);
    }

    public void setExerciseEntryHeartrate(int heartrate) {
        newEntry.setmHeartRate(heartrate);
    }

    public void setExerciseEntryComment(String comment) {
        newEntry.setComment(comment);
    }

    //Date and time setting method
    public void setExerciseEntryDate(int dateorTime, int yrOrhr, int monthOrmin, int dayOrsec) {
        int i = 0;
        String minStr;
        String secStr;

        //Ensuring minutes and seconds are in correct format
        if (monthOrmin < 10) {
            minStr = "" + 0 + monthOrmin;
        } else {
            minStr = "" + monthOrmin;
        }

        if (dayOrsec < 10) {
            secStr = "" + 0 + dayOrsec;
        } else {
            secStr = "" + dayOrsec;
        }

        String curDate = newEntry.getmDateTime();

        StringBuilder newDate = new StringBuilder();

        if (dateorTime == 0) {
            newDate.append(newEntry.getmDateTime(), 0, 8);
            newDate.append(" " + findMonthName(monthOrmin) + " " + dayOrsec + " " + yrOrhr);
        } else {
            newDate.append(yrOrhr + ":" + minStr + ":" + secStr);
            newDate.append(curDate, 8, curDate.length());
        }

        newEntry.setmDateTime(newDate.toString());
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
