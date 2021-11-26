package com.example.findmyandroid.data;

import com.example.findmyandroid.data.model.CreateUser;
import com.example.findmyandroid.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }


    public Result<LoggedInUser> login(String username, String password, String softwareID, String deviceName) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password, softwareID, deviceName);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result <CreateUser> createUser(String firstName, String lastName, String email, String primaryPhoneNum, String secondaryPhoneNum, String password) {
        //handle create user
        Result <CreateUser> result = dataSource.createUser(firstName, lastName, email, primaryPhoneNum, secondaryPhoneNum, password);
        if (result instanceof Result.Success) {
            //TODO: create user in backend endpoint
        }
        return result;
    }
}