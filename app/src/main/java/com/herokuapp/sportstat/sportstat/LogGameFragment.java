package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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
    private  AdapterView.OnItemClickListener mListener;


    private int mSectionNumber;


    static final String[] MANUAL_ITEM_TERMS = {"Date", "Start Time", "End Time", "Assists", "Two-Points", "Three-Points", "Comment"};
    private static final String TAG = "tag";
    private String mSelectedListItem;
    static Integer numEntry = 0;

    private static BasketballGame mGame;

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
        mListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                mSelectedListItem = ((TextView) view).getText().toString();

                if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[0])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_DATE_PICKER);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[1])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_START_TIME_PICKER);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[2])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_END_TIME_PICKER);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[3])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_ASSISTS_ALERT);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[4])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_TWO_POINTS_ALERT);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[5])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_THREE_POINTS_ALERT);
                } else if (mSelectedListItem.equals(MANUAL_ITEM_TERMS[6])) {
                    displayDialog(SportStatDialogFragment.DIALOG_ID_COMMENT_ALERT);
                }

            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_game, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get the ListView and wired the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(mListener);
    }

    public void displayDialog(int id) {
        android.app.DialogFragment fragment = SportStatDialogFragment.newInstance(id, this);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_list_item_tag));
    }


    //onClick methods for the save and cancel buttons; they return the user to the Start fragment.
    //onSaveClicked saves the entry to the database
    public static void onSaveClicked(View v, final MainActivity act){

        mGame.setmImageIdentifier(SettingsFragment.mImageId);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        mGame.setUsername(prefs.getString(Globals.USERNAME, null));
        mGame.setUserId(prefs.getInt(Globals.USER_ID, 0));

        final JSONObject post = mGame.getJSONObject();

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String postResponseString = CloudUtilities.post(
                        act.getString(R.string.sportstat_url) + "basketball_games", post
                );



                try {
                    JSONObject postResponse = new JSONObject(postResponseString);

                    if (postResponse.has("status")) {
                        makeToast("Registration failed.");
                        return "failure";
                    }

                    makeToast("Saved!");
                    //finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                makeToast("Save failed.");
                return "failure";
            }

            private void makeToast(final String toast) {
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(act.getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }.execute();

    }



    //Methods to set fields on a potential ExerciseEntry--------------------------------------

    public void setBasketBallGameAssists(int as) {
        mGame.setAssists(as);
    }

    public void setBasketBallGameTwoPoints(int twoPts) {
        mGame.setTwoPoints(twoPts);
    }

    public void setBasketBallGameThreePoints(int threePts) {
        mGame.setThreePoints(threePts);
    }

    public void setBasketBallGameComment(String comment) {
        mGame.setComment(comment);
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
