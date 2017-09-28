package com.GrocWheels_back4app.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.GrocWheels_back4app.Fragments.Adapters.CartAdapter;
import com.GrocWheels_back4app.Fragments.Adapters.ProductAdapter;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.CartItem;
import com.GrocWheels_back4app.Model.Delivery;
import com.GrocWheels_back4app.Model.Order;
import com.GrocWheels_back4app.Model.Product;
import com.GrocWheels_back4app.Model.Store;
import com.GrocWheels_back4app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.mail.event.StoreListener;

/**
 * Requests List Class to show the requests once user logs into the application
 * Extends ActionBArHandler to handle actionBar
 */
public class Cart extends ActionBarItemsHandler {
    ListView requestsList;
    double maxDistance;
     ArrayList<CartItem> productsInCart = new ArrayList<>();
    ArrayList<Order> orders = new ArrayList<>();
    FloatingActionButton checkOut;
    TextView noItemsTV;
    /**
     * No Arg Constructor
     */
    public Cart() {
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
            View view = inflater.inflate(R.layout.fragment_cart_product_list, container, false);
        requestsList = (ListView) view.findViewById(R.id.list_view_cart);
noItemsTV = (TextView) view.findViewById(R.id.no_items_in_cart_tv);
  //FetchRequests from back4app servers
        fetchCart();
       // Setting clickable listView
        requestsList.setClickable(true);
        requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // On click get the position, save in mainactiviy to access it in later times.
//                ((MainActivity) getActivity()).setPosition(position);
//                ((MainActivity) getActivity()).setCurrentPosition(positi
//
//  *****************Need to add the current product to Cart here**********
                //  ((MainActivity) getActivity()).setSelectedStore(listOfProducts.get(position));
                // Redirect to requestdetails fragment
               // ((MainActivity) getActivity()).replaceFragment(new RequestDetails(), true);
            }
        });


        checkOut = (FloatingActionButton) view.findViewById(R.id.check_out_fab);
        if(productsInCart.size()==0){
            checkOut.setVisibility(View.INVISIBLE);
            noItemsTV.setVisibility(View.VISIBLE);
        }else{
            checkOut.setVisibility(View.VISIBLE);
            noItemsTV.setVisibility(View.INVISIBLE);
        }
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                placeOrder();
//                placeDeliveryRequest();
//                productsInCart.clear();

                showCheckOutDialog();
            }
        });


        return view;
    }
