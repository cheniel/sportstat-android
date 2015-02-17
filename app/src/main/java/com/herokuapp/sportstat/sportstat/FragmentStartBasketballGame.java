package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 *
 * Use the {@link FragmentStartBasketballGame#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStartBasketballGame extends Fragment {

    private GameBasketball mGame;

    private TextView mAssistsView;
    private TextView mTwoPointsView;
    private TextView mThreePointsView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentStartGame.
     */
    public static FragmentStartBasketballGame newInstance(String param2) {
        FragmentStartBasketballGame fragment = new FragmentStartBasketballGame();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentStartBasketballGame() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGame = new GameBasketball();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_basketball_game, container, false);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAssistsView = (TextView)
                getActivity().findViewById(R.id.new_basketball_game_assist_text_view);
        mTwoPointsView = (TextView)
                getActivity().findViewById(R.id.new_basketball_game_two_point_text_view);
        mThreePointsView = (TextView)
                getActivity().findViewById(R.id.new_basketball_game_three_point_text_view);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void increaseAssists(){
        mGame.setAssists(mGame.getAssists() + 1);
        mAssistsView.setText(String.valueOf(mGame.getAssists()));
    }

    public void increaseTwoPoints(){
        mGame.setTwoPoints(mGame.getTwoPoints() + 1);
        mTwoPointsView.setText(String.valueOf(mGame.getTwoPoints()));
    }

    public void increaseThreePoints(){
        mGame.setThreePoints(mGame.getThreePoints() + 1);
        mThreePointsView.setText(String.valueOf(mGame.getThreePoints()));
    }

}
