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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.herokuapp.sportstat.sportstat.CustomListResources.LazyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 *.
 * Use the {@link NewsfeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderBoardFragment extends ListFragment {

    public static final String KEY_SONG = "song"; // parent node
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_USERNAME = "un";
    public static final String KEY_ASSISTS = "assists";
    public static final String KEY_TWOS = "twos";
    public static final String KEY_THREES = "threes";
    public static final String KEY_STATSCORE = "stat_score";
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
    private ArrayList<BasketballGame> mBasketballGames;
    private String mStatScore;
    private Switch mySwitch;
    private String mUrl;
    private AsyncTask<String, Void, String> mRefreshView;
    private ArrayList<HashMap<String, String>> mFriendsArray;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment NewsfeedFragment.
     */


    public static LeaderBoardFragment newInstance(int sectionNumber) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LeaderBoardFragment() {
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
                    .replace(R.id.leaderboard_fragment, new ListFragment()).commit();

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leader_board, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();

        final int userId = PreferenceManager.getDefaultSharedPreferences(
                getActivity()).getInt(Globals.USER_ID, -1);

        mySwitch = (Switch) getView().findViewById(R.id.leaderboard_switch_id);

        //check the current state before we display the screen
        if(mySwitch.isChecked()){
            mUrl =  getString(R.string.sportstat_url) + "users.json";
        }
        else {
            mUrl = getString(R.string.sportstat_url) + "users/" + userId
                    + "/following.json";
        }


        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    mUrl =  getString(R.string.sportstat_url) + "users.json";
                    Log.d(TAG, "AAAAH");

                }else{
                   mUrl = getString(R.string.sportstat_url) + "users/" + userId
                           + "/following.json";
                }
                getNewData();

            }
        });



        if (userId == -1) {
            Log.e(getActivity().getLocalClassName(), "preference error");
            return;
        }

        getNewData();


    }



    private void updateViewUsingJSONArray(JSONArray friends) {
        ArrayList<BasketballGame> feed = new ArrayList<>();
        mFriendsArray = new ArrayList<>();

        for (int i = 0; i < friends.length(); i++) {
            try {
                JSONObject friend = friends.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(KEY_USERNAME, friend.getString("username"));
                map.put(KEY_ID, ""+friend.getInt("id"));



                JSONArray usersGames = friend.getJSONArray("games");
                for(int k = 0; k<usersGames.length(); k++){
                    feed.add (
                            BasketballGame.getBasketballGameFromJSONObject(
                                    usersGames.getJSONObject(k)));

                    //Log.d(TAG, "CCC: lb: "+feed.get(k).getmImageIdentifier());
                }

                map.put(KEY_THUMB_URL,friend.getString("avatar"));

               

                map.put(KEY_STATSCORE, findStatScore(feed));

                mFriendsArray.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        updateViewLeaderBoard(mFriendsArray);
    }

    public class CustomComparator implements Comparator<HashMap<String, String>> {
        @Override
        public int compare(HashMap m1, HashMap m2) {
            return comparison((String)m1.get(KEY_STATSCORE), (String)(m2.get(KEY_STATSCORE)));
        }
    }

    private int comparison(String stat1, String stat2) {
        double stat1int = Double.parseDouble(stat1);
        double stat2int = Double.parseDouble(stat2);
        int retInt = 0;
        if(stat1int>stat2int) retInt=-1;
        else if(stat2int>stat1int) retInt= 1;
        else if(stat1int==stat2int) retInt= 0;

        return retInt;
    }

    //Takes an ArrayList of BasketBallGame objects and updates the listview
    private void updateViewLeaderBoard(ArrayList<HashMap<String, String>> friends) {

        ArrayList<HashMap<String, String>> sortedFeed = new ArrayList<>();
        sortedFeed.addAll(friends);
        Collections.sort(sortedFeed, new CustomComparator());




        mAdapter = new LazyAdapter(this.getActivity(),sortedFeed, false, true, getActivity());



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

        Intent intent = new Intent(this.getActivity(), FriendViewActivity.class);
        HashMap<String, String> selectedItem = mFriendsArray.get(position);

        intent.putExtra(FriendViewActivity.USERNAME,selectedItem.get(KEY_USERNAME));
        Log.d(TAG, selectedItem.get(KEY_ID));
        intent.putExtra(FriendViewActivity.USER_ID, selectedItem.get(KEY_ID));

        startActivity(intent);

    }


    //Take the array of basketball games stored in the user's history and calculate StatScore
    public static String findStatScore(ArrayList<BasketballGame> mBasketballGames) {

        double avgAssists, avgTwos, avgThrees;

        int assistsSum = 0;
        int twosSum = 0;
        int threesSum = 0;
        int count = 0;
        //TextView avgTextView = (TextView) getView().findViewById(R.id.avg_stats_text_view);

        for(BasketballGame game : mBasketballGames){
            count++;
            assistsSum+=game.getAssists();
            twosSum+=game.getTwoPoints();
            threesSum+=game.getThreePoints();
        }

        //Log.d(TAG, "AAA: LeaderBoard NUMBERS: "+assistsSum+" two's" +twosSum+" threes: "+threesSum);

        avgAssists = assistsSum/((double)count);
        avgTwos = twosSum/((double)count);
        avgThrees = threesSum/((double)count);

        //Log.d(TAG, "AAA: AVG LeaderBoard NUMBERS: "+avgAssists+" two's" +avgTwos+" threes: "+avgThrees);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");


        String statScore = decimalFormat.format(avgAssists+avgTwos+avgThrees);

        return statScore;
    }


    //Create a new async task to populate the leaderboard
    public void getNewData(){

        final Handler handler = new Handler(Looper.getMainLooper());
        mRefreshView = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String resultString = CloudUtilities.getJSON(
                        mUrl);


                //This get's users friends^
                Log.d(getActivity().getLocalClassName(), resultString);

                try {
                    final JSONArray leaderfeed = new JSONArray(resultString);

                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    updateViewUsingJSONArray(leaderfeed);
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



    @Override
    public void onDetach() {
        super.onDetach();
    }

}

