package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *.
 * Use the {@link NewsfeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsfeedFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int mSectionNumber;
    private ArrayAdapter<BasketballGame> defAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment NewsfeedFragment.
     */


    public static NewsfeedFragment newInstance(int sectionNumber) {
        NewsfeedFragment fragment = new NewsfeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsfeedFragment() {
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
                    .replace(R.id.newsfeed_fragment, new ListFragment()).commit();

        }



        ArrayList<BasketballGame> gamesArray = new ArrayList<>();
        BasketballGame one = new BasketballGame(this.getActivity());
        gamesArray.add(one);


        //Fill the gamesArray with the users friends' saved games in chronological order
        updateView(gamesArray);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    //Takes an ArrayList of BasketBallGame objects and updates the listview
    private void updateView(ArrayList<BasketballGame> gamesArray) {


        defAdapter = new ArrayAdapter<BasketballGame>(this.getActivity(), R.layout.plain_textview, gamesArray);
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

}

