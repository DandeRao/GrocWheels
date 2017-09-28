package com.GrocWheels_back4app.Model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by mkrao on 6/14/2017.
 */

public class StoreDelivery  {
    double distance=0.0;
    String storeName;
    int noOfOrders;

    public String getStoreName() {
        return storeName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getNoOfOrders() {
        return noOfOrders;
    }

    public void setNoOfOrders(int noOfOrders) {
        this.noOfOrders = noOfOrders;
    }
}
