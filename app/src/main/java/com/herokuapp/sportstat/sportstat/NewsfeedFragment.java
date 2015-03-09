package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import com.herokuapp.sportstat.sportstat.CustomListResources.LazyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 *.
 * Use the {@link NewsfeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsfeedFragment extends ListFragment {

    public static final String KEY_SONG = "song"; // parent node
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_USERNAME = "un";
    public static final String KEY_ASSISTS = "assists";
    public static final String KEY_TWOS = "twos";
    public static final String KEY_THREES = "threes";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_THUMB_URL = "thumb_url";
    private static final String TAG = "dis tag";

    ListView list;
    LazyAdapter mAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int mSectionNumber;
    private ArrayAdapter<BasketballGame> defAdapter;
    private ArrayList<HashMap<String, String>> mGamesArray;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment NewsfeedFragment.
     */


    public static NewsfeedFragment newInstance(int sectionNumber) {
        NewsfeedFragment fragment = new NewsfeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsfeedFragment() {
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


        mGamesArray = new ArrayList<HashMap<String, String>>();


        // Display the fragment as the main content. I found the getChildFragmentManager() method on StackOverflow
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.newsfeed_fragment, new ListFragment()).commit();

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
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
                                 + "/feed.json");

                String userInfoStr = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "users/" + userId
                                + ".json");

                Log.d(TAG, "FUCK THIS SHIT: "+userInfoStr);

                try {
                    final JSONArray newsfeed = new JSONArray(newsfeedString);
                    final JSONObject userInfo = new JSONObject(userInfoStr);


                    handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                updateViewUsingJSONArray(newsfeed, userInfo);
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

    private void updateViewUsingJSONArray(JSONArray newsfeed, JSONObject userInfo) {
        ArrayList<BasketballGame> feed = new ArrayList<>();

        for (int i = 0; i < newsfeed.length(); i++) {
            try {
                JSONObject basketballObject = newsfeed.getJSONObject(i);
                feed.add (feed.size()-i,
                        BasketballGame.getBasketballGameFromJSONObject(
                                basketballObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String imgId = "";
        try{
           imgId = userInfo.getString("avatar");
        } catch(Exception e){
            imgId = "9";
        }

        updateView(feed, imgId);
    }

    //Takes an ArrayList of BasketBallGame objects and updates the listview
    private void updateView(ArrayList<BasketballGame> gamesArray, String imgId) {
        mGamesArray.clear();

        ArrayList<HashMap<String, String>> games = new ArrayList<>();

        for(BasketballGame game : gamesArray){

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(KEY_ID, ""+game.getUserId()); //Stores user ID as string...

            String capUserName = game.getUsername().substring(0,1).toUpperCase()
                    + game.getUsername().substring(1, game.getUsername().length());

            map.put(KEY_USERNAME, capUserName);
            map.put(KEY_TITLE, game.toStringForNewsFeed());
            map.put(KEY_ASSISTS, " "+game.getAssists()+" ");
            map.put(KEY_TWOS, " "+game.getTwoPoints()+" ");
            map.put(KEY_THREES, " "+game.getThreePoints()+" ");
            map.put(KEY_THUMB_URL, imgId);


            mGamesArray.add(map);

        }



        mAdapter = new LazyAdapter(this.getActivity(), mGamesArray, true, false, getActivity());


        //defAdapter = new ArrayAdapter<BasketballGame>(this.getActivity(), R.layout.plain_textview, gamesArray);
        setListAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    //When user clicks someone's newsfeed entry, bring them to that users' page
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this.getActivity(), GameSummaryActivity.class);
        HashMap<String, String> selectedItem = mGamesArray.get(position);
        BasketballGame selectedGame = new BasketballGame();
        selectedGame.setUsername(selectedItem.get(KEY_USERNAME));
        selectedGame.setUserId(Integer.parseInt(selectedItem.get(KEY_ID)));
        Log.d(TAG, "DIS USER ID: "+Integer.parseInt(selectedItem.get(KEY_ID)));
        selectedGame.setAssists(Integer.parseInt(selectedItem.get(KEY_ASSISTS).substring(1,2)));
        selectedGame.setTwoPoints(Integer.parseInt(selectedItem.get(KEY_TWOS).substring(1,2)));
        selectedGame.setThreePoints(Integer.parseInt(selectedItem.get(KEY_THREES).substring(1,2)));


        intent.putExtra(SportLoggingActivity.BASKETBALL_GAME, selectedGame);
        intent.putExtra(SportLoggingActivity.CALLING_ACTIVITY, "newsfeed_fragment");

        startActivity(intent);



    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

