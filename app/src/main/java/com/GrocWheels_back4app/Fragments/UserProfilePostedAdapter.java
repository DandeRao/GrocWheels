package com.GrocWheels_back4app.Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.GrocWheels_back4app.Model.Request;
import com.GrocWheels_back4app.R;

import java.util.ArrayList;


/**
 * UserProfilePostedAdapter class created to display the requests posted by user legibly.
 * Extends ArrayAdapter<Request>
 * Created by mkrao on 12/13/2016.
 */

public class UserProfilePostedAdapter extends ArrayAdapter<Request> {


    ArrayList<Request> requests;

    /**
     * 4 argument constructor
     * @param context Context of the application
     * @param resource R.id.resourceName, for the adapter to use
     * @param textViewResourceId a namesake textview resource id to passto the super classes constructor
     * @param objects List of requests the adapter should populate in the ListView
     */
    public UserProfilePostedAdapter(Context context, int resource, int textViewResourceId, ArrayList<Request> objects) {
        super(context, resource, textViewResourceId, objects);
        requests = objects;
    }

    /**
     * GetView Method to populate individual item in ListView
     * @param position position of the item in the listView
     * @param convertView old view to be used for editing
     * @param parent viewgroup the listview belongs to
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text1 = (TextView) view.findViewById(R.id.row_request_item);
        TextView text2 = (TextView) view.findViewById(R.id.row_request_requestor);
        TextView text3 = (TextView) view.findViewById(R.id.row_request_neededBy);
        TextView text4 = (TextView) view.findViewById(R.id.row_request_acceptedOn);

        System.out.println(requests.get(position).getItem());

        text1.setText(requests.get(position).getItem());
        text2.setText("Requested By: "+requests.get(position).getRequestedBy());
        text3.setText("Needed By: "+requests.get(position).getNeededBy());
        text4.setText("Accepted On: "+requests.get(position).getPostedOn());
        return view;
    }

}
