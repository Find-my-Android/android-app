package com.example.findmyandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.findmyandroid.data.Result;
import com.example.findmyandroid.data.model.CreateUser;
import com.example.findmyandroid.databinding.FragmentUserRegistrationBinding;
import com.example.findmyandroid.ui.login.LoginViewModel;
import com.example.findmyandroid.ui.login.LoginViewModelFactory;
import com.example.findmyandroid.ui.login.UserRegistrationModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class UserRegistration extends Fragment {

    private LoginViewModel loginViewModel;
    private UserRegistrationModel userRegistrationModel;
    private FragmentUserRegistrationBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentUserRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        final EditText firstNameEdit = binding.firstName;
        final EditText lastNameEdit = binding.lastName;
        final EditText emailEdit = binding.email;
        final EditText primaryPhoneEdit = binding.primaryPhone;
        final EditText secondaryPhoneEdit = binding.secondaryPhone;
        final EditText firstPasswordEdit = binding.password;
        final EditText secondPasswordEdit = binding.passwordAgain;
        binding.buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = firstNameEdit.getText().toString();
                String lastName = lastNameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String primaryPhone = primaryPhoneEdit.getText().toString();
                String secondaryPhone = secondaryPhoneEdit.getText().toString();
                String firstPassword = firstPasswordEdit.getText().toString();
                String secondPassword = secondPasswordEdit.getText().toString();
                //Log.i("info", firstName);

                //TODO: update create button
                userRegistrationModel = new UserRegistrationModel();
                if (!userRegistrationModel.fieldFilled(firstName)) {
                    showCreateUserErrorEmptyField("First Name");
                    return;
                }

                if (!userRegistrationModel.fieldFilled(lastName)) {
                    showCreateUserErrorEmptyField("Last Name");
                    return;
                }

                if (!userRegistrationModel.isNameValid(firstName)) {
                    showBadInputMessage("Enter a valid first name");
                    return;
                }

                if (!userRegistrationModel.isNameValid(lastName)) {
                    showBadInputMessage("Enter a valid second name");
                    return;
                }

                if (!userRegistrationModel.fieldFilled(email)) {
                    showCreateUserErrorEmptyField("Email");
                    return;
                }

                if(!userRegistrationModel.isUserNameValid(email)) {
                    showBadInputMessage("Invalid email entered");
                    return;
                }

                if (!userRegistrationModel.fieldFilled(primaryPhone)) {
                    showCreateUserErrorEmptyField("Primary Phone");
                    return;
                }

                if(!userRegistrationModel.isPhoneNumberValid(primaryPhone)) {
                    showBadInputMessage("Invalid primary phone number entered");
                    return;
                }

                if(secondaryPhone.trim().length() == 0 || secondaryPhone == null) {
                    secondaryPhone = "";
                } else if(!userRegistrationModel.isPhoneNumberValid(secondaryPhone)) {
                    showBadInputMessage("Invalid secondary phone number entered");
                    return;
                }

                if (!userRegistrationModel.fieldFilled(firstPassword)) {
                    showCreateUserErrorEmptyField("Password");
                    return;
                }

                if (!userRegistrationModel.fieldFilled(secondPassword)) {
                    showCreateUserErrorEmptyField("Enter Password Again");
                    return;
                }

                if(!userRegistrationModel.isPasswordValid(firstPassword)) {
                    showBadInputMessage("Password length must be more than 5");
                    return;
                }

                if(!userRegistrationModel.isPasswordValid(secondPassword)) {
                    showBadInputMessage("Password length must be more than 5");
                    return;
                }

                if(!userRegistrationModel.isPasswordMatching(firstPassword, secondPassword)) {
                    showBadInputMessage("Passwords do not match");
                    return;
                }

                CreateUser newUser = new CreateUser(firstName, lastName, email, primaryPhone, secondaryPhone, firstPassword);
                Boolean result = loginViewModel.createUser(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), newUser.getPrimaryPhoneNum(), newUser.getSecondaryPhoneNum(), newUser.getPassword());
                Log.i("user", newUser.getFirstName());
                if(result) {
                    successfullyCreatedUser(firstName, lastName);
                    NavHostFragment.findNavController(UserRegistration.this)
                            .navigate(R.id.action_userRegstration_to_login);
                }
                errorCreatingUser(firstName, lastName);
            }
        });

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserRegistration.this)
                        .navigate(R.id.action_userRegstration_to_login);
            }
        });
    }

    public void showBadInputMessage(String input) {
        String fail = input;
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    input,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void showCreateUserErrorEmptyField(String field) {
        String fail = "Please enter field for the " + field;
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    fail,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void successfullyCreatedUser(String firstName, String lastName) {
        String success = "Successfully created user for " + firstName + " " + lastName;
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    success,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void errorCreatingUser(String firstName, String lastName) {
        String success = "Error when creating user for " + firstName + " " + lastName;
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    success,
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}