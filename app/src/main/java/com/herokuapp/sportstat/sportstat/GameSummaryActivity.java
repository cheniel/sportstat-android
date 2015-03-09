package com.herokuapp.sportstat.sportstat;


import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.sportstat.sportstat.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Created by John Rigby on 2/26/15
 *
 * Class to display the statistics of a single basketball game.
 *
 * Receives data from a basketball game in the form of an intent, either from
 * SportLoggingActivity ...
 */
//NOTE: Graph implementations are mostly from code found here: http://www.geeks.gallery/android-drawing-bar-chart-graph-using-achartengine-library/#comment-9522
public class GameSummaryActivity extends Activity {

    private static final String TAG = "";
    private int mAssists;
    private int mTwoPoints;
    private int mThreePoints;
    private int mPossessions;
    private double mDistanceRan;
    private long mDuration;
    private int mShotsMade;
    private int mShotsAttempted;
    private String mTime;
    private LatLng mFirstLocation;
    private String mFirstLocationGeocodeString;
    private Geocoder mGeocoder;

    BasketballGame mGame;


    private View mChart;
    private String[] mLabels = new String[] {
            "Assists", "2-Points" , "3-Points", ""
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if(i.getStringExtra(SportLoggingActivity.CALLING_ACTIVITY).equals("sport_logging")) {

            setContentView(R.layout.activity_game_summary);
        }else{
            setContentView(R.layout.activity_game_summary_external);
        }

        mGame = (BasketballGame)i.getSerializableExtra(SportLoggingActivity.BASKETBALL_GAME);

        mAssists = mGame.getAssists();
        mTwoPoints = mGame.getTwoPoints();
        mThreePoints = mGame.getThreePoints();
        mPossessions = mGame.getPossessions();
        mShotsAttempted = mGame.getShotsAttempted();
        mDistanceRan = mGame.getDistance();
        mDuration = mGame.getDuration();

        mFirstLocation = new LatLng(mGame.getFirstLat(), mGame.getFirstLon());
        mGeocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses =
                    mGeocoder.getFromLocation(mFirstLocation.latitude, mFirstLocation.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();

                for (int j = 0; j < address.getMaxAddressLineIndex(); j++)
                    sb.append(address.getAddressLine(j)).append("\n");

                mFirstLocationGeocodeString = sb.toString();
                mGame.setGeocodedString(mFirstLocationGeocodeString);

                Log.d(TAG, "Geocode: "+mFirstLocationGeocodeString);
            }
        } catch (Exception e){
            Log.d(TAG, "couldn't get geocoded location");
            e.printStackTrace();
        }

        String lineSep = System.getProperty("line.separator"); //The lineSep declaration was found on stackoverflow

        TextView titleText = (TextView) findViewById(R.id.game_summary_title_text);
        titleText.setText(mGame.getPrettyTimeForHistory()+lineSep+"@"+mFirstLocationGeocodeString);
        TextView statsText = (TextView) findViewById(R.id.stats_text_view);


        int shotsMade = mTwoPoints+mThreePoints;
        String shotsMadePctStr = "";
        if(mShotsAttempted > 0){
            shotsMadePctStr = ""+(shotsMade/mShotsAttempted)*100+"%";

            statsText.setText("Shots attempted: "+mShotsAttempted+lineSep+"Shots made: "+shotsMade
                    +lineSep+"Shots Made %age: "+shotsMadePctStr+lineSep+"Possessions: "+mPossessions
                    +lineSep+"Distance Ran (m): "+mDistanceRan+lineSep+"Duration of Game: "+formatDuration(mDuration));
        } else {
            statsText.setText("Shots made: "+shotsMade+lineSep+"Possessions: "+mPossessions
                    +lineSep+"Distance Ran (m): "+mDistanceRan+lineSep+"Duration of Game: "
                    +formatDuration(mDuration));
        }

