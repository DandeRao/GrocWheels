package com.GrocWheels_back4app.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.GrocWheels_back4app.Fragments.Adapters.CurrentDeliveryAdapter;
import com.GrocWheels_back4app.Fragments.Adapters.DeliveryAdapter;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.Order;
import com.GrocWheels_back4app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Requests List Class to show the requests once user logs into the application
 * Extends ActionBArHandler to handle actionBar
 */
public class CurrentDeliveryList extends ActionBarItemsHandler {
    ListView deliveryList;

    ArrayList<Order> listOfOrders = new ArrayList<>();


    /**
     * No Arg Constructor
     */
    public CurrentDeliveryList() {
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

       final View view = inflater.inflate(R.layout.fragment_my_deliveries_list, container, false);
        deliveryList = (ListView) view.findViewById(R.id.deliveries_list_my_current_deliveries);
//Using Seekbar to set miles radius

  //FetchRequests from back4app servers
                fetchStores();
       // Setting clickable listView
        deliveryList.setClickable(true);
        deliveryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // On click get the position, save in mainactiviy to access it in later times.
//                ((MainActivity) getActivity()).setPosition(position);
//                ((MainActivity) getActivity()).setCurrentPosition(position);
              //  ((MainActivity) getActivity()).setSelectedStore(listOfStores.get(position));
                // Redirect to requestdetails fragment
             //   ((MainActivity) getActivity()).replaceFragment(new ProductsList(), true);
                showDeliveryDetails(position);
            }
        });



        return view;
    }
//Request Refresher (called when refresher is clicked on)
    public void refreshRequests(){

        fetchStores();
    }

    /**
     * Method to fetch requests from back4app servers and add it to list in manactivity to access in other fragments
     */
    public void fetchStores(){
        listOfOrders.clear();
    final ParseGeoPoint parsePoint = new ParseGeoPoint(((MainActivity) getActivity()).getCurrentLocation().getLatitude(),((MainActivity) getActivity()).getCurrentLocation().getLongitude());
        ((MainActivity) getActivity()).setUserLocation(parsePoint);

        // Use in case if u want orders with all the statuses
//        ParseQuery<Order> query1 = ParseQuery.getQuery(Order.class);
//        ParseQuery<Order> query2 = ParseQuery.getQuery(Order.class);
//
//        System.out.println("Current Location: "+parsePoint.toString());
//        query1.whereWithinMiles("storeLocation",parsePoint,maxDistance);
//        query1.whereEqualTo("status",0);
//        query2.whereWithinMiles("storeLocation",parsePoint,maxDistance);
//        query2.whereEqualTo("status",1);
//
//        List<ParseQuery<Order>> queries = new ArrayList<ParseQuery<Order>>();
//        queries.add(query1);
//        queries.add(query2);
//        ParseQuery<Order> query = ParseQuery.or(queries);
//
//
        // Query for fetching the requests
        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        System.out.println("Current Location: "+parsePoint.toString());

        query.whereEqualTo("deliveryBy",ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("status",2);

        // Making query to fetch in background
        query.findInBackground(new FindCallback<Order>() {
        public void done(List<Order> list, ParseException e) {
            if (e == null) {
             //   Toast.makeText((MainActivity) getActivity(),list.size()+" stores fetched",Toast.LENGTH_SHORT).show();

                for(Order r:list){
                   // r.setDistance(calculateDistance(parsePoint,r.getStoreLocation()));
                    listOfOrders.add(r);
                }
                // Save fetched list to list in Mainactivity so that other fragments can access it
              //  ((MainActivity) getActivity()).setStores(listOfStores);
              // Create adapter to display in custom listView
               ArrayAdapter adapter = new CurrentDeliveryAdapter(getContext(),R.layout.current_delivery_adapter,R.id.contact_name,listOfOrders);
                deliveryList.setAdapter(adapter);

            } else {
                // handle Parse Exception here
                Toast.makeText((MainActivity) getActivity(),"Error while fetching stores",Toast.LENGTH_SHORT).show();
            }
        }
    });

}


    public static Double calculateDistance(ParseGeoPoint currentLocation, ParseGeoPoint storeLocation) {
        final int R = 6371; // Radious of the earth
        Double lat1 = currentLocation.getLatitude();
        Double lon1 = currentLocation.getLongitude();
        Double lat2 = storeLocation.getLatitude();
        Double lon2 = storeLocation.getLongitude();
        Double latDistance = toRad(lat2 - lat1);
        Double lonDistance = toRad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distanceinmeters = R * c * 1000*0.000621371;
        BigDecimal bd = new BigDecimal(distanceinmeters).setScale(2, RoundingMode.HALF_EVEN);
        distanceinmeters=bd.doubleValue();
        return distanceinmeters;

    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public void showDeliveryDetails(int position){
        final Order orderItem = listOfOrders.get(position);
       final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_driver_orders_details);
        dialog.setTitle("Check Out");
        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView storeName = (TextView) dialog.findViewById(R.id.driver_pick_up_store_name);
        TextView distanceToStore = (TextView) dialog.findViewById(R.id.distance_pick);
        TextView deliveryTo = (TextView) dialog.findViewById(R.id.delivery_to);
        TextView deliveryNoOfItems = (TextView) dialog.findViewById(R.id.delivery_no_items);
        TextView deliveryDistance = (TextView) dialog.findViewById(R.id.delivery_distance);
        TextView deliveryPay = (TextView) dialog.findViewById(R.id.delivery_pay);

        storeName.setText(orderItem.getString("storeName"));
        distanceToStore.setText(calculateDistance(orderItem.getParseGeoPoint("storeLocation"),((MainActivity) getActivity()).getUserLocation())+" miles");
        deliveryTo.setText(orderItem.getString("orderedBy"));
        deliveryNoOfItems.setText(orderItem.getNumber("noOfItems")+ "");
        deliveryDistance.setText(orderItem.getNumber("deliveryDistance")+"");
        deliveryPay.setText(orderItem.getNumber("deliveryCharge")+"");


        Button accept  = (Button) dialog.findViewById(R.id.accept_delivery_button);
        Button back = (Button) dialog.findViewById(R.id.go_back);
        accept.setVisibility(View.GONE);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // logic to accept delivery

                orderItem.put("deliveryBy",ParseUser.getCurrentUser().getUsername());
                orderItem.saveInBackground();
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
