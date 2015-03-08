package com.herokuapp.sportstat.sportstat;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.herokuapp.sportstat.sportstat.CustomListResources.CustomDrawerAdapter;
import com.herokuapp.sportstat.sportstat.CustomListResources.DrawerItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FriendViewFragment.OnFragmentInteractionListener {

    private static final String TAG = "a";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    // the other fragments
    FragmentManager mFragmentManager;
    private StartGameFragment mFragmentStartGame;
    private LogGameFragment mFragmentLogGame;
    private FriendViewFragment mFragmentFriendView;
    private SettingsFragment mFragmentSettings;
    private NewsfeedFragment mFragmentNewsfeed;
    private FriendFinderFragment mFragmentFriendFinder;
    private LeaderBoardFragment mFragmentLeaderBoard;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;

    CustomDrawerAdapter adapter;

    List<DrawerItem> dataList;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private boolean itemSelected = false;
    private boolean profileOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "on create callllle");

        mFragmentManager = getFragmentManager();
        mFragmentStartGame = StartGameFragment.newInstance(NavigationDrawerFragment.START_TAB_ID);
        mFragmentLogGame = LogGameFragment.newInstance(NavigationDrawerFragment.LOG_TAB_ID);
        mFragmentFriendView = FriendViewFragment.newInstance(NavigationDrawerFragment.PROFILE_TAB_ID);
        mFragmentNewsfeed = NewsfeedFragment.newInstance(NavigationDrawerFragment.NEWSFEED_TAB_ID);
        mFragmentSettings = SettingsFragment.newInstance(NavigationDrawerFragment.SETTINGS_TAB_ID);
        mFragmentFriendFinder = FriendFinderFragment.newInstance(NavigationDrawerFragment.FRIEND_TAB_ID);
        mFragmentLeaderBoard = LeaderBoardFragment.newInstance(NavigationDrawerFragment.LEADERBOARD_TAB_ID);

        setContentView(R.layout.activity_main);

//        dataList = new ArrayList<DrawerItem>();
//        mTitle = mDrawerTitle = getTitle();
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.drawer_list_view);
//
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
//                GravityCompat.START);
//
//
//        dataList.add(new DrawerItem("Message", R.drawable.ic_action_email));
//        dataList.add(new DrawerItem("Likes", R.drawable.ic_action_good));
//        dataList.add(new DrawerItem("Games", R.drawable.ic_action_gamepad));
//        dataList.add(new DrawerItem("Lables", R.drawable.ic_action_labels));
//        dataList.add(new DrawerItem("Search", R.drawable.ic_action_search));
//        dataList.add(new DrawerItem("Cloud", R.drawable.ic_action_cloud));
//        dataList.add(new DrawerItem("Camara", R.drawable.ic_action_camera));
//        dataList.add(new DrawerItem("Video", R.drawable.ic_action_video));
//        dataList.add(new DrawerItem("Groups", R.drawable.ic_action_group));
//        dataList.add(new DrawerItem("Import & Export",
//                R.drawable.ic_action_import_export));
//        dataList.add(new DrawerItem("About", R.drawable.ic_action_about));
//        dataList.add(new DrawerItem("Settings", R.drawable.ic_action_settings));
//        dataList.add(new DrawerItem("Help", R.drawable.ic_action_help));

//
//        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
//                dataList);
//
//        mDrawerList.setAdapter(adapter);



        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

