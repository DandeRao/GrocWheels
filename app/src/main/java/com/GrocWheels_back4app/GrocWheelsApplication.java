package com.GrocWheels_back4app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.GrocWheels_back4app.Model.Delivery;
import com.GrocWheels_back4app.Model.Order;
import com.GrocWheels_back4app.Model.Product;
import com.GrocWheels_back4app.Model.Requests;
import com.GrocWheels_back4app.Model.Store;
import com.onesignal.OneSignal;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Android Application Class to declare parse values and initialize the application.
 * ALso registers the device for push notifications
 * Created by mkrao on 11/20/2016.
 */

public class GrocWheelsApplication extends Application {

    private static GrocWheelsApplication instance = new GrocWheelsApplication();
    public static Context getContext(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();

         ParseObject.registerSubclass(Order.class);
         ParseObject.registerSubclass(Product.class);
         ParseObject.registerSubclass(Store.class);
         ParseObject.registerSubclass(Delivery.class);

// Parse Initialization
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("PhR4sjHsQ0EsEWmkXJxfY0sZggwbB6OknUeJOVrd")
                .clientKey("ChcJVuSrMGLIlLScJAdApPu19vWePVDW9bzBhFKT")
                .server("https://parseapi.back4app.com/").build()
        );

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Log.d("Parse","Initialized");
// Installation declaration and setup to receive Push notifications from Google Cloud Messaging services.
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "855353218401");
        installation.saveInBackground();
        OneSignal.startInit(this).init();

// Subscribing for parse channels
        ParsePush.subscribeInBackground("Requests", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Log.d("Parse","Success");
                else
                    Log.d("Parse","Failed");
            }
        });
    }
}
