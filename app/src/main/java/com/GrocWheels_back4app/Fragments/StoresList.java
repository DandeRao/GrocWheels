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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.GrocWheels_back4app.Fragments.Adapters.StoreAdapter;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.Order;
import com.GrocWheels_back4app.Model.Store;
import com.GrocWheels_back4app.R;
import com.parse.FindCallback;
import com.parse.Parse;
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
public class StoresList extends ActionBarItemsHandler {
    ListView requestsList;
    double maxDistance;
    SeekBar maxDistanceSeekBar;
    ArrayList<Store> listOfStores = new ArrayList<Store>();
    FloatingActionButton cartFAB;
    FloatingActionButton searchFAB;
    TextView milesHeading;
    EditText storeNameET;
    /**
     * No Arg Constructor
     */
    public StoresList() {
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

       final View view = inflater.inflate(R.layout.fragment_stores_list, container, false);
        requestsList = (ListView) view.findViewById(R.id.list_view_stores_list);
//Using Seekbar to set miles radius
        milesHeading = (TextView) view.findViewById(R.id.miles_heading);
        milesHeading.setText("Showing stores within "+maxDistance+" miles");
        maxDistanceSeekBar = (SeekBar) view.findViewById(R.id.maxDistance);
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
        Toast.makeText(getContext(),"Showing stores within "+ (int) maxDistance + " miles.", Toast.LENGTH_SHORT).show();
        milesHeading.setText("Showing stores within "+maxDistance+" miles");
        refreshRequests();
    }
});
  //FetchRequests from back4app servers
                fetchStores("initial");
       // Setting clickable listView
        requestsList.setClickable(true);
        requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // On click get the position, save in mainactiviy to access it in later times.
//                ((MainActivity) getActivity()).setPosition(position);
//                ((MainActivity) getActivity()).setCurrentPosition(position);
                ((MainActivity) getActivity()).setSelectedStore(listOfStores.get(position));
                // Redirect to requestdetails fragment
                ((MainActivity) getActivity()).replaceFragment(new ProductsList(), true);
            }
        });

        cartFAB = (FloatingActionButton) view.findViewById(R.id.cart_fab_stores_list);
        cartFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(new Cart(), true);
            }
        });

        searchFAB = (FloatingActionButton) view.findViewById(R.id.search_fab_stores);
        searchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_search_store);
                ((TextView) dialog.findViewById(R.id.search_title)).setText("Search Store");
                dialog.show();
                storeNameET = (EditText) dialog.findViewById(R.id.dialog_store_name);
                Button search = (Button) dialog.findViewById(R.id.dialog_search_store) ;
                Button back = (Button) dialog.findViewById(R.id.dialog_back_to_stores_list) ;

                if(storeNameET.getText().toString().equals("")) {
                    search.setClickable(false);
                }else{
                    search.setClickable(true);
                }
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                       fetchStores(storeNameET.getText().toString());
                    }
                });
            }
        });

        return view;
    }
//Request Refresher (called when refresher is clicked on)
    public void refreshRequests(){

        fetchStores("initial");
    }

    /**
     * Method to fetch requests from back4app servers and add it to list in manactivity to access in other fragments
     */
    public void fetchStores(String searchString){
        listOfStores.clear();
    final ParseGeoPoint parsePoint = new ParseGeoPoint(((MainActivity) getActivity()).getCurrentLocation().getLatitude(),((MainActivity) getActivity()).getCurrentLocation().getLongitude());
        ((MainActivity) getActivity()).setUserLocation(parsePoint);
        // Query for fetching the requests
        ParseQuery<Store> query = ParseQuery.getQuery(Store.class);
        if(!searchString.equals("initial") && !searchString.equals("")) {
            query.whereContains("name", searchString);
        }
        System.out.println("Current Location: "+parsePoint.toString());
       query.whereWithinMiles("location",parsePoint,maxDistance);

        // Making query to fetch in background
        query.findInBackground(new FindCallback<Store>() {
        public void done(List<Store> list, ParseException e) {
            if (e == null) {
              //  Toast.makeText((MainActivity) getActivity(),list.size()+" stores fetched",Toast.LENGTH_SHORT).show();

                for(Store r:list){
                    r.setDistance(calculateDistance(parsePoint,r.getStoreLocation()));
                    listOfStores.add(r);
                }
                // Save fetched list to list in Mainactivity so that other fragments can access it
                ((MainActivity) getActivity()).setStores(listOfStores);
              // Create adapter to display in custom listView
               ArrayAdapter adapter = new StoreAdapter(getContext(),R.layout.store_adapter,R.id.row_store_name,listOfStores);
                requestsList.setAdapter(adapter);

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
