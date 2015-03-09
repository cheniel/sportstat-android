package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.herokuapp.sportstat.sportstat.view.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class FriendViewActivity extends Activity implements StatsFragment.OnFragmentInteractionListener {

    private static final String TAG = "tag";
    public static final String USERNAME = "EXTRA USER NAME";
    public static final String USER_ID = "EXTRA USER ID";
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;
    private Button mFollowButton;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private HistoryFragment histFrag = null;
    private StatsFragment statFrag = null;

    private String regid;
    private IntentFilter mMessageIntentFilter;
    private ArrayList<BasketballGame> mBasketballGames;
    private String mStatScore;
    private int mPassedUserId;
    private String mUserName;
    private int mCurrentUserId;
    private boolean isAlreadyFollowing = false;


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_view);


        mUserName = PreferenceManager.getDefaultSharedPreferences(this).getString(Globals.USERNAME, "");


        mCurrentUserId = PreferenceManager.getDefaultSharedPreferences(this).getInt(Globals.USER_ID, -1);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPassedUserId = extras.getInt(USER_ID, -1);
            if (mPassedUserId == -1){
                Log.d(TAG, "failed to get user id from intent");
                Toast.makeText(this, "User Not Found", Toast.LENGTH_SHORT);
            }
            mUserName = extras.getString(USERNAME, null);
        }

        // int id = getResources().getIdentifier("res:drawable/blank_profile.gif", null, null);

        ImageView imageView = (ImageView) findViewById(R.id.profile_image_view);
        imageView.setImageResource(PreferenceManager.getDefaultSharedPreferences(this).getInt(Globals.USER_PROFILE_IMG_ID, 99));


        TextView textView = (TextView) findViewById(R.id.profile_text_edit);
        mFollowButton = (Button) findViewById(R.id.button_follow_user);

        if (mCurrentUserId != mPassedUserId) {
            mFollowButton.setVisibility(View.VISIBLE);
            mFollowButton.setEnabled(true);

            mFollowButton.setText("Follow");

            mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isAlreadyFollowing) {
                        mFollowButton.setText("Unfollow");
                        isAlreadyFollowing = true;
                        addFriendToFriendList(mPassedUserId, mCurrentUserId);
                    } else {
                        mFollowButton.setText("Follow");
                        isAlreadyFollowing = false;
                    }
                }
            });
        }

        String linesep = System.getProperty("line.separator");

        textView.setText(mUserName + linesep + "StatScore: " + mStatScore);


        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        // Get their instances.
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        histFrag = new HistoryFragment();
        statFrag = new StatsFragment();

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(statFrag);
        fragments.add(histFrag);


        // use FragmentPagerAdapter to bind the slidingTabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter = new ActionTabsViewPagerAdapter(getFragmentManager(),
                fragments);
        viewPager.setAdapter(myViewPageAdapter);

        // make sure the tabs are equally spaced.
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        //Make sure that users' unit preference changes are reflected when switching back
        //to the history tab
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

    }

    private void addFriendToFriendList(int addUserId, int toUserId) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        int uid;
        if (mPassedUserId == mCurrentUserId) {
            uid = mCurrentUserId;
        } else {
            uid = mPassedUserId;
            Log.d(TAG, "ID: " + mPassedUserId);
        }

        final int userId = uid;

        if (userId == -1) {
            Log.e(getLocalClassName(), "preference error");
            return;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String newsfeedString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "users/" + userId
                                + "/basketball_games.json");

                Log.d(getLocalClassName(), newsfeedString);

                try {
                    final JSONArray newsfeed = new JSONArray(newsfeedString);

                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mBasketballGames = getBasketballGameListFromJSONArray(newsfeed);

                                    displayAverages(mBasketballGames);

                                    TextView textView = (TextView) findViewById(R.id.profile_text_edit);

                                    String linesep = System.getProperty("line.separator");


                                    textView.setText(mUserName + linesep + "StatScore: " + mStatScore);

                                    if (mBasketballGames.size() == 0) {
                                        histFrag.updateView(mBasketballGames, getApplicationContext(), false);
                                        statFrag.updateStats(mBasketballGames, getApplicationContext(), false);
                                    } else {

                                        histFrag.updateView(mBasketballGames, getApplicationContext(), true);
                                        statFrag.updateStats(mBasketballGames, getApplicationContext(), true);
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

    private ArrayList<BasketballGame> getBasketballGameListFromJSONArray(JSONArray newsfeed) {
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

        return feed;
    }

    private void displayAverages(ArrayList<BasketballGame> mBasketballGames) {

        double avgAssists, avgTwos, avgThrees, avgShotsMade = 0;

        int assistsSum = 0;
        int twosSum = 0;
        int threesSum = 0;
        int attempts = 0;
        int count = 0;
        TextView avgTextView = (TextView) findViewById(R.id.avg_stats_text_view);

        for (BasketballGame game : mBasketballGames) {
            count++;
            assistsSum += game.getAssists();
            twosSum += game.getTwoPoints();
            threesSum += game.getThreePoints();
            attempts += game.getShotsAttempted();
        }

        avgAssists = assistsSum / ((double) count);
        avgTwos = twosSum / ((double) count);
        avgThrees = threesSum / ((double) count);
        if (attempts != 0) {
            avgShotsMade = ((twosSum + threesSum) / attempts) * 100;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        mStatScore = decimalFormat.format(avgAssists + avgTwos + avgThrees);

        String linesep = System.getProperty("line.separator");
        avgTextView.setText("Avg Assists: " + decimalFormat.format(avgAssists) + linesep + "Avg 2-Pointer's: "
                + decimalFormat.format(avgTwos) + linesep + "Avg 3-Pointer's: " + decimalFormat.format(avgThrees)
                + linesep + "Average Shots Made: " + decimalFormat.format(avgShotsMade) + "%");

    }
}
