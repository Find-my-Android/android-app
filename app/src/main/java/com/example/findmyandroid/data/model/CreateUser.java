package com.example.findmyandroid.data.model;

public class CreateUser {

    private String firstName;
    private String lastName;
    private String email;
    private String primaryPhoneNum;
    private String secondaryPhoneNum;
    private String password;

    public CreateUser(String firstName, String lastName, String email, String primaryPhoneNum, String secondaryPhoneNum, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.primaryPhoneNum = primaryPhoneNum;
        this.secondaryPhoneNum = secondaryPhoneNum;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPrimaryPhoneNum() {
        return primaryPhoneNum;
    }

    public String getSecondaryPhoneNum() {
        return secondaryPhoneNum;
    }

    public String getPassword() {
        return password;
    }
}
