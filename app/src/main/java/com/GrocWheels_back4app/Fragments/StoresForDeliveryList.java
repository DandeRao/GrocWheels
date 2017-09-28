package com.GrocWheels_back4app.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import com.GrocWheels_back4app.Fragments.Adapters.DeliveryAdapter;
import com.GrocWheels_back4app.Fragments.Adapters.StoreDeliveryAdapter;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.CartItem;
import com.GrocWheels_back4app.Model.Order;
import com.GrocWheels_back4app.Model.Store;
import com.GrocWheels_back4app.Model.StoreDelivery;
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
public class StoresForDeliveryList extends ActionBarItemsHandler {
    ListView deliveryList;
    double maxDistance;
    SeekBar maxDistanceSeekBar;
    ArrayList<Order> listOfOrders = new ArrayList<>();
    TextView milesHeading;
    ArrayList<StoreDelivery> listOfStoresAndOrders = new ArrayList<>();

    /**
     * No Arg Constructor
     */
    public StoresForDeliveryList() {
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
            ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        bar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
        bar.setDisplayShowTitleEnabled(true);
        maxDistance = ((MainActivity) getActivity()).getMaxDistanceForSeeker();

       final View view = inflater.inflate(R.layout.fragment_delivery_stores_list, container, false);
        deliveryList = (ListView) view.findViewById(R.id.list_view_delivery_store_list);
//Using Seekbar to set miles radius
        milesHeading = (TextView) view.findViewById(R.id.delivery_miles_seek_bar);
        milesHeading.setText("Showing stores within "+maxDistance+" miles");
        maxDistanceSeekBar = (SeekBar) view.findViewById(R.id.delivery_store_maxDistance);
        maxDistanceSeekBar.setProgress((int) maxDistance);
        maxDistanceSeekBar.setMax(75);
        System.out.println("Max distance is :"+maxDistance);
//On Seekbar Change listener
    maxDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        maxDistance = (double) progress;
        System.out.println("Max Distance set from slider progress to (Progress):"+progress);
        maxDistance = (maxDistance<=10.0?10.0:maxDistance);
        ((MainActivity) getActivity()).setMaxDistanceForSeeker(maxDistance);
        System.out.println("Max Distance set from slider progress to :"+((MainActivity) getActivity()).getMaxDistanceForSeeker());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(getContext(),"Showing deliveries within "+ (int) maxDistance + " miles.", Toast.LENGTH_SHORT).show();
        milesHeading.setText("Showing stores within "+maxDistance+" miles");
        refreshRequests();
    }
});
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
             //   showDeliveryDetails(position);
                ((MainActivity) getActivity()).setSelectedStoreForDeliveryId(listOfStoresAndOrders.get(position).getStoreName());
                ((MainActivity) getActivity()).replaceFragment(new DeliveryList(), true);
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

        // Query for fetching the requests
        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        System.out.println("Current Location: "+parsePoint.toString());
       query.whereWithinMiles("storeLocation",parsePoint,maxDistance);
        query.whereEqualTo("status",1);
        // Making query to fetch in background
        query.findInBackground(new FindCallback<Order>() {
        public void done(List<Order> list, ParseException e) {
            if (e == null) {
                Toast.makeText((MainActivity) getActivity(),list.size()+" stores fetched",Toast.LENGTH_SHORT).show();

                for(Order r:list){
                   // r.setDistance(calculateDistance(parsePoint,r.getStoreLocation()));
                    listOfOrders.add(r);
                }

                populateStoresList();
                // Save fetched list to list in Mainactivity so that other fragments can access it
              //  ((MainActivity) getActivity()).setStores(listOfStores);
              // Create adapter to display in custom listView
               ArrayAdapter adapter = new StoreDeliveryAdapter(getContext(),R.layout.delivery_store_adapter,R.id.row_delivery_stores_list,listOfStoresAndOrders);
                deliveryList.setAdapter(adapter);

            } else {
                // handle Parse Exception here
                Toast.makeText((MainActivity) getActivity(),"Error while fetching stores",Toast.LENGTH_SHORT).show();
            }
        }
    });

}

public void populateStoresList(){

    listOfStoresAndOrders.clear();

    ArrayList<Integer> stores = getStores();

    for(int a:stores) {
        StoreDelivery s = new StoreDelivery();
        int noOfOrdersInStore = 0;
        for (Order o : listOfOrders) {
            if(a == (int) o.getNumber("orderToStoreId")) {
                s.setStoreName(o.getString("storeName"));
                s.setDistance(calculateDistance(((MainActivity) getActivity()).getUserLocation(), o.getParseGeoPoint("storeLocation")));
                for (Order p : listOfOrders) {
                    if (p.getString("storeName").equals(o.getString("storeName"))) {
                        noOfOrdersInStore++;
                    }
                }
                s.setNoOfOrders(noOfOrdersInStore);

            }
        }
        listOfStoresAndOrders.add(s);
    }
}


    public ArrayList<Integer>  getStores(){

        ArrayList<Integer> stores = new ArrayList<>();
        for(Order o:listOfOrders){
            if(!stores.contains((Integer) o.getNumber("orderToStoreId"))){
                stores.add((Integer) o.getNumber("orderToStoreId"));
            }
        }

        return stores;
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
                ((MainActivity) getActivity()).replaceFragment(new CurrentDeliveryList(), true);
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