        //Display bargraph
        openChart();

    }

    private void openChart(){
        int[] x = {0,1,2,3};
        int[] barSubstance = {mAssists, mTwoPoints, mThreePoints, 0};
        // int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400, 0, 0, 0, 0 };

        Log.d(TAG, "Assists, twos, threes: " + mAssists + " " + mTwoPoints + " " + mThreePoints);

        // Creating an XYSeries for Income
        XYSeries scoreSeries = new XYSeries("Income");

        // Creating an XYSeries for Expense
        //XYSeries expenseSeries = new XYSeries("Expense");
        // Adding data to Income and Expense Series
        for(int i=0;i<x.length;i++){
            scoreSeries.add(i, barSubstance[i]);
            //expenseSeries.add(i,expense[i]);

        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();


        // Adding Income Series to the dataset
        dataset.addSeries(scoreSeries);
        // Adding Expense Series to dataset
        //dataset.addSeries(expenseSeries);

        // Creating XYSeriesRenderer to customize scoreSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.CYAN); //color of the graph set to cyan
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);
        incomeRenderer.setChartValuesTextAlign(Align.CENTER);
        incomeRenderer.setChartValuesTextSize(50);
        incomeRenderer.setChartValuesSpacing(10);


        //Creating XYSeriesRenderer to customize expenseSeries
//        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
//        expenseRenderer.setColor(Color.GREEN);
//        expenseRenderer.setFillPoints(true);
//        expenseRenderer.setLineWidth(2);
//        expenseRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);

        multiRenderer.setChartTitle("");
        multiRenderer.setXTitle("");
        multiRenderer.setYTitle("");
        //multiRenderer.setXAxisColor(Color.TRANSPARENT);
        multiRenderer.setYAxisColor(Color.TRANSPARENT);
        multiRenderer.setYLabelsColor(Color.TRANSPARENT, Color.TRANSPARENT);

        /***
         * Customizing graphs
         */
//setting text size of the title
        multiRenderer.setChartTitleTextSize(28);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(24);
        multiRenderer.setShowLegend(false);

        multiRenderer.setShowTickMarks(false);

        //multiRenderer.setLegendTextSize(0f);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(45);
        //setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);
        //setting click false on graph
        multiRenderer.setClickEnabled(false);
        //setting zoom to false on both axis
        multiRenderer.setZoomEnabled(false, false);
        //setting lines to display on y axis
        multiRenderer.setShowGridY(false);
        //setting lines to display on x axis
        multiRenderer.setShowGridX(false);
        //setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
        //setting displaying line on grid
        multiRenderer.setShowGrid(false);
        //setting zoom to false
        multiRenderer.setZoomEnabled(false);
        //setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);
        //setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
        //setting to in scroll to false
        multiRenderer.setInScroll(false);
        //setting to set legend height of the graph
        //multiRenderer.setLegendHeight(30);
        //setting x axis label align
        multiRenderer.setXLabelsAlign(Align.CENTER);
        //setting y axis label to align
        multiRenderer.setYLabelsAlign(Align.LEFT);
        //setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);

        // Setting the Y axis height to scale to displayed data:
        int maxBar = Math.max(Math.max(mAssists, mTwoPoints), mThreePoints);
        int axisHeightGuide = (int)(1.10*maxBar);
        if(axisHeightGuide == 0){axisHeightGuide = 1+maxBar;}

        multiRenderer.setYAxisMax(axisHeightGuide);



        //setting no of values to display in y axis
        multiRenderer.setYLabels(maxBar);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-.7);
//setting max values to be display in x axis
        multiRenderer.setXAxisMax(2.7);
        //setting bar size or space between two bars
        multiRenderer.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{60, 30, 30, 30});

        for(int i=0; i< x.length;i++){
            multiRenderer.addXTextLabel(i, mLabels[i]);
        }

        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        //multiRenderer.addSeriesRenderer(expenseRenderer);

        //this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
        //remove any views before u paint the chart
        chartContainer.removeAllViews();
        //drawing bar chart
        mChart = ChartFactory.getBarChartView(this, dataset, multiRenderer,Type.DEFAULT);
        //adding the view to the linearlayout
        chartContainer.addView(mChart);

    }


    //TODO: implement that when the user clicks Save or cancel, they are brought back to Newsfeed
    public void onSaveClicked(View v){

        mGame.setmImageIdentifier(SettingsFragment.mImageId);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mGame.setUsername(prefs.getString(Globals.USERNAME, null));
        mGame.setUserId(prefs.getInt(Globals.USER_ID, 0));

        final JSONObject post = mGame.getJSONObject();

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String postResponseString = CloudUtilities.post(
                        getString(R.string.sportstat_url) + "basketball_games", post
                );

                Log.d(getLocalClassName(), postResponseString);

                try {
                    JSONObject postResponse = new JSONObject(postResponseString);

                    if (postResponse.has("status")) {
                        makeToast("Registration failed.");
                        return "failure";
                    }

                    makeToast("Saved!");
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                makeToast("Save failed.");
                return "failure";
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

    //TODO: make this return NewsFeedFragment to main view
    public void onCancelClicked(View v){


        finish();

    }

    //Launch the user's profile
    public void onViewUserProfClicked(View v){
        launchUserProfile();
    }

    private void launchUserProfile() {
        Intent i = new Intent(this, FriendViewActivity.class);
        final String enteredUserName = mGame.getUsername();

        final String correctUserName = enteredUserName.substring(0,1).toLowerCase()+enteredUserName.substring(1,enteredUserName.length());

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String userLookupResponseString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "user_id/" +
                                correctUserName + ".json");

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
                        intent.putExtra(FriendViewActivity.IMG_ID, userJSON.getString("avatar"));
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
//                                Toast.makeText(MainActivity,
//                                        toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }.execute();

    }

    private String formatDuration(long duration){
        SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
        Date date = new Date();
        date.setTime(duration);

        return format.format(date);
    }

}
