package com.example.findmyandroid.ui.login;

import android.util.Patterns;

public class UserRegistrationModel {

    public boolean fieldFilled(String input) {
        return input.trim().length() > 0;
    }


    public boolean isNameValid(String name) {
        return name != null && name.trim().length() > 1;
    }

    public boolean isPhoneNumberValid(String number) {
        if (number == null) {
            return false;
        }

        if(number.trim().length() < 8) {
            return false;
        }

        try {
            number = number.replace("\"", "");
            int num = Integer.parseInt(number.trim());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return false;
        }
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public boolean isPasswordMatching(String password1, String password2) {
        return password1.equals(password2);
    }
}
