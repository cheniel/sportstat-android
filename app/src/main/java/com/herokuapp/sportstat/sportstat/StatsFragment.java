package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {


    private static final String TAG = "tagStats";
    private View mChart;

    private OnFragmentInteractionListener mListener;
    private ArrayList<BasketballGame> mGamesArray;
    private LinearLayout chartContainer;
    private View mView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openChart(Context c) {

        int[] x = new int[mGamesArray.size()];
        int[] assists = new int[mGamesArray.size()];
        int[] twos = new int[mGamesArray.size()];
        int[] threes = new int[mGamesArray.size()];


        for (int j = 0; j < mGamesArray.size(); j++) {
            x[j] = j;
            assists[j] = mGamesArray.get(j).getAssists();
            twos[j] = mGamesArray.get(j).getTwoPoints();
            threes[j] = mGamesArray.get(j).getThreePoints();
        }


        // Creating an XYSeries for Income
        XYSeries assistSeries = new XYSeries("Assists");
        // Creating an XYSeries for Expense
        XYSeries twoSeries = new XYSeries("2-Points");

        XYSeries threeSeries = new XYSeries("3-Points");

        for (int i = 0; i < x.length; i++) {
            assistSeries.add(i, assists[i]);
            twoSeries.add(i, twos[i]);
            threeSeries.add(i, threes[i]);
        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        dataset.addSeries(assistSeries);
        // Adding Expense Series to dataset
        dataset.addSeries(twoSeries);

        dataset.addSeries(threeSeries);

        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer assistsRenderer = new XYSeriesRenderer();
        assistsRenderer.setColor(Color.BLACK); //color of the graph set to cyan
        assistsRenderer.setFillPoints(true);
        assistsRenderer.setLineWidth(7f);
        assistsRenderer.setDisplayChartValues(true);
        //setting chart value distance
        assistsRenderer.setDisplayChartValuesDistance(10);
        //setting line graph point style to circle
        assistsRenderer.setPointStyle(PointStyle.CIRCLE);
        //setting stroke of the line chart to solid
        assistsRenderer.setStroke(BasicStroke.SOLID);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer twosRenderer = new XYSeriesRenderer();
        twosRenderer.setColor(Color.BLUE);
        twosRenderer.setFillPoints(true);
        twosRenderer.setLineWidth(7f);
        twosRenderer.setDisplayChartValues(true);
        //setting line graph point style to circle
        twosRenderer.setPointStyle(PointStyle.SQUARE);
        //setting stroke of the line chart to solid
        twosRenderer.setStroke(BasicStroke.SOLID);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer threeRenderer = new XYSeriesRenderer();
        twosRenderer.setColor(Color.RED);
        twosRenderer.setFillPoints(true);
        twosRenderer.setLineWidth(7f);
        twosRenderer.setDisplayChartValues(true);
        //setting line graph point style to circle
        twosRenderer.setPointStyle(PointStyle.SQUARE);
        //setting stroke of the line chart to solid
        twosRenderer.setStroke(BasicStroke.SOLID);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle(c.getString(R.string.stats_chart_title));

          multiRenderer.setXTitle("Entries from " + mGamesArray.get(0).getTimeString()
                  + " to " + mGamesArray.get(mGamesArray.size() - 1).getTimeString());


        /***
         * Customizing graphs
         */
        //setting text size of the title
        multiRenderer.setChartTitleTextSize(35);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(32);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(24);

        multiRenderer.setLegendTextSize(30);
        //setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);
        //setting click false on graph
        multiRenderer.setClickEnabled(false);
        //setting zoom to false on both axis
        multiRenderer.setZoomEnabled(false, false);
        //setting lines to display on y axis
        multiRenderer.setShowGridY(true);
        //setting lines to display on x axis
        multiRenderer.setShowGridX(true);
        //setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
        //setting displaying line on grid
        multiRenderer.setShowGrid(true);
        //setting zoom to false
        multiRenderer.setZoomEnabled(false);
        //setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);
        //setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
        //setting to in scroll to false
        multiRenderer.setInScroll(false);
        //setting to set legend height of the graph
        multiRenderer.setLegendHeight(30);
        //setting x axis label align
        multiRenderer.setXLabelsAlign(Align.CENTER);
        //setting y axis label to align
        multiRenderer.setYLabelsAlign(Align.LEFT);
        //setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);

        //setting no of values to display in y axis
        multiRenderer.setYLabels(10);
        // setting y axis max value
        multiRenderer.setYAxisMax(2*Math.max(Math.max(findMax(assists), findMax(twos)), findMax(threes)));
        multiRenderer.setYAxisMin(-Math.max(Math.max(findMax(assists), findMax(twos)), findMax(threes)));
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-0.5);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMax(mGamesArray.size());
        //setting bar size or space between two bars
        //multiRenderer.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(c.getResources().getColor(R.color.transparent_background));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setScale(2f);
        //setting x axis point size
        multiRenderer.setPointSize(4f);
        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{70, 30, 70, 70});

        //        for(int i=0; i< x.length;i++){
        //            multiRenderer.addXTextLabel(i, mMonth[i]);
        //        }

        // Adding assistsRenderer and twosRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(assistsRenderer);
        multiRenderer.addSeriesRenderer(twosRenderer);
        multiRenderer.addSeriesRenderer(threeRenderer);


        //this part is used to display graph on the xml
        //remove any views before u paint the chart
        chartContainer.removeAllViews();
        //drawing bar chart
        mChart = ChartFactory.getLineChartView(getActivity(), dataset, multiRenderer);
        //adding the view to the linearlayout
        chartContainer.addView(mChart);

    }

    //Helper function to find the largest int in an intArray
    private int findMax(int[] ar) {
        int max = ar[0];
        for (int i = 0; i < mGamesArray.size(); i++) {

            if (ar[i] > max) {
                max = ar[i];
            }
        }
        return max;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stats, container, false);
        // Inflate the layout for this fragment

        Log.d(TAG, "onCreateView");
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // mListener = (OnFragmentInteractionListener) this;
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

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onResume called");
        //openChart();
    }

    @Override
    public void onResume() {
        super.onResume();

        chartContainer = (LinearLayout) mView.findViewById(R.id.profile_stats_chart);

    }

    //Update graphs in the StatsFragent
    public void updateStats(ArrayList<BasketballGame> gamesArray, Context context, boolean areEntries) {
        TextView reminder = (TextView) getView().findViewById(R.id.remind_to_log_text_id);
        if(areEntries) {
            mGamesArray = gamesArray;
            reminder.setVisibility(View.INVISIBLE);
            openChart(context);
        }else{
            reminder.setVisibility(View.VISIBLE);
            reminder.setText(context.getString(R.string.remind_to_log));
        }

    }

}
