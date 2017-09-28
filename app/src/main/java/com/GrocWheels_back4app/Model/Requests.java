package com.GrocWheels_back4app.Model;
/**
 * Requests class to act as a entity for parse requests table
 * Extends ParseObject Class
 */

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Requests")
public class Requests extends ParseObject{

    String item;

    /**
     * Default constructor
     */
    public Requests() {
    }

    /**
     * Getter for item attribute
     * @return item
     */
    public String getItem() {
        return item;
    }

    /**
     * Setter for item attribute
     * @param item
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * toString method to use in regular ListView Adapters.
     * @return
     */
    @Override
    public String toString() {
        return  "Requested item: " + getString("item")+ "\n"+
                "Requested By(User): " + getString("RequestedBy") + '\n' +
                "Accepted On: "+getString("acceptedOn")+ '\n' +
                "Needed by: "+getString("neededBy");
    }
}