//Request Refresher (called when refresher is clicked on)
    public void refreshRequests(){

        fetchCart();
    }

    public void showCheckOutDialog(){
        double totalCost=0.0;
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_checkout_summary);
        dialog.setTitle("Check Out");
        dialog.show();
        TextView noOfItemsTV = (TextView) dialog.findViewById(R.id.no_of_items_cart);
        TextView totalCostTV = (TextView) dialog.findViewById(R.id.total_cost_cart);
        TextView deliveryChargesTV = (TextView) dialog.findViewById(R.id.delivery_charges_tv);
        TextView costOfItemsTV = (TextView) dialog.findViewById(R.id.cost_of_items);

        int noOfItems =0;
        double deliveryCharge = 0.0;
        double totalAmountOfCart=0.0;
        for(CartItem c:productsInCart){
            noOfItems+=c.getUnitsOrdered();
            totalAmountOfCart+=c.getTotalAmount();
        }

        ArrayList<Integer> stores= getNumberOfOrders();
        for(int i=0;i<stores.size();i++){
            double dcForOneStore = 0.0;
            int numberOfItemsForOneStore =0;
            ParseGeoPoint storeLocation = getStoreLocation(stores.get(i));
            for(CartItem c:productsInCart){
                if(c.getProduct().getStoreId()== stores.get(i)) {
                    numberOfItemsForOneStore++;
                }
            }
            dcForOneStore = calculateDeliveryCharge(storeLocation,numberOfItemsForOneStore);
            deliveryCharge+= dcForOneStore;
        }

        // Add logic to show only two digits total
        BigDecimal bd = new BigDecimal(totalAmountOfCart).setScale(2, RoundingMode.HALF_EVEN);
        totalAmountOfCart = bd.doubleValue();

        noOfItemsTV.setText(noOfItems+"");
        costOfItemsTV.setText("$"+totalAmountOfCart+"");
        deliveryChargesTV.setText("$"+deliveryCharge+"");
        totalCost = totalAmountOfCart+deliveryCharge;
        BigDecimal bd2 = new BigDecimal(totalCost).setScale(2, RoundingMode.HALF_EVEN);
        totalCost = bd2.doubleValue();
        totalCostTV.setText("$"+totalCost+"");
       Button pay  = (Button) dialog.findViewById(R.id.dialog_check_out);
        Button back = (Button) dialog.findViewById(R.id.dialog_continue_shopping);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog placingOrder =  new ProgressDialog(getActivity());
                placingOrder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                placingOrder.setMessage("Placing Order. \nPlease wait...");
                placingOrder.setIndeterminate(true);
                placingOrder.setCanceledOnTouchOutside(false);
                placingOrder.show();
                dialog.dismiss();
                placeOrder();
              //  placeDeliveryRequest();
                productsInCart.clear();

                placingOrder.dismiss();
                Toast.makeText(getActivity(), "Order Placed successfully", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).replaceFragment(new OrdersList(), true);
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
     * Method to fetch requests from back4app servers and add it to list in manactivity to access in other fragments
     */
    public void fetchCart(){

        this.productsInCart =((MainActivity) getActivity()).getCart();
        int noOfItems =0;
        double deliveryCharge = 0.0;
        double totalAmountOfCart=0;
        for(CartItem c:productsInCart){
            noOfItems+=c.getUnitsOrdered();
            totalAmountOfCart+=c.getTotalAmount();
        }
        BigDecimal bd = new BigDecimal(totalAmountOfCart).setScale(2, RoundingMode.HALF_EVEN);
        totalAmountOfCart = bd.doubleValue();
        ArrayList<Integer> stores= getNumberOfOrders();
        for(int i=0;i<stores.size();i++){
            double dcForOneStore = 0.0;
            int numberOfItemsForOneStore =0;
            ParseGeoPoint storeLocation = getStoreLocation(stores.get(i));
            for(CartItem c:productsInCart){
                if(c.getProduct().getStoreId()== stores.get(i)) {
                   numberOfItemsForOneStore++;
                }
            }
            dcForOneStore = calculateDeliveryCharge(storeLocation,numberOfItemsForOneStore);
            deliveryCharge+= dcForOneStore;
        }

        ArrayAdapter adapter = new CartAdapter(getContext(),R.layout.cart_adapter,R.id.row_cart_product_name,productsInCart);
        requestsList.setAdapter(adapter);
}

