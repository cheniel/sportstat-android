package com.herokuapp.sportstat.sportstat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DavidHarmon on 3/6/15.
 */
public class TrackingService extends Service implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = "TrackingService";

    private NotificationManager mNotificationManager;
    private static boolean isRunning = false;

    // Keeps track of all current registered clients.
    private List<Messenger> mClients = new ArrayList<Messenger>();
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_LATLNG_VALUE = 3;

    // Target we publish for clients to send messages to IncomingHandler.
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean mRequestingLocationUpdates;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            sendMessageToUI(location);
        }
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "Provider Disabled");
        }
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service Started.");

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        showNotification();
        isRunning = true;

        return START_STICKY; // Run until explicitly stopped.
    }

    /**
     * Display a notification in the notification bar.
     */
    private void showNotification() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(this.getString(R.string.service_label))
                .setContentText(getResources().getString(R.string.service_started)).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent).build();
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(0, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mMessenger.getBinder();
    }

    /**
     * Send the location data to all clients.
     * @param loc The value to send.
     */
    private void sendMessageToUI(Location loc) {
        Iterator<Messenger> messengerIterator = mClients.iterator();
        while(messengerIterator.hasNext()) {
            Messenger messenger = messengerIterator.next();
            try {
                // Send data as two doubles
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();

                Bundle bundle = new Bundle();
                bundle.putDouble("lat", lat);
                bundle.putDouble("lon", lon);
                Message msg = Message.obtain(null, MSG_SET_LATLNG_VALUE);
                msg.setData(bundle);
                messenger.send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                mClients.remove(messenger);
            }
        }
    }

    public static boolean isRunning()
    {
        return isRunning;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        mNotificationManager.cancelAll(); // Cancel the persistent notification.
        Log.i(TAG, "Service Stopped.");
        isRunning = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection to google services failed");
        Toast.makeText(this, "Google Connection Failed", Toast.LENGTH_SHORT);
    }

    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(200);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.
                requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }

    /**
     * Handle incoming messages from SportLoggingActivity
     */
    private class IncomingMessageHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
