package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.herokuapp.sportstat.sportstat.CustomListResources.LazyAdapter;
import com.herokuapp.sportstat.sportstat.view.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment implements StatsFragment.OnFragmentInteractionListener {


    private static final String TAG = "tag";
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private HistoryFragment histFrag = null;
    private StatsFragment statFrag = null;

    private String regid;
    private IntentFilter mMessageIntentFilter;
    private ArrayList<BasketballGame> mBasketballGames;
    private String mStatScore;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_DISPLAYED_USER_ID = "displayed_user_id";

    private OnFragmentInteractionListener mListener;
    private int mSectionNumber;


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment FriendViewFragment.
     */

    public static MyProfileFragment newInstance(int sectionNumber) {
        MyProfileFragment fragment = new MyProfileFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MyProfileFragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_view, container, false);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        ImageView imageView = (ImageView) getView().findViewById(R.id.profile_image_view);
        int profilePicId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(Globals.USER_PROFILE_IMG_ID, 99);
        LazyAdapter.setImage(profilePicId, imageView);



        TextView textView = (TextView) getView().findViewById(R.id.profile_text_edit);
        String linesep = System.getProperty("line.separator");
        String userName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Globals.USERNAME, "");




        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        // Get their instances.
        slidingTabLayout = (SlidingTabLayout) getView().findViewById(R.id.tab);
        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);

        histFrag = new HistoryFragment();
        statFrag = new StatsFragment();


        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(statFrag);
        fragments.add(histFrag);


        // use FragmentPagerAdapter to bind the slidingTabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter = new ActionTabsViewPagerAdapter(getChildFragmentManager(),
                fragments);
        viewPager.setAdapter(myViewPageAdapter);

        // make sure the tabs are equally spaced.
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);


        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (fragments.get(position).equals(histFrag)) {
                    //histFrag.updateView();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });


        final int userId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(Globals.USER_ID, -1);

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
                                    mBasketballGames = getBasketballGameListFromJSONArray(newsfeed);

                                    displayAverages(mBasketballGames);

                                    TextView textView = (TextView) getView().findViewById(R.id.profile_text_edit);
                                    String linesep = System.getProperty("line.separator");
                                    String userName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Globals.USERNAME, "");

                                    try{
                                        Double.parseDouble(mStatScore);
                                    }catch (Exception e){

                                        mStatScore = "0";
                                    }
                                    textView.setText(userName+linesep+"StatScore: "+mStatScore);

                                    if(mBasketballGames.size()==0){
                                        histFrag.updateView(mBasketballGames, getActivity(), false);
                                        statFrag.updateStats(mBasketballGames, getActivity(), false);
                                        slidingTabLayout.setVisibility(View.INVISIBLE);
                                    }else{
                                        slidingTabLayout.setVisibility(View.VISIBLE);
                                        histFrag.updateView(mBasketballGames, getActivity(), true);
                                        statFrag.updateStats(mBasketballGames, getActivity(), true);
                                    }


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

    //Method to display average Two's, Three's, Assists', and shots-made percentages to the screen
    private void displayAverages(ArrayList<BasketballGame> mBasketballGames) {

        double avgAssists, avgTwos, avgThrees, avgShotsMade = 0;

        int assistsSum = 0;
        int twosSum = 0;
        int threesSum = 0;
        int attempts = 0;
        int count = 0;

        int twosSumShotsPct = 0;
        int threeSumShotsPct = 0;

        TextView avgTextView = (TextView) getView().findViewById(R.id.avg_stats_text_view);

        for (BasketballGame game : mBasketballGames) {
            count++;
            assistsSum += game.getAssists();
            twosSum += game.getTwoPoints();
            threesSum += game.getThreePoints();
            attempts += game.getShotsAttempted();
            if(game.getShotsAttempted()>0){
                twosSumShotsPct+=game.getTwoPoints();
                threeSumShotsPct+=game.getThreePoints();
            }
        }

        Log.d(TAG, "AAA: FriendView sums:"+assistsSum+" "+twosSum+" "+threesSum );

        avgAssists = assistsSum / ((double) count);
        avgTwos = twosSum / ((double) count);
        avgThrees = threesSum / ((double) count);


        if((""+avgAssists).equals("NaN")){
            avgAssists = 0;
        }
        if((""+avgTwos).equals("NaN")){
            avgTwos = 0;
        }

        if((""+avgThrees).equals("NaN")){
            avgThrees = 0;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String linesep = System.getProperty("line.separator");

        mStatScore = decimalFormat.format(avgAssists + avgTwos + avgThrees);
        if (attempts > 0) {
            avgShotsMade = ((twosSumShotsPct+threeSumShotsPct) / (double)attempts) * 100;
            avgTextView.setText("Avg Assists: " + decimalFormat.format(avgAssists) + linesep + "Avg 2-Pointer's: "
                    + decimalFormat.format(avgTwos) + linesep + "Avg 3-Pointer's: " + decimalFormat.format(avgThrees)
                    + linesep + "Average Shots Made: " + decimalFormat.format(avgShotsMade) + "%");
        } else {
            avgTextView.setText("Avg Assists: " + decimalFormat.format(avgAssists) + linesep + "Avg 2-Pointer's: "
                    + decimalFormat.format(avgTwos) + linesep + "Avg 3-Pointer's: " + decimalFormat.format(avgThrees));
        }
    }

    private ArrayList<BasketballGame> getBasketballGameListFromJSONArray(JSONArray newsfeed) {
        ArrayList<BasketballGame> feed = new ArrayList<>();

        for (int i = 0; i < newsfeed.length(); i++) {
            try {
                JSONObject basketballObject = newsfeed.getJSONObject(i);
                feed.add(feed.size()-i,
                        BasketballGame.getBasketballGameFromJSONObject(
                                basketballObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return feed;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }

}
