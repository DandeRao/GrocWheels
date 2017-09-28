package com.GrocWheels_back4app.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mkrao on 6/14/2017.
 */

@ParseClassName("Products")
public class Product  extends ParseObject {

    public String getProductName(){
        return getString("name");
    }

    public String getProductCurrency(){
        return getString("currency");
    }

    public double getProductPrice(){
        return getDouble("price");
    }

    public int getProductUnitsAvailable(){
        return getInt("unitsAvailable");
    }

    public int getStoreId() {return getInt("storeId");}
}
