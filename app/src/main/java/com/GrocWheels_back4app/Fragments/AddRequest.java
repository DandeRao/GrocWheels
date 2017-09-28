package com.GrocWheels_back4app.Fragments;



import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.Requests;
import com.GrocWheels_back4app.R;
import com.onesignal.OneSignal;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Add Request Fragment, Used to display add request view and handle the button clicks
 * A simple {@link Fragment} subclass.
 */
public class AddRequest extends ActionBarItemsHandler {

    TextView neededBy;
    TextView item;
    Button addRequest;
    Date value =new Date();
    Calendar cal = Calendar.getInstance();
    ProgressDialog progressDialog;
    String neededByString;
    EditText itemET;
    EditText neededByET;

    /**
     * Default constructor
     */
    public AddRequest() {
        // Required empty public constructor
    }

    /**
     * Called when user clicks on AddRequest in Action Bar
     * handles the attribute's on click listener for Register button
     * to add a request into pasrse database
     * onCreateView method overrided
     *
     * @param inflater inflater for inflating layout
     * @param container viewgroup
     * @param savedInstanceState anyprevious saved instances
     * @return View
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);

        // Inflate fragment_add_request layout
        View view = inflater.inflate(R.layout.fragment_add_request, container, false);

        //Assigning hooks from the UI layout
        item = (TextView) view.findViewById(R.id.item_label);
        neededBy = (TextView) view.findViewById(R.id.needed_by);
        addRequest = (Button) view.findViewById(R.id.add_request);
        itemET = (EditText) view.findViewById(R.id.item);
        neededByET = (EditText) view.findViewById(R.id.needed_by);

        return view;
    }

    /**
     * onResume Method which will be called when the fragment enters the screen
     * Handles all actoin bar clicks and AddRequest button click
     */

    @Override
    public void onResume() {
        super.onResume();
        cal.setTime(value);
        progressDialog = new ProgressDialog(getActivity());

        // Setting date and time picker for the edit text neededBy

        neededBy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
// Date picker to be called first
                DatePickerDialog dp = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // needed by set to current date taken from the onDateSet method
                        neededByString = (month+1)+"/"+dayOfMonth+"/"+year;

                        // Time Picker called
                      TimePickerDialog tp =   new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                neededByString = neededByString + " "+ String.format("%02d",hourOfDay)+":"+String.format("%02d",minute);
                                neededBy.setText(neededByString);
                            }
                        }, cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE), true);
                        tp.show();

                    }


                },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                dp.getDatePicker().setMinDate(System.currentTimeMillis() + 1000);
                dp.setTitle("By when do you need it?");
                dp.show();
            }


        });

        // Request Button handler (Anonymous ofcourse)
        addRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validations for empty item and neededBy fields

                if (itemET.getText().toString().length() == 0 || neededByET.getText().toString().length() == 0) {
                    String message = "";
                    message = (itemET.getText().toString().length() == 0) ? " Item " : message;
                    message = (itemET.getText().toString().length() == 0 && neededByET.getText().toString().length() == 0) ? message + " and " : message;
                    message = (neededByET.getText().toString().length() == 0) ? message+"Needed By" : message;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please enter " + message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {

                    // Passed validations now enter the data into parse database
                    System.out.println("Entered Else");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setTitle("Adding the request to pool...");
                    progressDialog.show();
                    //new thread to do the network communications part
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Calling method to add request
                                addRequest();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    progressDialog.dismiss();
                  // Application redirected to RequestList fragment meanwhile asynchronusly adding data to parse server
                    ((MainActivity) getActivity()).replaceFragment(new StoresList(), false);
                }
            }
        });

    }

    /**
     * Add REquest method to asynchronously add the data to parse servers
     */
    public void addRequest(){

// Performing the updation of data to parse server asychronusly with the ASYNCTASK
new AsyncTask <String, Integer, String>() {
    @Override
    protected String doInBackground(String... params) {
       // Fetch location to add it to database
        ParseGeoPoint parseLocation = new ParseGeoPoint(((MainActivity) getActivity()).getCurrentLocation().getLatitude(),((MainActivity) getActivity()).getCurrentLocation().getLongitude());
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date dateobj = new Date();
        Requests request = new Requests();
        request.put("item",params[1]);
        request.put("RequestedBy", ParseUser.getCurrentUser().getUsername());
        request.put("neededBy",params[0]);
        request.put("requestedOn",df.format(dateobj));
        request.put("isAccepted",false);
        request.put("acceptedOn","Not Accepted Yet");
        request.put("acceptedBy","Not Accepted Yet");
        request.put("requestorEmail",ParseUser.getCurrentUser().getEmail());
        request.put("requestedLocation",parseLocation);

        // Save in Background does the actual push to server
        request.saveInBackground();


        //sendPushNotifications();
        ParseQuery pushQuery = ParseInstallation.getQuery();
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage("User "+ ParseUser.getCurrentUser().getUsername()+" needs "+ params[1]);
        push.setChannel("Requests");
        push.sendInBackground();
        try {
            push.send();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            OneSignal.postNotification(new JSONObject("{'contents': {'en':'Request Posted'}}"), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Notifications sent");
               return null;
    }
}.execute(neededByET.getText().toString(),itemET.getText().toString());

}

    /**
     * Send pushnotifications method to send push notifications to all devices
     * Currently not working as client push is disabled in parse server
     */
    public void sendPushNotifications(){



        // Create our Installation query
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("channels", "Requests"); // Set the channel

// Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage("User "+ ParseUser.getCurrentUser().getUsername()+" needs "+ item.getText().toString());
        push.setChannel("Requests");
         push.sendInBackground();


    }
}
