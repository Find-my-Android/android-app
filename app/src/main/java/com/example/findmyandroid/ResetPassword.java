package com.example.findmyandroid;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.findmyandroid.databinding.FragmentResetPasswordBinding;
import com.example.findmyandroid.databinding.FragmentUserRegistrationBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ResetPassword extends Fragment {

    private FragmentResetPasswordBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    URL url = new URL("https://fmya.duckdns.org:8445/user/forgotpassword");
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoOutput(true);
                    http.setRequestProperty("Accept", "application/json");
                    http.setRequestProperty("Content-Type", "application/json");

                    final EditText emailEdit = binding.email;
                    String email=emailEdit.getText().toString();

                    String data = "{\"email\": \""+email+"\"}";

                    byte[] out = data.getBytes(StandardCharsets.UTF_8);

                    OutputStream stream = http.getOutputStream();
                    stream.write(out);

                    if(http.getResponseCode()==200){
                        String success="An Email has been sent. Please check your spam folder";
                        if (getContext() != null && getContext().getApplicationContext() != null) {
                            Toast.makeText(
                                    getContext().getApplicationContext(),
                                    success,
                                    Toast.LENGTH_LONG).show();
                        }
                        http.disconnect();
                        NavHostFragment.findNavController(ResetPassword.this)
                                .navigate(R.id.action_reset_password_to_login);

                    }
                    else if(http.getResponseCode()==400){
                        String fail="This user doesn't seem to exist. Please double check the email you entered.";
                        if (getContext() != null && getContext().getApplicationContext() != null) {
                            Toast.makeText(
                                    getContext().getApplicationContext(),
                                    fail,
                                    Toast.LENGTH_LONG).show();
                        }
                        http.disconnect();
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                }

            }
        });

        binding.buttonBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ResetPassword.this)
                        .navigate(R.id.action_reset_password_to_login);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}