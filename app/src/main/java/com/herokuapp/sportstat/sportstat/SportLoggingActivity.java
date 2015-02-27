package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;


public class SportLoggingActivity extends Activity {

    public static final String ASSISTS = "assists";
    public static final String TWO_POINTS = "two_points";
    public static final String THREE_POINTS = "three_points";
    public static final String GAME_TIME = "time_of_game";
    private BasketballGame mGame;
    private String mTime;
    private TextView mAssistsView;
    private TextView mTwoPointsView;
    private TextView mThreePointsView;
    private BroadcastReceiver mPebbleConnectedReceiver;
    private BroadcastReceiver mPebbleDisconnectedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_logging);
        mGame = new BasketballGame();

        //Save the time when the user started playing
        mTime = Calendar.getInstance().getTime().toString();


        mAssistsView = (TextView) findViewById(R.id.new_basketball_game_assist_text_view);
        mTwoPointsView = (TextView) findViewById(R.id.new_basketball_game_two_point_text_view);
        mThreePointsView = (TextView) findViewById(R.id.new_basketball_game_three_point_text_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // detect if watch is connected
        boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
        Log.i(getLocalClassName(), "Pebble is " + (connected ? "connected" : "not connected"));
        if (connected) {
            Toast.makeText(this, "Pebble is connected!", Toast.LENGTH_SHORT).show();
        }

        // open up SportStat Pebble app
        PebbleKit.startAppOnPebble(getApplicationContext(), PebbleApp.APP_UUID);

        // register receivers for Pebble disconnect and connect
        mPebbleConnectedReceiver = PebbleKit.registerPebbleConnectedReceiver(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(getLocalClassName(), "Pebble connected!");
                Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
            }
        });

        mPebbleDisconnectedReceiver = PebbleKit.registerPebbleDisconnectedReceiver(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(getLocalClassName(), "Pebble disconnected!");
                Toast.makeText(getApplicationContext(), "Pebble disconnected!", Toast.LENGTH_LONG).show();
            }
        });

        // send initial message to Pebble
        PebbleDictionary data = new PebbleDictionary();
        data.addString(PebbleApp.MSG_GENERIC_STRING, "onResume message");
        data.addUint8(PebbleApp.MSG_ASSIST_COUNT, (byte) mGame.getAssists());
        data.addUint8(PebbleApp.MSG_TWO_POINT_COUNT, (byte) mGame.getTwoPoints());
        data.addUint8(PebbleApp.MSG_THREE_POINT_COUNT, (byte) mGame.getThreePoints());
        PebbleKit.sendDataToPebble(getApplicationContext(), PebbleApp.APP_UUID, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mPebbleConnectedReceiver);
        unregisterReceiver(mPebbleDisconnectedReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sport_logging, menu);
        return true;
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

    public void increaseAssists(View view) {
        mGame.setAssists(mGame.getAssists() + 1);
        mAssistsView.setText(String.valueOf(mGame.getAssists()));
    }

    public void increaseTwoPoints(View view) {
        mGame.setTwoPoints(mGame.getTwoPoints() + 1);
        mTwoPointsView.setText(String.valueOf(mGame.getTwoPoints()));
    }

    public void increaseThreePoints(View view) {
        mGame.setThreePoints(mGame.getThreePoints() + 1);
        mThreePointsView.setText(String.valueOf(mGame.getThreePoints()));
    }

    // TODO: add decrement onLongClick

    //When the user clicks done, launch the GameSummaryActivity,
    //and pass all statistics to it (as well as game
    public void onDoneButtonPressed(View view) {

        Intent intent = new Intent(this, GameSummaryActivity.class);
        intent.putExtra(ASSISTS, mGame.getAssists());
        intent.putExtra(TWO_POINTS, mGame.getTwoPoints());
        intent.putExtra(THREE_POINTS, mGame.getThreePoints());
        intent.putExtra(GAME_TIME, mTime);
        //Put the automatically recorded stats here too


        startActivity(intent);

    }

}