//        // Set up the drawer.

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments


        itemSelected = true;

        profileOpen = position == NavigationDrawerFragment.PROFILE_TAB_ID;
        switch (position) {
            case (NavigationDrawerFragment.START_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentStartGame)
                        .commit();
                onSectionAttached(NavigationDrawerFragment.START_TAB_ID);
                break;

            case (NavigationDrawerFragment.LOG_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentLogGame)
                        .commit();
                onSectionAttached(NavigationDrawerFragment.LOG_TAB_ID);
                break;

            case (NavigationDrawerFragment.SETTINGS_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentSettings)
                        .commit();
                SettingsFragment.getFromSharedPrefs = true;
                onSectionAttached(NavigationDrawerFragment.SETTINGS_TAB_ID);

                break;

            case (NavigationDrawerFragment.NEWSFEED_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentNewsfeed)
                        .commit();
                onSectionAttached(NavigationDrawerFragment.NEWSFEED_TAB_ID);
                break;

            case (NavigationDrawerFragment.PROFILE_TAB_ID):
//                Intent i = new Intent(this, FriendViewActivity.class);
//                startActivity(i);
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentFriendView)
                        .commit();


                onSectionAttached(NavigationDrawerFragment.PROFILE_TAB_ID);
                break;

            case (NavigationDrawerFragment.FRIEND_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentFriendFinder)
                        .commit();
                onSectionAttached(NavigationDrawerFragment.FRIEND_TAB_ID);
                break;

            case (NavigationDrawerFragment.LEADERBOARD_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentLeaderBoard)
                        .commit();
                onSectionAttached(NavigationDrawerFragment.LEADERBOARD_TAB_ID);
                break;

            default:
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position))
                        .commit();
                break;
        }

        restoreActionBar();
    }

    public void onSectionAttached(int number) {

        if(itemSelected) {

            switch (number) {
                case NavigationDrawerFragment.START_TAB_ID:
                    mTitle = getString(R.string.Start_Game_Tab);
                    break;
                case NavigationDrawerFragment.LOG_TAB_ID:
                    mTitle = getString(R.string.Log_Game_Tab);
                    break;
                case NavigationDrawerFragment.PROFILE_TAB_ID:
                    mTitle = getString(R.string.My_Profile_Tab);
                    break;
                case NavigationDrawerFragment.NEWSFEED_TAB_ID:
                    mTitle = getString(R.string.Newsfeed_Tab);
                    break;
                case NavigationDrawerFragment.LEADERBOARD_TAB_ID:
                    mTitle = getString(R.string.Leaderboard_Tab);
                    break;
                case NavigationDrawerFragment.SETTINGS_TAB_ID:
                    mTitle = getString(R.string.Settings_Tab);
                    break;
                case NavigationDrawerFragment.FRIEND_TAB_ID:
                    mTitle = getString(R.string.Friend_Tab);
                    break;
            }
            restoreActionBar();
        }
        itemSelected = false;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void onMenuItemPressed(MenuItem item){
        mNavigationDrawerFragment.selectItem(NavigationDrawerFragment.START_TAB_ID);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void startSportLoggingActivity(View view) {
        Intent intent = new Intent(".activities.SportLoggingActivity");
        startActivity(intent);
    }

    //On save and on cancel clicked methods for LogGameFragment
    public void onSaveClicked(View v){
        LogGameFragment.onSaveClicked(v, this);

        getFragmentManager().beginTransaction().remove(mFragmentLogGame).commit();

        returnToNewsfeed();

    }

    public void onCancelClicked(View v){
        LogGameFragment.onCancelClicked(v);
    }

    //OnSave, OnCancel, and OnChangePhoto clicked methods for Settings Fragment

    public void onSettingsSaveClicked(View v){
        SettingsFragment.onSaveClicked(v, this);

        returnToNewsfeed();
    }

    public void onSettingsCancelClicked(View v){
        SettingsFragment.onCancelClicked(v, this.getApplicationContext());

        returnToNewsfeed();
    }

    public void onChangePhotoClicked(View v){
        SettingsFragment.onChangePhotoClicked(v, this);

    }

    //OnSearchClicked methods for FriendFinderFragment
    public void onSearchClicked(View v){
        final String enteredUserName = FriendFinderFragment.mUserSearchEditText.getText().toString();


        if (enteredUserName.isEmpty()) {
            Toast.makeText(this, "Please input username.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String userLookupResponseString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "user_id/" +
                                enteredUserName + ".json");

                Log.d(TAG, userLookupResponseString);

                try {
                    JSONObject userJSON = new JSONObject(userLookupResponseString);

                    if (userJSON.has("status")) {
                        makeToast("Friend does not exist");
                        return "failure";
                    }

                    if (userJSON.has("id")) {
                        makeToast("Friend exists!");

                        Intent intent = new Intent(".activities.FriendViewActivity");
                        intent.putExtra(FriendViewActivity.USER_ID, userJSON.getInt("id"));
                        intent.putExtra(FriendViewActivity.USERNAME, userJSON.getString("username"));
                        startActivity(intent);

                        return "success";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                makeToast("Friend does not exist.");
                return "failure";
            }

            private void makeToast(final String toast) {
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }.execute();
    }


    //OnFragmentInteraction method needed for FriendViewFragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    if(profileOpen) {
        returnToNewsfeed();
    }

        super.onSaveInstanceState(outState);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Globals.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Globals.context = this.getApplicationContext();
    }

    //Helper method to restore the default view
    private void returnToNewsfeed(){
        getFragmentManager().beginTransaction().remove(mFragmentLogGame).commit();

        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFragmentNewsfeed)
                .commit();
        itemSelected = true;
        onSectionAttached(NavigationDrawerFragment.NEWSFEED_TAB_ID);
    }
}
