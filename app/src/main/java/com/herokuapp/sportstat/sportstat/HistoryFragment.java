package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
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
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int mSectionNumber;
    private ArrayAdapter<BasketballGame> defAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment  HistoryFragment.
     */


    public static HistoryFragment newInstance(int sectionNumber) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryFragment() {
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


        // Display the fragment as the main content. I found the getChildFragmentManager() method on StackOverflow
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_history, new ListFragment()).commit();

        }



        ArrayList<BasketballGame> gamesArray = new ArrayList<>();
        BasketballGame one = new BasketballGame();
        gamesArray.add(one);


        //Fill the gamesArray with the users friends' saved games in chronological order
        updateView(gamesArray);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        final int userId = PreferenceManager.getDefaultSharedPreferences(
                getActivity()).getInt(Globals.USER_ID, -1);

        if (userId == -1) {
            Log.e(getActivity().getLocalClassName(), "preference error");
            return;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String newsfeedString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "users/" + userId
                                + "/basketball_games.json");

                Log.d(getActivity().getLocalClassName(), newsfeedString);

                try {
                    final JSONArray newsfeed = new JSONArray(newsfeedString);

                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    updateViewUsingJSONArray(newsfeed);
                                }
                            }
                    );


                    return "success";
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "failure";
            }

        }.execute();

    }

    private void updateViewUsingJSONArray(JSONArray newsfeed) {
        ArrayList<BasketballGame> feed = new ArrayList<>();

        for (int i = 0; i < newsfeed.length(); i++) {
            try {
                JSONObject basketballObject = newsfeed.getJSONObject(i);
                feed.add(
                        BasketballGame.getBasketballGameFromJSONObject(
                                basketballObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        updateView(feed);
    }

    //Takes an ArrayList of BasketBallGame objects and updates the listview
    private void updateView(ArrayList<BasketballGame> gamesArray) {
        defAdapter = new ArrayAdapter<BasketballGame>(this.getActivity(), R.layout.plain_textview, gamesArray);
        setListAdapter(defAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //((FriendViewActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

