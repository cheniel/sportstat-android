package com.herokuapp.sportstat.sportstat;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    // the other fragments
    FragmentManager mFragmentManager;
    private FragmentStartGame mFragmentStartGame;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getFragmentManager();
        mFragmentStartGame = FragmentStartGame.newInstance(NavigationDrawerFragment.START_TAB_ID);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.Start_Game_Tab);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
            }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position + 1) {
            case (NavigationDrawerFragment.START_TAB_ID):
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentStartGame)
                        .commit();
                break;

            default:
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
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
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonPressed(View v) {
        int id = v.getId();

        switch (id){
            case R.id.menu_action_add:
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mFragmentStartGame)
                        .commit();
                break;
            case R.id.Start_New_Basketball_Game_Button:
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, new FragmentStartBasketballGame())
                        .commit();
                break;
            default:
                Toast.makeText(this, "Button not recognized", Toast.LENGTH_SHORT);
                break;
        }
    }

    public void onMenuItemPressed(MenuItem item){
        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFragmentStartGame)
                .commit();
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

}
