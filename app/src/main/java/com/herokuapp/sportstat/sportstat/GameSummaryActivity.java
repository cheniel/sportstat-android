package com.herokuapp.sportstat.sportstat;


import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.herokuapp.sportstat.sportstat.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;

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
    private String mTime;


    private View mChart;
    private String[] mLabels = new String[] {
            "Assists", "Two-Points" , "Three-Points", ""
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);

        Intent i = getIntent();

        mAssists = i.getIntExtra(SportLoggingActivity.ASSISTS, 0);
        mTwoPoints = i.getIntExtra(SportLoggingActivity.TWO_POINTS, 0);
        mThreePoints = i.getIntExtra(SportLoggingActivity.THREE_POINTS, 0);
        mTime = i.getStringExtra(SportLoggingActivity.GAME_TIME);
        //TODO: insert other stats


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
        incomeRenderer.setDisplayChartValuesDistance(10); //setting chart value distance

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

        /***
         * Customizing graphs
         */
//setting text size of the title
        multiRenderer.setChartTitleTextSize(28);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(24);
        multiRenderer.setShowLegend(false);
        //TODO: Get rid of tiny numbers above columns

        //multiRenderer.setLegendTextSize(0f);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(35);
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
        multiRenderer.setLegendHeight(30);
        //setting x axis label align
        multiRenderer.setXLabelsAlign(Align.CENTER);
        //setting y axis label to align
        multiRenderer.setYLabelsAlign(Align.LEFT);
        //setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        //setting no of values to display in y axis
        multiRenderer.setYLabels(10);
        // Setting the Y axis height to scale to displayed data:
        int maxBar = Math.max(Math.max(mAssists, mTwoPoints), mThreePoints);
        int axisHeightGuide = (int)(1.10*maxBar);
        if(axisHeightGuide == 0){axisHeightGuide = 1+maxBar;}

        multiRenderer.setYAxisMax(axisHeightGuide);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-0.5);
//setting max values to be display in x axis
        multiRenderer.setXAxisMax(3);
        //setting bar size or space between two bars
        multiRenderer.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

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



}
