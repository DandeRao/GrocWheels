package com.GrocWheels_back4app.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

/**
 * Created by mkrao on 6/14/2017.
 */

@ParseClassName("Orders")
public class Order  extends ParseObject {
    JSONArray items;
    double totalAmount;
    String orderedBy;
    int orderToStoreId;
    int status;
    int noOfItems;
    double distance;

    String deliveryBy;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }



    public JSONArray getItems() {
        return items;
    }

    public void setItems(JSONArray items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(String orderedBy) {
        this.orderedBy = orderedBy;
    }

    public int getOrderToStoreId() {
        return orderToStoreId;
    }

    public void setOrderToStoreId(int orderToStoreId) {
        this.orderToStoreId = orderToStoreId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public String getDeliveryBy() {
        return deliveryBy;
    }

    public void setDeliveryBy(String deliveryBy) {
        this.deliveryBy = deliveryBy;
    }
}
