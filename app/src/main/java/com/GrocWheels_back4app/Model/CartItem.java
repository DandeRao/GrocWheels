package com.GrocWheels_back4app.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mkrao on 6/14/2017.
 */


public class CartItem {
Product product;
    int unitsOrdered;
    double totalAmount;
    String storeName;

    public CartItem(Product product, int unitsOrdered, String storeName) {
        this.product = product;
        this.unitsOrdered = unitsOrdered;
        totalAmount = product.getProductPrice()*unitsOrdered;
        this.storeName = storeName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getUnitsOrdered() {
        return unitsOrdered;
    }

    public void setUnitsOrdered(int unitsOrdered) {
        this.unitsOrdered = unitsOrdered;
        this.totalAmount = product.getProductPrice()*unitsOrdered;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
