package com.herokuapp.sportstat.sportstat;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "a";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    // the other fragments
    FragmentManager mFragmentManager;
    private StartGameFragment mFragmentStartGame;
    private LogGameFragment mFragmentLogGame;
    private SettingsFragment mFragmentSettings;
    private NewsfeedFragment mFragmentNewsfeed;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private boolean itemSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "on create callllle");

        mFragmentManager = getFragmentManager();
        mFragmentStartGame = StartGameFragment.newInstance(NavigationDrawerFragment.START_TAB_ID);
        mFragmentLogGame = LogGameFragment.newInstance(NavigationDrawerFragment.LOG_TAB_ID);
        mFragmentNewsfeed = NewsfeedFragment.newInstance(NavigationDrawerFragment.NEWSFEED_TAB_ID);
        mFragmentSettings = SettingsFragment.newInstance(NavigationDrawerFragment.SETTINGS_TAB_ID);

        setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        Log.d(TAG, "AAA: nav drawer sel"+position);
        itemSelected = true;

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
                onSectionAttached(NavigationDrawerFragment.SETTINGS_TAB_ID);
                break;

            case (NavigationDrawerFragment.NEWSFEED_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentNewsfeed)
                        .commit();
                onSectionAttached(NavigationDrawerFragment.NEWSFEED_TAB_ID);
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
            Log.d(TAG, "AAA: sectionAtteched " + number);
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
                    Log.d(TAG, "attached: " + getString(R.string.Newsfeed_Tab));
                    mTitle = getString(R.string.Newsfeed_Tab);
                    break;
                case NavigationDrawerFragment.LEADERBOARD_TAB_ID:
                    mTitle = getString(R.string.Leaderboard_Tab);
                    break;
                case NavigationDrawerFragment.SETTINGS_TAB_ID:
                    mTitle = getString(R.string.Settings_Tab);
                    break;
            }
            restoreActionBar();
        }
        itemSelected = false;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        Log.d(TAG, "AAA: TITLE: "+mTitle);
        actionBar.setTitle(mTitle);
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
        LogGameFragment.onSaveClicked(v);

        getFragmentManager().beginTransaction().remove(mFragmentLogGame).commit();

        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFragmentNewsfeed)
                .commit();
        onSectionAttached(NavigationDrawerFragment.LOG_TAB_ID);

    }

    //On save and on cancel clicked methods for LogGameFragment
    public void onCancelClicked(View v){
        LogGameFragment.onCancelClicked(v);
    }

}
