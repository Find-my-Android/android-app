package com.example.findmyandroid.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.findmyandroid.MainActivity;
import com.example.findmyandroid.data.LoginRepository;
import com.example.findmyandroid.data.Result;
import com.example.findmyandroid.data.model.CreateUser;
import com.example.findmyandroid.data.model.LoggedInUser;
import com.example.findmyandroid.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    String softwareID;
    String deviceName;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        softwareID = softwareID;
        deviceName = deviceName;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

   public boolean createUser(String nfirstName, String nlastName, String nemail, String nprimaryPhoneNum, String nsecondaryPhoneNum, String npassword) {
        Result <CreateUser> result = loginRepository.createUser(nfirstName, nlastName, nemail, nprimaryPhoneNum, nsecondaryPhoneNum, npassword);

       if (result instanceof Result.Success) {
           return true;
       } else {
           return false;
       }
   }

    public void login(String username, String password, String softwareID, String deviceName) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password, softwareID, deviceName);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName(), data.getAuthToken())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return false;
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}