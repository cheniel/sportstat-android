package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


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
    private ArrayList<String> mFriendsArray;
    private static EditText mUserSearchEditText;


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

        mFriendsArray = new ArrayList<>();
        mFriendsArray.add("Scruffy");
        mFriendsArray.add("Ishmael");

        updateView(mFriendsArray);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_finder, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mUserSearchEditText = (EditText) getView().findViewById(R.id.friend_search_edit_text);



    }

    public static void onSearchClicked(View v, MainActivity act){

        attemptLogin(v, act);




    }




    //Takes an ArrayList of BasketBallGame objects and updates the listview
    private void updateView(ArrayList<String> gamesArray) {


        defAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.plain_textview, gamesArray);
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



    public static void attemptLogin(View view, final MainActivity act) {
        final String enteredUserName = mUserSearchEditText.getText().toString();


        if (enteredUserName.isEmpty()) {
            Toast.makeText(act, "Please input username.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String loginResponseString = CloudUtilities.getJSON(
                        act.getString(R.string.sportstat_url) + "user_id/" +
                                enteredUserName + ".json");

                Log.d(TAG, loginResponseString);

                try {
                    JSONObject loginResponse = new JSONObject(loginResponseString);

                    if (loginResponse.has("status")) {
                        makeToast("Login failed. User may not exist.");
                        return "failure";
                    }

                    if (loginResponse.has("id")) {
                        makeToast("Login successful!");
//                        saveUsernameAndUserId(
//                                loginResponse.getString("username"),
//                                loginResponse.getInt("id")
                        //  );
                        //launchApp();

                        return "success";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                makeToast("Login failed");
                return "failure";
            }

            private void makeToast(final String toast) {
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(th getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }.execute();

    }
}

