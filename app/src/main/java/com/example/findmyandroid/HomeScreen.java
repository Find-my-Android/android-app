package com.example.findmyandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.findmyandroid.data.LoginDataSource;
import com.example.findmyandroid.databinding.FragmentHomeScreenBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.security.GeneralSecurityException;

import butterknife.ButterKnife;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class HomeScreen extends Fragment implements OnMapReadyCallback {

    private FragmentHomeScreenBinding binding;

    MapView mapView;
    GoogleMap map;
    MasterKey masterKeyAlias;
    private Location mLastLocation;
    private IntentFilter filter;
    private Handler mHandler;
    public BackgroundService gpsService;
    public boolean mTracking = false;

    public HomeScreen() throws GeneralSecurityException, IOException {
    }

    @SuppressLint("MissingPermission")
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        MainActivity ac = (MainActivity) getActivity();
        String token = ac.getToken();

        Log.e("Hello", "Start");
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) binding.mapview;
        mapView.onCreate(savedInstanceState);
        mHandler = new Handler();

        Log.e("Hello", "MapCreate");
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);

        Log.e("Hello", "MapAsync");

        ButterKnife.bind(getActivity());
        final Intent intent = new Intent(this.getActivity().getApplication(), BackgroundService.class);
        this.getActivity().getApplication().startService(intent);
//        this.getApplication().startForegroundService(intent);
        this.getActivity().getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.e("Hello", "IntentFinish");
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO: Start location tracking
        startLocationButtonClick();
//        binding.changePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(HomeScreen.this)
//                        .navigate(R.id.action_homeScreen_to_changePassword);
//            }
//        });
        MainActivity ac = (MainActivity) getActivity();
        Log.i("info", ac.getToken());

        binding.buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginDataSource logout = new LoginDataSource();
                logout.logout();
                String welcome = getString(R.string.successLogout);
                // TODO : initiate successful logged in experience
                if (getContext() != null && getContext().getApplicationContext() != null) {
                    Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
                }
                if (getContext() != null) {
                    try {
                        masterKeyAlias=new MasterKey.Builder(getContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
                        SharedPreferences sp = EncryptedSharedPreferences.create(
                                getContext(),
                                "secret_shared_prefs",
                                masterKeyAlias,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("username");
                        editor.remove("password");
                        editor.remove("softwareid");
                        editor.remove("devicename");
                        editor.remove("token");
                        editor.commit();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NavHostFragment.findNavController(HomeScreen.this)
                            .navigate(R.id.action_homeScreen_to_login);
                }
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.animateCamera(CameraUpdateFactory.zoomTo(1));
    }

    public void startLocationButtonClick() {
        Dexter.withActivity(this.getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(gpsService==null)
                            Log.e("A", " Nullgps");
                       // gpsService.startTracking();
                        mTracking = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }



    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();

            Log.e("A","ServiceAStart");
            if (name.endsWith("BackgroundService")) {
                Log.e("A","ServiceStart");
                MainActivity ac = (MainActivity) getActivity();
                String token = ac.getToken();
                gpsService = ((BackgroundService.LocationServiceBinder) service).getService(token);
                Log.e("A","ServiceFinish");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                //gpsService = null;
            }
        }

    };


}
