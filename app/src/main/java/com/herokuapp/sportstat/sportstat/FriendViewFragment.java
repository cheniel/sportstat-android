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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.herokuapp.sportstat.sportstat.view.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendViewFragment extends Fragment implements StatsFragment.OnFragmentInteractionListener {


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
    private int mStatScore;

    private static final String ARG_SECTION_NUMBER = "section_number";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    public static FriendViewFragment newInstance(int sectionNumber) {
        FriendViewFragment fragment = new FriendViewFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendViewFragment() {
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        ImageView imageView = (ImageView) getView().findViewById(R.id.profile_image_view);
        imageView.setImageResource(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(Globals.USER_PROFILE_IMG_ID, 99));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 300);

        imageView.setLayoutParams(params);
        TextView textView = (TextView) getView().findViewById(R.id.profile_text_edit);

        String linesep = System.getProperty("line.separator");

        String userName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Globals.USERNAME, "");


        textView.setText(userName+linesep+"StatScore: "+mStatScore);

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

                                    histFrag.updateView(mBasketballGames, getActivity());
                                    statFrag.updateStats(mBasketballGames, getActivity());
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
