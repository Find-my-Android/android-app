package com.example.findmyandroid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class BackgroundService extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";
    private LocationListener1 mLocationListener;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;

    private final int LOCATION_INTERVAL = 500;
    private final int LOCATION_DISTANCE = 10;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener1 implements android.location.LocationListener
    {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener1(String provider)
        {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
           MyAppApplication mApp = ((MyAppApplication)getApplicationContext());
            mLastLocation = location;
            Log.e(TAG, "LocationChanged: "+location);



//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);

            //check if tracking is on

//            try {
//                //
//                URL url = new URL("https://fmya.duckdns.org:8445/phone/get/" + mApp.getSoftware_id());
//                HttpURLConnection http = (HttpURLConnection)url.openConnection();
//                http.setRequestMethod("GET");
//                http.setDoOutput(true);
//                http.setRequestProperty("Accept", "application/json");
//                http.setRequestProperty("Content-Type", "application/json");
//                http.setRequestProperty("Authorization", "Bearer " + mApp.getToken());
//
//                InputStream inStream = http.getInputStream();
//                String text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
//                Log.i("test", text);
//                http.disconnect();
//
//            } catch (Exception e) {
//                Log.e("error", e.toString());
//            }
            mApp.setToken(binder.locGetToken());
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL("https://fmya.duckdns.org:8445/phone/get/" + mApp.getSoftware_id().trim());
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer " + mApp.getToken());

                InputStream inStream = http.getInputStream();
                String text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
                Log.i("test", text);
                JSONObject json = new JSONObject(text);
                String objstr = json.get("phone").toString();
                Log.i("test", objstr);
                objstr = objstr.replace("[", "");
                objstr = objstr.replace("]", "");
                JSONObject phone = new JSONObject(objstr);
                String getTrackingState = phone.getString("tracking_state").toString();
                Log.i("test", getTrackingState);
                http.disconnect();

                int track = Integer.parseInt(getTrackingState);

                if (track == 1) {
                    // Update tracking if tracking is on
                    url = new URL("https://fmya.duckdns.org:8445/phone/track");
                    http = (HttpURLConnection)url.openConnection();
                    http.setRequestMethod("PATCH");
                    http.setDoOutput(true);
                    http.setRequestProperty("Accept", "application/json");
                    http.setRequestProperty("Content-Type", "application/json");
                    http.setRequestProperty("Authorization", "Bearer " + mApp.getToken());
                    String data = "{\"software_id\": \"" + mApp.getSoftware_id() + "\", \"latitude\": \"" + location.getLatitude() + "\", \"longitude\": \"" + location.getLongitude() + "\"}";
                    Log.e("A", mApp.getToken());
                    byte[] out = data.getBytes(StandardCharsets.UTF_8);

                    OutputStream stream = http.getOutputStream();
                    stream.write(out);

                    inStream = http.getInputStream();
                    text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
                    http.disconnect();
                }
            } catch (Exception e) {
                Log.e("error", e.toString());
            }

        }
        public Location getLocation() {
            return mLastLocation;
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");
        startForeground(12345678, getNotification());
        Intent sendLocation = new Intent();
        sendLocation.setAction("string");
        sendLocation.putExtra("Data", "Hello");
        sendBroadcast(sendLocation);
        startTracking();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);

            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            Log.e("A", "IsNull");
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Log.e("A", "IsNull2");
        }
    }

    public void startTracking() {
        initializeLocationManager();
        mLocationListener = new LocationListener1(LocationManager.GPS_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener );

        } catch (java.lang.SecurityException ex) {
             Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
             Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    public void stopTracking() {
        this.onDestroy();
    }

    private Notification getNotification() {

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
            return builder.build();
        }

        return null;
    }


    public class LocationServiceBinder extends Binder {
        String token;
        public BackgroundService getService(String input) {
            token = input;
            return BackgroundService.this;
        }

        public void setToken(String input) {
            token = input;
        }

        public String locGetToken() {
            return token;
        }
    }

}
