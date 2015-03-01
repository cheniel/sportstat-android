package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Calendar;


public class SportLoggingActivity extends Activity {

    public static final String ASSISTS = "assists";
    public static final String TWO_POINTS = "two_points";
    public static final String THREE_POINTS = "three_points";
    public static final String GAME_TIME = "time_of_game";
    public static final String SHOTS_ATTEMPTED ="shots_attempted";
    public static final String BASKETBALL_GAME = "basketball_game";
    private BasketballGame mGame;
    private String mTime;
    private TextView mAssistsView;
    private TextView mTwoPointsView;
    private TextView mThreePointsView;
    private Button mAssistButton;
    private Button mTwoPointButton;
    private Button mThreePointButton;
    private BroadcastReceiver mPebbleConnectedReceiver;
    private BroadcastReceiver mPebbleDisconnectedReceiver;
    private BroadcastReceiver mPebbleDataReceiver;
    private BroadcastReceiver mPebbleNackReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_logging);




        mGame = new BasketballGame(this);

        //Save the time when the user started playing
        mTime = Calendar.getInstance().getTime().toString();

        mAssistsView = (TextView) findViewById(R.id.new_basketball_game_assist_text_view);
        mTwoPointsView = (TextView) findViewById(R.id.new_basketball_game_two_point_text_view);
        mThreePointsView = (TextView) findViewById(R.id.new_basketball_game_three_point_text_view);
        mAssistButton = (Button) findViewById(R.id.new_basketball_game_assist_button);
        mTwoPointButton = (Button) findViewById(R.id.new_basketball_game_two_point_button);
        mThreePointButton = (Button) findViewById(R.id.new_basketball_game_three_point_button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerPebbleReceivers();
        setOnLongClickListeners();

        // detect if watch is connected
        boolean connected = PebbleKit.isWatchConnected(this);
        Log.i(getLocalClassName(), "Pebble is " + (connected ? "connected" : "not connected"));
        if (connected) {
            Toast.makeText(this, "Pebble is connected!", Toast.LENGTH_SHORT).show();
            sendPointInfoToPebble("initial data sent from Android", true);
        }

        // open up SportStat Pebble app
        PebbleKit.startAppOnPebble(this, PebbleApp.APP_UUID);
    }

    private void setOnLongClickListeners() {

        mAssistButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mGame.getAssists() > 0) {
                    mGame.setAssists(mGame.getAssists() - 1);
                    mAssistsView.setText(String.valueOf(mGame.getAssists()));
                    sendPointInfoToPebble(null, false);
                    return true;
                }
                return false;
            }
        });

        mTwoPointButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mGame.getTwoPoints() > 0) {
                    mGame.setTwoPoints(mGame.getTwoPoints() - 1);
                    mTwoPointsView.setText(String.valueOf(mGame.getTwoPoints()));
                    sendPointInfoToPebble(null, false);
                    return true;
                }
                return false;
            }
        });

        mThreePointButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mGame.getThreePoints() > 0) {
                    mGame.setThreePoints(mGame.getThreePoints() - 1);
                    mThreePointsView.setText(String.valueOf(mGame.getThreePoints()));
                    sendPointInfoToPebble(null, false);
                    return true;
                }
                return false;
            }
        });
    }

    private void registerPebbleReceivers() {

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

        mPebbleNackReceiver = PebbleKit.registerReceivedNackHandler(this, new PebbleKit.PebbleNackReceiver(PebbleApp.APP_UUID) {

            @Override
            public void receiveNack(Context context, int transactionId) {
                Log.i(getLocalClassName(), "message failed, retrying" + transactionId);
                sendPointInfoToPebble(null, false);
            }

        });

        final Handler handler = new Handler();
        mPebbleDataReceiver = PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PebbleApp.APP_UUID) {

            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                Log.d(getLocalClassName(), "Received data from Pebble!");

                boolean receivedPointData = false;

                String msg = data.getString(PebbleApp.MSG_GENERIC_STRING);
                if (msg != null) {
                    Log.d(getLocalClassName(), "Received msg from pebble: " + msg);
                }

                Long assists = data.getUnsignedIntegerAsLong(PebbleApp.MSG_ASSIST_COUNT);
                if (assists != null) {
                    mGame.setAssists(assists.intValue());
                    receivedPointData = true;
                }

                Long two_pts = data.getUnsignedIntegerAsLong(PebbleApp.MSG_TWO_POINT_COUNT);
                if (two_pts != null) {
                    mGame.setTwoPoints(two_pts.intValue());
                    receivedPointData = true;
                }

                Long three_pts = data.getUnsignedIntegerAsLong(PebbleApp.MSG_THREE_POINT_COUNT);
                if (three_pts != null) {
                    mGame.setThreePoints(three_pts.intValue());
                    receivedPointData = true;
                }

                if (data.contains(PebbleApp.MSG_REQUEST_RESPONSE)) {
                    sendPointInfoToPebble("responding", false);
                }

                if (receivedPointData) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateViewPointValues();
                        }
                    });
                }

                PebbleKit.sendAckToPebble(context, transactionId);
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mPebbleConnectedReceiver);
        unregisterReceiver(mPebbleDisconnectedReceiver);
        unregisterReceiver(mPebbleDataReceiver);
        unregisterReceiver(mPebbleNackReceiver);
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
        sendPointInfoToPebble(null, false);
    }

    public void increaseTwoPoints(View view) {
        mGame.setTwoPoints(mGame.getTwoPoints() + 1);
        mTwoPointsView.setText(String.valueOf(mGame.getTwoPoints()));
        sendPointInfoToPebble(null, false);
    }

    public void increaseThreePoints(View view) {
        mGame.setThreePoints(mGame.getThreePoints() + 1);
        mThreePointsView.setText(String.valueOf(mGame.getThreePoints()));
        sendPointInfoToPebble(null, false);
    }

    private void updateViewPointValues() {
        mAssistsView.setText(String.valueOf(mGame.getAssists()));
        mTwoPointsView.setText(String.valueOf(mGame.getTwoPoints()));
        mThreePointsView.setText(String.valueOf(mGame.getThreePoints()));
    }


    private void sendPointInfoToPebble(String message, boolean initial) {
        Log.d(getLocalClassName(), "called sendPointInfoToPebble");
        PebbleDictionary data = new PebbleDictionary();
        if (message != null) {
            data.addString(PebbleApp.MSG_GENERIC_STRING, message);
        }
        if (initial) {
            data.addInt8(PebbleApp.MSG_INITIAL_POINT_LOAD, (byte) 0);
        }
        data.addUint8(PebbleApp.MSG_ASSIST_COUNT, (byte) mGame.getAssists());
        data.addUint8(PebbleApp.MSG_TWO_POINT_COUNT, (byte) mGame.getTwoPoints());
        data.addUint8(PebbleApp.MSG_THREE_POINT_COUNT, (byte) mGame.getThreePoints());
        PebbleKit.sendDataToPebble(this, PebbleApp.APP_UUID, data);
    }

    private void sendGameEndToPebble() {
        PebbleDictionary data = new PebbleDictionary();
        data.addInt8(PebbleApp.MSG_END_GAME, (byte) 0);
        PebbleKit.sendDataToPebble(this, PebbleApp.APP_UUID, data);
    }

    //When the user clicks done, launch the GameSummaryActivity,
    //and pass the mGame (w all saved stats) to that
    public void onDoneButtonPressed(View view) {
        sendGameEndToPebble();

        Intent intent = new Intent(this, GameSummaryActivity.class);

        intent.putExtra(BASKETBALL_GAME, mGame);
//
//        intent.putExtra(ASSISTS, mGame.getAssists());
//        intent.putExtra(TWO_POINTS, mGame.getTwoPoints());
//        intent.putExtra(THREE_POINTS, mGame.getThreePoints());


        //TODO: infer how many shots attempted

        //Put the automatically recorded stats here too

        startActivity(intent);

        finish();
    }

}
