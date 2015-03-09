package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
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
import android.widget.TextView;

import com.herokuapp.sportstat.sportstat.CustomListResources.LazyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 *.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String BASKETBALL_GAME = "game";
    private ArrayList<BasketballGame> mGamesArray;

    private int mSectionNumber;
    private ArrayAdapter<BasketballGame> defAdapter;

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

    private boolean mIsEntries;


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




        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }


    //Takes an ArrayList of BasketBallGame objects and updates the listview
    public void updateView(ArrayList<BasketballGame> gamesArray, Context context, boolean isEntries) {

        mIsEntries = isEntries;


        if(mIsEntries) {


            ArrayList<HashMap<String, String>> games = new ArrayList<>();
            mGamesArray = gamesArray;

            for (int i = gamesArray.size()-1; i >=0; i--) {

                BasketballGame game = gamesArray.get(i);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_ID, "" + game.getUserId()); //Stores user ID as string...

                String capUserName = game.getUsername().substring(0, 1).toUpperCase()
                        + game.getUsername().substring(1, game.getUsername().length());

                map.put(KEY_USERNAME, capUserName);
                map.put(KEY_TITLE, game.toStringForHistory());
                map.put(KEY_ASSISTS, " " + game.getAssists() + " ");
                map.put(KEY_TWOS, " " + game.getTwoPoints() + " ");
                map.put(KEY_THREES, " " + game.getThreePoints() + " ");
                //map.put(KEY_ARTIST, game.getLocation());
                //map.put(KEY_DURATION, game.get)
                // map.put(KEY_THUMB_URL, (user profile link))

                games.add(map);

            }


            LazyAdapter adapter = new LazyAdapter(this.getActivity(), games, false, false, getActivity());


            //defAdapter = new ArrayAdapter<BasketballGame>(this.getActivity(), R.layout.plain_textview, gamesArray);
            setListAdapter(adapter);

        }else{

        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        Intent intent = new Intent(this.getActivity(), GameSummaryActivity.class);

        intent.putExtra(SportLoggingActivity.BASKETBALL_GAME, mGamesArray.get(position));
        intent.putExtra(SportLoggingActivity.CALLING_ACTIVITY, "history_fragment");

        startActivity(intent);
//
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

