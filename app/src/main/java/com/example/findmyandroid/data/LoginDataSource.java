package com.example.findmyandroid.data;

import android.os.StrictMode;
import android.util.Log;

import com.example.findmyandroid.data.model.LoggedInUser;
import java.net.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.lang.Object;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL("https://fmya.duckdns.org:8445/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("charset", "utf-8");

            String data = "{\"email\": \"" + username + "\", \"password\": \"" + password + "\"}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            InputStream inStream = http.getInputStream();
            String text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();

            char[] characters = new char[text.length()];
            for (int i = 0; i < text.length(); i++) {
                characters[i] = text.charAt(i);
            }

            String token = "";
            int counter = 0;

            for (int i = 0; i < text.length(); i++) {
                if (characters[i] == '\"') {
                    counter++;
                    i++;
                }

                if (counter == 5) {
                    token += characters[i];
                }
            }
            http.disconnect();

            url = new URL("https://fmya.duckdns.org:8445/user");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Authorization", "Bearer " + token);

            inStream = http.getInputStream();
            text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
            http.disconnect();

            JSONObject json = new JSONObject(text);
            String objstr = json.get("user").toString();
            objstr = objstr.replace("[", "");
            objstr = objstr.replace("]", "");
            JSONObject user = new JSONObject(objstr);
            String last_name = user.getString("last_name").toString();
            String first_name = user.getString("first_name").toString();
            String name = first_name + " " + last_name;

            LoggedInUser newUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            name);

            return new Result.Success<>(newUser);

        } catch (Exception e) {
            Log.e("error", e.toString());
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}