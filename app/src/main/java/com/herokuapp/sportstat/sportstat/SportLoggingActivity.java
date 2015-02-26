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

import java.util.UUID;


public class SportLoggingActivity extends Activity {
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("fe649006-bd16-4f05-a0c3-060750535107");
    private GameBasketball mGame;
    private TextView mAssistsView;
    private TextView mTwoPointsView;
    private TextView mThreePointsView;
    private BroadcastReceiver mPebbleConnectedReceiver;
    private BroadcastReceiver mPebbleDisconnectedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_logging);
        mGame = new GameBasketball();
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
        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);

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

    public void onDoneButtonPressed(View view) {

    }

}