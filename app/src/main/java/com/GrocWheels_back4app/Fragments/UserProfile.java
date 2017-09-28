package com.GrocWheels_back4app.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.Requests;
import com.GrocWheels_back4app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfile extends ActionBarItemsHandler {

    TextView userName;
    TextView email;
    TextView requestsAccepted;
    TextView requestsPosted;
    public UserProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userName = (TextView) view.findViewById(R.id.userName);
        userName.setText(ParseUser.getCurrentUser().getUsername());
        email = (TextView) view.findViewById(R.id.emailId_textView);
        email.setText(ParseUser.getCurrentUser().getEmail());
       // requestsAccepted = (TextView) view.findViewById(R.id.numberOfRequestsAccepted);
        requestsPosted = (TextView) view.findViewById(R.id.numberOfRequests);
        ParseQuery<Requests> query = new ParseQuery<Requests>("Requests");
        query.whereEqualTo("RequestedBy", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<Requests>() {
            public void done(List<Requests> list, ParseException e) {
                if (e == null) {

                    //   System.out.println("got from server"+list.size());
                    //   System.out.println(Arrays.toString(list.toArray()));
                    //    ((MainActivity) getActivity()).setRequests(list);
                    requestsPosted.setText(" "+list.size()+"");

                    }
                    //((MainActivity) getActivity()).setRequests(listOfRequests);
                    /*ArrayAdapter adapter = new ArrayAdapter<Request>( getContext(),android.R.layout.simple_list_item_1,listOfRequests);
                    requestsList.setAdapter(adapter);*/

                }

        });

        ParseQuery<Requests> query1 = new ParseQuery<Requests>("Requests");
        query1.whereEqualTo("acceptedBy", ParseUser.getCurrentUser().getUsername());
        query1.findInBackground(new FindCallback<Requests>() {
            public void done(List<Requests> list, ParseException e) {
                if (e == null) {

                    //   System.out.println("got from server"+list.size());
                    //   System.out.println(Arrays.toString(list.toArray()));
                    //    ((MainActivity) getActivity()).setRequests(list);
                    requestsAccepted.setText(" "+list.size());

                }
                //((MainActivity) getActivity()).setRequests(listOfRequests);
                    /*ArrayAdapter adapter = new ArrayAdapter<Request>( getContext(),android.R.layout.simple_list_item_1,listOfRequests);
                    requestsList.setAdapter(adapter);*/

            }

        });

        requestsAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setShowing(1);
                ((MainActivity) getActivity()).replaceFragment(new UserRequests(),true);
            }
        });
        requestsPosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setShowing(0);
                ((MainActivity) getActivity()).replaceFragment(new UserRequests(),true);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

}
