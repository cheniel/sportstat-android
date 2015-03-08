package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;


public class SportLoggingActivity extends Activity implements ServiceConnection {

    private static final String TAG = "SPORT LOGGING ACTIVITY";

    public static final String ASSISTS = "assists";
    public static final String TWO_POINTS = "two_points";
    public static final String THREE_POINTS = "three_points";
    public static final String GAME_TIME = "time_of_game";
    public static final String SHOTS_ATTEMPTED ="shots_attempted";
    public static final String BASKETBALL_GAME = "basketball_game";
    public static final String CALLING_ACTIVITY = "calling_activity";
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

    // location management variables
    private Messenger mServiceMessenger = null;
    private Intent locIntent;
    boolean mIsBound;
    private ArrayList<LatLng> mLocList;     // the list of locations for tracking purpose
    private ArrayList<LatLng> mShotLocList; // the list of locations that the user took shots from
    private LatLng mLastLocation;           // the most recent location for comparison purposes

    private double mDistanceTraveled;   // the total distance traveled in meters
    private long mTimeInMillis;        // the time the game has taken thus far
    private mTimingTask mAsyncTimingTask;

    private static final String LOGTAG = "MainActivity";
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    private ServiceConnection mConnection = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_logging);

        mGame = new BasketballGame();
        mLocList = new ArrayList<>();
        mShotLocList = new ArrayList<>();

        // TODO: set userId and username in BasketballGame.

        //Save the time when the user started playing
        mTime = Calendar.getInstance().getTime().toString();

        mAssistsView = (TextView) findViewById(R.id.new_basketball_game_assist_text_view);
        mTwoPointsView = (TextView) findViewById(R.id.new_basketball_game_two_point_text_view);
        mThreePointsView = (TextView) findViewById(R.id.new_basketball_game_three_point_text_view);
        mAssistButton = (Button) findViewById(R.id.new_basketball_game_assist_button);
        mTwoPointButton = (Button) findViewById(R.id.new_basketball_game_two_point_button);
        mThreePointButton = (Button) findViewById(R.id.new_basketball_game_three_point_button);

        locIntent = new Intent(this, TrackingService.class);

        mAsyncTimingTask = new mTimingTask();
        mAsyncTimingTask.execute();

        startService(locIntent);
        automaticBind();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }
        stopService(locIntent);
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
                    logShotLocation();
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
                    logShotLocation();
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
                    logShotLocation();
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
                    logShotLocation();  // only do it here since all three are sent every time
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

        // pass the time and location list to the game object
        mGame.setLocList(mLocList);
        mGame.setDistance(mDistanceTraveled);
        mGame.setDuration(mTimeInMillis);
        mGame.setPossessions(inferNumPossessionsFromLocList());

        Intent intent = new Intent(this, GameSummaryActivity.class);

        intent.putExtra(BASKETBALL_GAME, mGame);
        intent.putExtra(CALLING_ACTIVITY, "sport_logging");
