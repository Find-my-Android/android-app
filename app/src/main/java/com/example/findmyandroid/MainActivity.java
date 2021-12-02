package com.example.findmyandroid;

import android.os.Build;
import android.os.Bundle;

import com.example.findmyandroid.data.model.LoggedInUser;
import com.example.findmyandroid.ui.login.LoginFragment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.findmyandroid.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    String android_id;
    String device_name;
    String token;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        String phoneNumber;
        super.onCreate(savedInstanceState);
        try {
            android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            device_name = android.os.Build.MODEL;
            //TODO: Test with real phone number?
            bundle.putString("softwareID", android_id);
            bundle.putString("deviceName", device_name);
            MyAppApplication mApp = ((MyAppApplication)getApplicationContext());
            mApp.setSoftware_id(android_id);
            mApp.setToken(token);

            Log.i("info", mApp.getToken().toString());

            LoginFragment frag = new LoginFragment();
            frag.setArguments(bundle);
            //phoneNumber = tMgr.getLine1Number();
            Log.i("SoftwareID: ", android_id);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public String getSoftwareID() {
        return android_id;
    }

    public String getDeviceName() {
        return device_name;
    }

    public void setToken(String input) {
        token = input;
    }

    public String getToken() {return token; }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}