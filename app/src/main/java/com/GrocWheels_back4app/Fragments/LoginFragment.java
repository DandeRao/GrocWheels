package com.GrocWheels_back4app.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Login Fragment to enable the user to login to the application,
 * Also sends flow to register screen for registering the user
 */
public class LoginFragment extends Fragment {

    Button login;
    Button register;
    EditText userName;
    EditText password;
    ProgressDialog progressDialog;
    ParseUser currentUser;
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * On Create view to handle all the above said things.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
     @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        System.out.println("Login Fragment Called");
      // Inflate the view
         View view = inflater.inflate(R.layout.fragment_login,container,false);

       //Set all the front end element hooks
        progressDialog = new ProgressDialog(this.getContext());
        userName = (EditText) view.findViewById(R.id.userName);
        password = (EditText) view.findViewById(R.id.password);
        login = (Button) view.findViewById(R.id.login);

         // get current user to do auto login
         currentUser = ParseUser.getCurrentUser();
         //Autologging in
         if (currentUser != null) {

             progressDialog.setMessage("Please Wait");
             progressDialog.setTitle("Logging in");
             progressDialog.show();
             progressDialog.dismiss();
             System.out.println("Current User "+currentUser.getUsername());
             alertDisplayer("Welcome Back", "User:" + currentUser.getUsername() +"\nEmail:"+currentUser.getEmail());
             if(ParseUser.getCurrentUser().getString("role").equals("Buyer")) {
                 ((MainActivity) getActivity()).replaceFragment(new StoresList(), true);
             }
             else if(ParseUser.getCurrentUser().getString("role").equals("Driver")){
                 ((MainActivity) getActivity()).replaceFragment(new StoresForDeliveryList(), true);
             }
             else if(ParseUser.getCurrentUser().getString("role").equals("store")){
                 alertDisplayer("Login Denied","Store Id cannot be used to login. \n Please use personal user name");
                 ParseUser.logOut();
             }

         } else {
             // show the signup or login screen
         }

        // Login button setting onClickListener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.setTitle("Logging in");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Actual Login Step
                            parseLogin();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start(); // Done on seperate thread

            }
        });

        register = (Button) view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calling register fragment
                ((MainActivity) getActivity()).replaceFragment(new RegisterFragment(),true);
            }
        });
        return view;
    }

    /**
     * Login Method
     */
    void parseLogin(){
        ParseUser.logInInBackground(userName.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                // Passed login step, Load the requestsList fragment
                if (parseUser != null) {
                    // Login pass go to requestlist page
                    progressDialog.dismiss();
                    getUserDetailFromParse();
                   if(ParseUser.getCurrentUser().getString("role").equals("Buyer")) {
                       ((MainActivity) getActivity()).replaceFragment(new StoresList(), true);
                   }
                   else if(ParseUser.getCurrentUser().getString("role").equals("Driver")){
                       ((MainActivity) getActivity()).replaceFragment(new StoresForDeliveryList(), true);
                   }
                   else if(ParseUser.getCurrentUser().getString("role").equals("store")){
                       alertDisplayer("Login Denied","Store Id cannot be used to login. \n Please use personal user name");
                       ParseUser.logOut();
                   }

                } else {
                    // Login Fail alert the user
                    progressDialog.dismiss();
                    alertDisplayer("Login Fail", e.getMessage()+" Please re-try");
                }
            }
        });
    }


    /**
     * GetUserDetails from parsemethod to get a user (for logging in)
     */
    void getUserDetailFromParse(){
        ParseUser user = ParseUser.getCurrentUser();

        alertDisplayer("Welcome Back", "User: " + user.getUsername() +"\nEmail: "+user.getEmail());

    }

    /**
     * Method to be called for displaying alerts
     * @param title Title for the alert
     * @param message Message to be displayed in the alert
     */
    void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity) getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }




}
