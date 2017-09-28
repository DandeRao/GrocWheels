package com.GrocWheels_back4app.Fragments.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.GrocWheels_back4app.Fragments.Cart;
import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.CartItem;
import com.GrocWheels_back4app.Model.Product;
import com.GrocWheels_back4app.Model.Store;
import com.GrocWheels_back4app.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * RequestAdapter class created to display the requests posted by user legibly.
 * Extends ArrayAdapter<Request>
 * Created by mkrao on 12/13/2016.
 */
public class CartAdapter extends ArrayAdapter<CartItem> {

    /**
     * 4 argument constructor
     * @param context Context of the application
     * @param resource R.id.resourceName, for the adapter to use
     * @param textViewResourceId a namesake textview resource id to passto the super classes constructor
     * @param objects List of requests the adapter should populate in the ListView
     */
    List<CartItem> products;
    public CartAdapter(Context context, int resource, int textViewResourceId, List<CartItem> objects) {
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
        TextView text1 = (TextView) view.findViewById(R.id.row_cart_product_name);
        TextView text2 = (TextView) view.findViewById(R.id.row_cart_product_price);
       TextView text3 = (TextView) view.findViewById(R.id.row_cart_product_order);
       TextView text4 = (TextView) view.findViewById(R.id.row_cart_store_name);
        // Interchanged the textboxes, first comes the units and next comes the total price
        double totalAmount = getDoubleInTwoDigits(products.get(position).getTotalAmount());



        text1.setText(products.get(position).getProduct().getProductName());
        text3.setText(totalAmount+" USD");
       text2.setText(products.get(position).getUnitsOrdered()+" units");
        text4.setText(products.get(position).getStoreName());

        return view;
    }

    public double getDoubleInTwoDigits(double number){

        return (new BigDecimal(number).setScale(2, RoundingMode.HALF_EVEN)).doubleValue();

    }


}
