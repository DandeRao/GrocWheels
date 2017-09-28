package com.GrocWheels_back4app.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.Request;
import com.GrocWheels_back4app.Model.Requests;
import com.GrocWheels_back4app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * UserRequests Fragment created to display the requests Posted or accepted by user
 * Extends Fragment
 * * A simple {@link Fragment} subclass.
 */
public class UserRequests extends Fragment {

    ListView userRequests;
    TextView requestsTypeLabel;
    String fetching;
    int showing;

    /**
     * No ARG Constructor
     */
    public UserRequests() {
        // Required empty public constructor
    }

    /**
     * Called when user requests is created
     * handles the attribure on click listeners, setting variables and getting requests list
     * from parse database
     * onCreateView method overrided
     *
     * @param inflater LayoutInflater to inflate the layouts
     * @param container ViewGroup
     * @param savedInstanceState Bundle with previous states
     * @return View created in the screen
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_requests, container, false);
        showing = ((MainActivity) getActivity()).getShowing();
        requestsTypeLabel = (TextView) view.findViewById(R.id.request_type_label);
        userRequests = (ListView) view.findViewById(R.id.userRequests);
        if (showing == 1) {
            requestsTypeLabel.setText("Requests Accepted");
            fetching = "acceptedBy";
        } else {
            requestsTypeLabel.setText("Requests Posted");
            fetching = "RequestedBy";
        }

        // Creating a parse query, using which the data from parse database is accessed
        ParseQuery<Requests> query = new ParseQuery<Requests>("Requests");
        query.whereEqualTo(fetching, ParseUser.getCurrentUser().getUsername());

        // Parse Query runs in backround thus taking network access off the main thread
                query.findInBackground(new FindCallback<Requests>() {
                    /**
                     * method done is called once the callback from back4app is received
                     * @param list List of requests posted in back4app database
                     * @param e Any Parse Exception during the query execution in background
                     */
                    @Override
            public void done(List<Requests> list, ParseException e) {
                ArrayList<Request> lor = new ArrayList<Request>();
                for (Requests r : list) {

                    Request nr = new Request();

                    nr.setItem(r.getString("item"));
                    nr.setNeededBy(r.getString("neededBy"));
                    nr.setRequestedBy(r.getString("RequestedBy"));
                    nr.setRequestedOn(r.getString("requestedOn"));
                    nr.setPostedOn(r.getString("acceptedOn"));
                    System.out.println("Getting objectIds " + r.getObjectId());
                    nr.setObjectId(r.getObjectId());

                    lor.add(nr);
                }

        // Loading the requests posted list into the custom adapter
                ArrayAdapter adapter = new UserProfilePostedAdapter(getContext(), R.layout.user_request_adapter, R.id.row_request_item, lor);
                userRequests.setAdapter(adapter);
            }
        });


        return view;
    }

}
