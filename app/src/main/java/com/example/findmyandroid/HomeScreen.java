package com.example.findmyandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Bundle;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class HomeScreen extends Fragment implements OnMapReadyCallback {

    private FragmentHomeScreenBinding binding;

    MapView mapView;
    GoogleMap map;
    MasterKey masterKeyAlias;
    public HomeScreen() throws GeneralSecurityException, IOException {
    }

    @SuppressLint("MissingPermission")
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeScreenBinding.inflate(inflater, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) binding.mapview;
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.changePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(HomeScreen.this)
//                        .navigate(R.id.action_homeScreen_to_changePassword);
//            }
//        });

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
       /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.1, -87.9)));

    }
}