public void placeOrder(){

    ArrayList<Integer> stores= getNumberOfOrders();


    for(int i=0;i<stores.size();i++){
        ParseGeoPoint storeLocation = getStoreLocation(stores.get(i));
       Order newOrder = new Order();
        JSONArray itemsArray = new JSONArray();
        double totalAmount =0.0;
        double deliveryCharge = 0.0;
        int noOfItems=0;
        String orderedBy = ParseUser.getCurrentUser().getUsername();
        int orderToStoreId = stores.get(i);
        int status =0;
        for(CartItem c:productsInCart){
            if(c.getProduct().getStoreId()== stores.get(i)) {
                itemsArray.put(getJSONObject(c));
                totalAmount += c.getTotalAmount();
                noOfItems +=c.getUnitsOrdered();
            }
        }

        deliveryCharge = calculateDeliveryCharge(storeLocation,noOfItems);
        totalAmount = totalAmount+deliveryCharge;
        BigDecimal bd = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_EVEN);
        totalAmount = bd.doubleValue();
        // newOrder.increment("orderId");
        newOrder.put("items",itemsArray);
        newOrder.put("totalAmount",totalAmount);
        newOrder.put("orderedBy",orderedBy);
        newOrder.put("orderToStoreId",orderToStoreId);
        newOrder.put("status",status);
        newOrder.put("noOfItems",noOfItems);
        newOrder.put("storeLocation", storeLocation);
        newOrder.put("storeName", getStoreName(stores.get(i)));
        newOrder.put("deliveryBy","Preparing for delivery");
        newOrder.put("deliveryCharge",calculateDeliveryCharge(storeLocation,noOfItems));
        newOrder.put("deliveryDistance",getStoreDistance(stores.get(i)));
        newOrder.put("orderContact",ParseUser.getCurrentUser().get("contact"));

        newOrder.setItems(itemsArray);
        newOrder.setNoOfItems(noOfItems);
        newOrder.setOrderedBy(orderedBy);
        newOrder.setOrderToStoreId(orderToStoreId);
        newOrder.setTotalAmount(totalAmount);

        orders.add(newOrder);
    }

     for(Order o:orders){
        o.increment("orderId");
        o.saveInBackground();

    }



}
    public ParseGeoPoint getStoreLocation(int storeId){
        ParseGeoPoint pg = new ParseGeoPoint(0,0);
        for(Store s:((MainActivity) getActivity()).getStores()){
            if(s.getStoreId() == storeId){
              pg=s.getStoreLocation();
            }
        }

        return pg;
    }

    public String getStoreName(int storeId){
        String pg = "";
        for(Store s:((MainActivity) getActivity()).getStores()){
            if(s.getStoreId() == storeId){
                pg=s.getStoreName();
            }
        }

        return pg;
    }

    public double getStoreDistance(int storeId){
        double pg = 0.0;
        for(Store s:((MainActivity) getActivity()).getStores()){
            if(s.getStoreId() == storeId){
                pg=s.getDistance();
            }
        }

        return pg;
    }

    public  double calculateDeliveryCharge(ParseGeoPoint storeLocation,int noOfItems){
       double distance = StoresList.calculateDistance(storeLocation,((MainActivity)getActivity()).getUserLocation());
        System.out.println("GD distance :"+distance+" noOfItems: "+noOfItems);
        double chargeBasedOnNumberOfItems=10;
        double chargeBasedOnDistance = 10;
        if(noOfItems>20){
            chargeBasedOnNumberOfItems = 20;
        }else if(noOfItems > 10 && noOfItems < 20 ){
            chargeBasedOnNumberOfItems =15;
        }else if(noOfItems <=10){
            chargeBasedOnNumberOfItems = 10;
        }

        if(distance >10) {
            chargeBasedOnDistance = 40;
        }else if(distance > 5&& distance <=10){
            chargeBasedOnDistance = 30;
        }else if(distance <=5){
            chargeBasedOnDistance=20;
        }
        System.out.println("GD distance charge: "+chargeBasedOnDistance+" items charge: "+chargeBasedOnNumberOfItems);
        double totalAmount = (chargeBasedOnDistance+chargeBasedOnNumberOfItems) / 100;
       // Following steps so that the double will have only two decimals.
        BigDecimal bd = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_EVEN);
        totalAmount = bd.doubleValue();
        return totalAmount;

    }

public JSONObject getJSONObject(CartItem p){
    JSONObject productJSON = new JSONObject();
    try{
        productJSON.put("name",p.getProduct().getProductName());
        productJSON.put("unitsOrdered",p.getUnitsOrdered());
        productJSON.put("totalAmount",p.getTotalAmount());

    }catch (JSONException e){
        e.printStackTrace();
    }

    return productJSON;
}
public ArrayList<Integer>  getNumberOfOrders(){

    ArrayList<Integer> stores = new ArrayList<>();
    for(CartItem c:productsInCart){
        if(!stores.contains(c.getProduct().getStoreId())){
            stores.add(c.getProduct().getStoreId());
        }
    }

    return stores;
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
