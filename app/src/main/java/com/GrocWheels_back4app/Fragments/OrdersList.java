package com.GrocWheels_back4app.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.GrocWheels_back4app.Fragments.Adapters.OrderAdapter;
import com.GrocWheels_back4app.Fragments.Adapters.StoreAdapter;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.Order;
import com.GrocWheels_back4app.Model.Store;
import com.GrocWheels_back4app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Requests List Class to show the requests once user logs into the application
 * Extends ActionBArHandler to handle actionBar
 */
public class OrdersList extends ActionBarItemsHandler {
    ListView ordersList;
    double maxDistance;
    SeekBar maxDistanceSeekBar;
    ArrayList<Order> listOfOrders = new ArrayList<>();
    /**
     * No Arg Constructor
     */
    public OrdersList() {
        // Required empty public constructor
    }

    /**
     * onCreateView method, Handles all the element hooks and their definitions
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        maxDistance = ((MainActivity) getActivity()).getMaxDistanceForSeeker();

       final View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
        ordersList = (ListView) view.findViewById(R.id.orders_list_view);

        //FetchRequests from back4app servers
        fetchOrders();
       // Setting clickable listView
        ordersList.setClickable(true);
        ordersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // On click get the position, save in mainactiviy to access it in later times.
//                ((MainActivity) getActivity()).setPosition(position);
//                ((MainActivity) getActivity()).setCurrentPosition(position);
              //  ((MainActivity) getActivity()).setSelectedStore(listOfStores.get(position));
                // Redirect to requestdetails fragment
           //     ((MainActivity) getActivity()).replaceFragment(new ProductsList(), true);
                showOrderDetails(position);
            }
        });

                return view;
    }
//Request Refresher (called when refresher is clicked on)
    public void refreshRequests(){

        fetchOrders();
    }

    /**
     * Method to fetch requests from back4app servers and add it to list in manactivity to access in other fragments
     */
    public void fetchOrders(){
        listOfOrders.clear();
    final ParseGeoPoint parsePoint = new ParseGeoPoint(((MainActivity) getActivity()).getCurrentLocation().getLatitude(),((MainActivity) getActivity()).getCurrentLocation().getLongitude());
        ((MainActivity) getActivity()).setUserLocation(parsePoint);
        // Query for fetching the requests
        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        System.out.println("Current Location: "+parsePoint.toString());
       query.whereEqualTo("orderedBy",ParseUser.getCurrentUser().getUsername());

        // Making query to fetch in background
        query.findInBackground(new FindCallback<Order>() {
        public void done(List<Order> list, ParseException e) {
            if (e == null) {
             //   Toast.makeText((MainActivity) getActivity(),list.size()+" stores fetched",Toast.LENGTH_SHORT).show();

                for(Order r:list){

                    listOfOrders.add(r);

                }

                Collections.reverse(listOfOrders);

              // Create adapter to display in custom listView
               ArrayAdapter adapter = new OrderAdapter(getContext(),R.layout.order_adapter,R.id.order_adapter_store_name,listOfOrders);
                ordersList.setAdapter(adapter);

            } else {
                // handle Parse Exception here
                Toast.makeText((MainActivity) getActivity(),"Error while fetching stores",Toast.LENGTH_SHORT).show();
            }
        }
    });

}

    public void showOrderDetails(final int position) {
        double totalCost = 0.0;

        String deliveryBy="Preparing for delivery";
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.order_summary);
        dialog.setTitle("Check Out");
        dialog.show();
        TextView noOfItemsTV = (TextView) dialog.findViewById(R.id.no_of_items_cart_order_list);
        TextView storeNameTV = (TextView) dialog.findViewById(R.id.store_name_order_list);
        TextView driverNameTV = (TextView) dialog.findViewById(R.id.delivery_by_driver_name);
        Button delivered = (Button) dialog.findViewById(R.id.delivered_button) ;
        Button back = (Button) dialog.findViewById(R.id.go_back_order_list) ;

        final Order o = listOfOrders.get(position);
        deliveryBy = o.getString("deliveryBy");
        noOfItemsTV.setText(o.getNumber("noOfItems")+"");
        storeNameTV.setText(o.getString("storeName"));
        driverNameTV.setText(deliveryBy);
        if(deliveryBy.equals("Preparing for delivery")) {
            delivered.setClickable(false);
        }else{
            delivered.setClickable(true);
        }

        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order o = listOfOrders.get(position);
                o.put("status",3);
                o.saveInBackground();
                dialog.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /**
     * Method to handle the Actionbar items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_sign_out:
                // Logout Line
                ParseUser.logOut();
                ((MainActivity) getActivity()).replaceFragment(new LoginFragment(),true);
                ((MainActivity) getActivity()).setGreenActionBar();
                return true;

            case R.id.add_request:
                ((MainActivity) getActivity()).replaceFragment(new OrdersList(), true);
                return (true);

            case R.id.refresh_request:
                //    Call Load Request Method
                refreshRequests();
                Toast toast = Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_LONG);
                toast.show();
                return (true);
        }

        return super.onOptionsItemSelected(item);
    }


}
