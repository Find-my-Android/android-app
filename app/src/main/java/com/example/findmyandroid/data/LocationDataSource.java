package com.example.findmyandroid.data;

import android.os.StrictMode;
import android.util.Log;

import com.example.findmyandroid.data.model.LoggedInUser;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

public class LocationDataSource {

    public Result<LoggedInUser> sendLocation(String latitude, String longitude, String softwareID, String token) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            //
            URL url = new URL("https://fmya.duckdns.org:8445/phone/track");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", "Bearer " + token);
            String data = "{\"software_id\": \"" + softwareID + "\", \"latitude\": \"" + latitude + "\", \"longitude\": \"" + longitude + "\"}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);
            http.disconnect();
            return new Result.Success<>("Yes");

        } catch (Exception e) {
            Log.e("error", e.toString());
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
}
