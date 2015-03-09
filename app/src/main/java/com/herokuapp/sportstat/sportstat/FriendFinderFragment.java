package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 *.
 * Use the {@link NewsfeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFinderFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "bspray";

    private int mSectionNumber;
    private ArrayAdapter<String> defAdapter;
    private ArrayList<String> mFriendsUsernameArray;
    public static EditText mUserSearchEditText;
    private ArrayList<Integer> mFriendsIdArray;
    private ArrayList<String> mFriendsAvatarArray;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment NewsfeedFragment.
     */


    public static FriendFinderFragment newInstance(int sectionNumber) {
        FriendFinderFragment fragment = new FriendFinderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendFinderFragment() {
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
                    .replace(R.id.friend_finder_fragment, new ListFragment()).commit();

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_finder, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mUserSearchEditText = (EditText) getView().findViewById(R.id.friend_search_edit_text);

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
                String resultString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "users/" + userId
                                + "/following.json");
                mFriendsUsernameArray = new ArrayList<String>();
                mFriendsIdArray = new ArrayList<Integer>();
                mFriendsAvatarArray = new ArrayList<String>();

                try {
                    final JSONArray friends = new JSONArray(resultString);

                    for (int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);

                        mFriendsUsernameArray.add(friend.getString("username"));
                        mFriendsIdArray.add(friend.getInt("id"));
                        mFriendsAvatarArray.add(friend.getString("avatar"));
                    }

                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    updateView(mFriendsUsernameArray);
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


    //Takes an ArrayList of BasketBallGame objects and updates the listview
    private void updateView(ArrayList<String> friendsArray) {
        defAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.plain_textview, friendsArray);
        setListAdapter(defAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    //When user clicks someone's newsfeed entry, bring them to that users' page
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(".activities.FriendViewActivity");
        intent.putExtra(FriendViewActivity.USER_ID, mFriendsIdArray.get(position));
        intent.putExtra(FriendViewActivity.USERNAME, mFriendsUsernameArray.get(position));
        intent.putExtra(FriendViewActivity.IMG_ID, mFriendsAvatarArray.get(position));
        startActivity(intent);

    }

}