//
//        intent.putExtra(ASSISTS, mGame.getAssists());
//        intent.putExtra(TWO_POINTS, mGame.getTwoPoints());
//        intent.putExtra(THREE_POINTS, mGame.getThreePoints());


        //TODO: infer how many shots attempted

        //Put the automatically recorded stats here too

        startActivity(intent);

        finish();
    }

    /**
     * use a ton of really hard math to infer the number of times that you crossed the
     * half way mark of the court, then calculate the average side that you incremented
     * your shot counter from to determine which side is your teams and which side is
     * the other teams.  Use all of this information to calculate the number of
     * possessions for each team, and the number of attempted / completed shots compared
     * to the number of possessions.
     *
     * @return the number of possessions for each team
     */
    private int inferNumPossessionsFromLocList() {

        double averageLat = 0.0, averageLon = 0.0;
        int numLeftPossessions = 0, numRightPossessions = 0;

        LatLng averagePoint;
        LatLng leftPoint = mLocList.get(0), rightPoint = mLocList.get(0);
        double largestDistance = 0.0;
        int side;

        // estimate center court location
        for (LatLng loc : mLocList){
            averageLat += loc.latitude;
            averageLon += loc.longitude;
        }
        averageLat /= mLocList.size();
        averageLon /= mLocList.size();
        averagePoint = new LatLng(averageLat, averageLon);

        // find the two farthest points from each other
        // uses a very basic algorithm, could definitely be improved with more time
        for (LatLng one : mLocList){
            for (LatLng two : mLocList){
                double temp = distanceFormula(one, two);
                if (temp > largestDistance){
                    leftPoint = one;
                    rightPoint = two;
                    largestDistance = temp;
                }
            }
        }

        // compute a list of points every time you cross center court
        boolean isOnRightSide = false;  // initialization is for the compiler, it will be set
        for (LatLng loc : mLocList) {
            double distanceToLeft = distanceFormula(loc, leftPoint);
            double distanceToRight = distanceFormula(loc, rightPoint);
            if (loc == mLocList.get(0)){    // initialize what side we start on
                if (distanceToLeft > distanceToRight){
                    isOnRightSide = true;
                } else {
                    isOnRightSide = false;
                }
            }else {
                if (distanceToLeft > distanceToRight) {
                    if (!isOnRightSide){    // if we just crossed over to the right side
                        numRightPossessions++;
                        isOnRightSide = true;
                    }
                } else {
                    if (isOnRightSide){     // if we just crossed over to the left side
                        numLeftPossessions++;
                        isOnRightSide = false;
                    }
                }
            }
        }

        // compute the side that belongs to each team using the location that shots were taken from
        int left = 0, right = 0;
        for (LatLng shotLoc : mShotLocList){
            double distanceToLeft = distanceFormula(shotLoc, leftPoint);
            double distanceToRight = distanceFormula(shotLoc, rightPoint);
            if (distanceToLeft > distanceToRight){
                right++;
            } else {
                left++;
            }
        }

        // if the greater number of shots were taken from the left side, then return
        // the number of possessions on the left side, else return the number of
        // possessions from the right side
        if (left > right){
            return numLeftPossessions;
        } else {
            return numRightPossessions;
        }
    }

    /**
     * log the location that the shot was taken from
     */
    private void logShotLocation(){
        mShotLocList.add(mLocList.get(mLocList.size() - 1));
    }

    private double distanceFormula(LatLng onePt, LatLng twoPt){
        Location one = new Location("SportLoggingActivity");
        Location two = new Location("SportLoggingActivity");

        one.setLatitude(onePt.latitude);
        one.setLongitude(onePt.longitude);

        two.setLatitude(twoPt.latitude);
        two.setLongitude(twoPt.longitude);

        return one.distanceTo(two);
    }

    /**
     * All of this stuff is for receiving updates from the tracking service
     */

    /**
     * Check if the service is running. If the service is running
     * when the activity starts, we want to automatically bind to it.
     */
    private void automaticBind() {
        if (TrackingService.isRunning()) {
            doBindService();
        }
    }

    /**
     * Send data to the service
     * @param intvaluetosend The data to send
     */
    private void sendMessageToService(int intvaluetosend) {
        if (mIsBound) {
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null, TrackingService.MSG_SET_LATLNG_VALUE,
                            intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /**
     * Bind this Activity to TimerService
     */
    private void doBindService() {
        bindService(new Intent(this, TrackingService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    /**
     * Un-bind this Activity to TimerService
     */
    private void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null, TrackingService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mServiceMessenger = new Messenger(service);
        try {
            Message msg = Message.obtain(null, TrackingService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            mServiceMessenger.send(msg);
        }
        catch (RemoteException e) {
            // In this case the service has crashed before we could even do anything with it
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
        mServiceMessenger = null;
    }

    /**
     * Handle incoming messages from TimerService
     */
    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // Log.d(LOGTAG,"IncomingHandler:handleMessage");
            switch (msg.what) {
                case TrackingService.MSG_SET_LATLNG_VALUE:
                    double lat = msg.getData().getDouble("lat");
                    double lon = msg.getData().getDouble("lon");
                    LatLng loc = new LatLng(lat, lon);

                    if (mLastLocation != null){
                        mDistanceTraveled+=(distanceFormula(loc, mLastLocation));
                    }

                    mLocList.add(loc);
                    mLastLocation = loc;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * tracking service stuff ends here
     */

    private class mTimingTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {

            mTimeInMillis = SystemClock.currentThreadTimeMillis();

            return null;
        }
    }
}
