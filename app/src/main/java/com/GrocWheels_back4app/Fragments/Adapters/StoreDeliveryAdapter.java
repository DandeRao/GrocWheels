package com.GrocWheels_back4app.Fragments.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.GrocWheels_back4app.Model.Order;
import com.GrocWheels_back4app.Model.Store;
import com.GrocWheels_back4app.Model.StoreDelivery;
import com.GrocWheels_back4app.R;

import java.util.List;

/**
 * RequestAdapter class created to display the requests posted by user legibly.
 * Extends ArrayAdapter<Request>
 * Created by mkrao on 12/13/2016.
 */
public class StoreDeliveryAdapter extends ArrayAdapter<StoreDelivery> {

    /**
     * 4 argument constructor
     * @param context Context of the application
     * @param resource R.id.resourceName, for the adapter to use
     * @param textViewResourceId a namesake textview resource id to passto the super classes constructor
     * @param objects List of requests the adapter should populate in the ListView
     */
    List<StoreDelivery> products;
    public StoreDeliveryAdapter(Context context, int resource, int textViewResourceId, List<StoreDelivery> objects) {
        super(context, resource, textViewResourceId, objects);
        products = objects;
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
        TextView text1 = (TextView) view.findViewById(R.id.row_delivery_stores_list);
        TextView text2 = (TextView) view.findViewById(R.id.row_delivery_distance);
       TextView text3 = (TextView) view.findViewById(R.id.row_delivery_store_no_of_items);



        text1.setText(products.get(position).getStoreName());
        text2.setText(products.get(position).getDistance()+" mi");
       text3.setText(products.get(position).getNoOfOrders()+" Deliveries");


        return view;
    }



}
