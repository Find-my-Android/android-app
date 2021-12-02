package com.example.findmyandroid.data;

import android.os.StrictMode;
import android.util.Log;

import com.example.findmyandroid.MainActivity;
import com.example.findmyandroid.data.model.CreateUser;
import com.example.findmyandroid.data.model.LoggedInUser;
import java.net.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.lang.Object;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    LoggedInUser newUser;
    private String dtoken;
    String duserID;
    String dsoftwareID;
    String ddeviceName;
    String phoneNumber;

    public void setdToken(String input) {
        dtoken = input;
    }

    public String getdToken() {
        return dtoken;
    }

    public Result<CreateUser> createUser(String nfirstName, String nlastName, String nemail, String nprimaryPhoneNum, String nsecondaryPhoneNum, String npassword) {
        String firstName = nfirstName;
        String lastName = nlastName;
        String email = nemail;
        String primaryPhoneNum = nprimaryPhoneNum;
        String secondaryPhoneNum = nsecondaryPhoneNum;
        String password = npassword;
        CreateUser createdUser = new CreateUser(firstName, lastName, email, primaryPhoneNum, secondaryPhoneNum, password);
        Log.i("info", createdUser.getFirstName() + createdUser.getPassword());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL("https://fmya.duckdns.org:8445/user/signup");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("charset", "utf-8");

            String data = "{\"first_name\": \"" + createdUser.getFirstName() + "\", \"last_name\": \"" + createdUser.getLastName() + "\", \"email\": \"" + createdUser.getEmail() + "\", \"primary_num\": \"" + createdUser.getPrimaryPhoneNum() + "\", \"secondary_num\": \"" + createdUser.getSecondaryPhoneNum() + "\", \"password\": \"" + createdUser.getPassword() + "\"}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            InputStream inStream = http.getInputStream();
            String text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
            Log.i("info", text);
            http.disconnect();

            return new Result.Success<>(createdUser);
        } catch (Exception e) {
            Log.e("error", e.toString());
            return new Result.Error(new IOException("Error creating new user", e));
        }
    }


    public Result<LoggedInUser> login(String username, String password, String softwareID, String deviceName) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            //User registration: user/signup
            URL url = new URL("https://fmya.duckdns.org:8445/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("charset", "utf-8");

            String data = "{\"email\": \"" + username + "\", \"password\": \"" + password + "\", \"source\": \"android\"}";

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
            String userID = user.getString("user_id").toString();
            String name = first_name + " " + last_name;
            Log.i("test",userID);


            Random rand = new Random();
            int num1 = (rand.nextInt(7) + 1) * 100 + (rand.nextInt(8) * 10) + rand.nextInt(8);
            int num2 = rand.nextInt(743);
            int num3 = rand.nextInt(10000);

            DecimalFormat df3 = new DecimalFormat("000"); // 3 zeros
            DecimalFormat df4 = new DecimalFormat("0000"); // 4 zeros

            phoneNumber = df3.format(num1) + "-" + df3.format(num2) + "-" + df4.format(num3);

            Log.i("test",phoneNumber);
            newUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            name, token);
            url = new URL("https://fmya.duckdns.org:8445/phone/create");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", "Bearer " + token);

            data = "{\"software_id\": \"" + softwareID + "\", \"user_id\": \"" + userID + "\", \"name\": \"" + deviceName + "\", \"phone_num\": \"" + phoneNumber + "\"}";
            dsoftwareID = softwareID;
            ddeviceName = deviceName;
            setdToken(token);
            duserID = userID;
            out = data.getBytes(StandardCharsets.UTF_8);

            stream = http.getOutputStream();
            stream.write(out);

            inStream = http.getInputStream();
            text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
            http.disconnect();
            return new Result.Success<>(newUser);

        } catch (Exception e) {
            Log.e("error", e.toString());
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        newUser = new LoggedInUser("", "", "");
    }
}