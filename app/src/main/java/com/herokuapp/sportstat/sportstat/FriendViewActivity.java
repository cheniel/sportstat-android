package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herokuapp.sportstat.sportstat.CustomListResources.LazyAdapter;
import com.herokuapp.sportstat.sportstat.view.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class FriendViewActivity extends Activity implements StatsFragment.OnFragmentInteractionListener {

    private static final String TAG = "tag";
    public static final String USERNAME = "EXTRA USER NAME";
    public static final String USER_ID = "EXTRA USER ID";
    public static final String IMG_ID = "img";
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
    private int mRelationShipId;
    private String mImageID;


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
            mImageID = extras.getString(IMG_ID, "9");
            if (mPassedUserId == -1) {
                Log.d(TAG, "failed to get user id from intent");
                Toast.makeText(this, "User Not Found", Toast.LENGTH_SHORT);
                mPassedUserId = mCurrentUserId;
            } else {
                amAlreadyFollowing(mCurrentUserId, mPassedUserId);
            }
            mUserName = extras.getString(USERNAME, null);
        }

        // int id = getResources().getIdentifier("res:drawable/blank_profile.gif", null, null);

        ImageView imageView = (ImageView) findViewById(R.id.profile_image_view);
        if(mImageID!=null && !mImageID.equals("null")){
            LazyAdapter.setImage(Integer.parseInt(mImageID), imageView);
        }else {
            imageView.setImageResource(R.drawable.blank_profile);
        }

        TextView textView = (TextView) findViewById(R.id.profile_text_edit);
        mFollowButton = (Button) findViewById(R.id.button_follow_user);

        if (mCurrentUserId != mPassedUserId) {
            mFollowButton.setVisibility(View.VISIBLE);
            mFollowButton.setEnabled(true);
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
                        unFollowUser(mRelationShipId);
                    }
                }
            });
        }

        String linesep = System.getProperty("line.separator");

        try{
            Integer.parseInt(mStatScore);
        }catch (Exception e){

            mStatScore = "0";
        }
        textView.setText(mUserName+linesep+"StatScore: "+mStatScore);


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

    @Override
    protected void onResume() {
        super.onResume();

        int uid;
        if (mPassedUserId == mCurrentUserId) {
            uid = mCurrentUserId;
            Log.d(TAG, "PASS ID: " + mPassedUserId);
        } else {
            uid = mPassedUserId;
            Log.d(TAG, "PASS ID: " + mPassedUserId);
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

                Log.d(TAG, "BBB: FV ID: "+basketballObject.getInt("id"));
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
            avgShotsMade = ((twosSum + threesSum) / attempts) * 100;
            avgTextView.setText("Avg Assists: " + decimalFormat.format(avgAssists) + linesep + "Avg 2-Pointer's: "
                    + decimalFormat.format(avgTwos) + linesep + "Avg 3-Pointer's: " + decimalFormat.format(avgThrees)
                    + linesep + "Average Shots Made: " + decimalFormat.format(avgShotsMade) + "%");
        } else {
            avgTextView.setText("Avg Assists: " + decimalFormat.format(avgAssists) + linesep + "Avg 2-Pointer's: "
                    + decimalFormat.format(avgTwos) + linesep + "Avg 3-Pointer's: " + decimalFormat.format(avgThrees));
        }
    }

    private void addFriendToFriendList(int addUserId, final int toUserId) {
        // create post object
        final JSONObject post = new JSONObject();
        try {
            post.put("follower_id", toUserId);
            post.put("following_id", addUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String registrationResponseString = CloudUtilities.post(
                        getString(R.string.sportstat_url)
                                + "users/" + toUserId + "/user_relationships.json", post);

                Log.d(getLocalClassName(), registrationResponseString);

                try {
                    JSONObject registrationResponse = new JSONObject(registrationResponseString);

                    if (registrationResponse.has("status")) {
                        makeToast("Follow failed.");
                        return "failure";
                    }

                    if (registrationResponse.has("id")){
                        mRelationShipId = registrationResponse.getInt("id");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "success";
            }

            private void makeToast(final String toast) {
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }.execute();
    }

    private void amAlreadyFollowing(final int myId, final int potentialId) {
        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String registrationResponseString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url)
                                + "relationship/" + myId + "/" + potentialId + ".json");

                Log.d(getLocalClassName(), registrationResponseString);

                try {
                    JSONObject registrationResponse = new JSONObject(registrationResponseString);

                    if (registrationResponse.has("found")) {
                        isAlreadyFollowing = registrationResponse.getBoolean("found");
                        if (isAlreadyFollowing){
                            mRelationShipId = registrationResponse.getInt("id");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mFollowButton.setText("Unfollow");
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mFollowButton.setText("Follow");
                                }
                            });
                        }
                        return "success";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "failure";
            }
        }.execute();
    }

    private void unFollowUser(final int relationshipId) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                CloudUtilities.delete(getString(R.string.sportstat_url) + "user_relationships/" +
                                relationshipId + ".json");

                return "success";
            }
        }.execute();
    }
}
