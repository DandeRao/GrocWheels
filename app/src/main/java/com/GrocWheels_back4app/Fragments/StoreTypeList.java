package com.GrocWheels_back4app.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.GrocWheels_back4app.Fragments.Adapters.ProductAdapter;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.CartItem;
import com.GrocWheels_back4app.Model.Product;
import com.GrocWheels_back4app.Model.Store;
import com.GrocWheels_back4app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Requests List Class to show the requests once user logs into the application
 * Extends ActionBArHandler to handle actionBar
 */
public class StoreTypeList extends ActionBarItemsHandler {
    ListView requestsList;
    double maxDistance;
     ArrayList<Product> listOfTypeOfMerchants = new ArrayList<>();
    FloatingActionButton cartFAB;
    TextView noProductsTV;
    /**
     * No Arg Constructor
     */
    public StoreTypeList() {
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
            View view = inflater.inflate(R.layout.fragment_products_list, container, false);
        requestsList = (ListView) view.findViewById(R.id.list_view_products_list);
  //FetchRequests from back4app servers
        fetchProducts();
       // Setting clickable listView

        noProductsTV = (TextView) view.findViewById(R.id.no_products_tv);



        requestsList.setClickable(true);
        requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // On click get the position, save in mainactiviy to access it in later times.
//                ((MainActivity) getActivity()).setPosition(position);
//                ((MainActivity) getActivity()).setCurrentPosition(positi
//
//  *****************Need to add the current product to Cart here**********
         //       ((MainActivity)getActivity()).addToCart(new CartItem(listOfProducts.get(position),1,getStoreName(listOfProducts.get(position).getStoreId())));

                // Redirect to requestdetails fragment
              //  ((MainActivity) getActivity()).replaceFragment(new RequestDetails(), true);
            }
        });

        cartFAB = (FloatingActionButton) view.findViewById(R.id.cart_fab_product_list);
        cartFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(new Cart(), true);
            }
        });
        return view;
    }
//Request Refresher (called when refresher is clicked on)
    public void refreshRequests(){

        fetchProducts();
    }

    /**
     * Method to fetch requests from back4app servers and add it to list in manactivity to access in other fragments
     */
    public void fetchProducts(){
        listOfTypeOfMerchants.clear();
       // Query for fetching the requests
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);

        query.whereEqualTo("storeId",((MainActivity) getActivity()).getSelectedStore().getNumber("storeId"));
        // Making query to fetch in background
        query.findInBackground(new FindCallback<Product>() {
        public void done(List<Product> list, ParseException e) {
            if (e == null) {
            //    Toast.makeText((MainActivity) getActivity(),list.size()+" items available",Toast.LENGTH_SHORT).show();

                for(Product r:list){
                    listOfTypeOfMerchants.add(r);
                }
                // Save fetched list to list in Mainactivity so that other fragments can access it
               // ((MainActivity) getActivity()).setStores(listOfProducts);
              // Create adapter to display in custom listView
               ArrayAdapter adapter = new ProductAdapter(getContext(),R.layout.product_adapter,R.id.row_product_name,listOfTypeOfMerchants);
                adapter.notifyDataSetChanged();
                requestsList.setAdapter(adapter);

                if(listOfTypeOfMerchants.size()==0){
                    noProductsTV.setVisibility(View.VISIBLE);
                }else{
                    noProductsTV.setVisibility(View.INVISIBLE);
                }
            } else {
                // handle Parse Exception here
                Toast.makeText((MainActivity) getActivity(),"Error while fetching products",Toast.LENGTH_SHORT).show();
            }
        }
    });

}

    public  String getStoreName(int storeId){
        String pg = "";
        for(Store s:((MainActivity) getActivity()).getStores()){
            if(s.getStoreId() == storeId){
                pg=s.getStoreName();
            }
        }

        return pg;
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
