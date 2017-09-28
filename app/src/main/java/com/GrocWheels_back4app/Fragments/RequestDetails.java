package com.GrocWheels_back4app.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.Model.Request;
import com.GrocWheels_back4app.Model.Requests;
import com.GrocWheels_back4app.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Request Details Fragment Extends actionbarItems for the actionbar handlers
 * A simple {@link Fragment} subclass.
 */
public class RequestDetails extends ActionBarItemsHandler {
    EditText item;
    EditText neededBy;
    EditText requestedBy;
    EditText requestedOn;
    Button goBack;
    Button acceptRequest;
    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage generateMailMessage;

    int currrentRequestPosition;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    int listEnd ;

    // Gesture detector for detecting the swiping events to traverse through the requests
       final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                /**
                 * On Fling event handler
                 * @param e1 event1
                 * @param e2 event2
                 * @param velocityX velocity on horizontal direction
                 * @param velocityY velocity on vertical direction
                 * @return true or false based on whether event consumed or not
                 */
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    Log.i("On Fling", "onFling has been called!");
                    final int SWIPE_MIN_DISTANCE = 120;
                    final int SWIPE_MAX_OFF_PATH = 250;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;
                    try {
                        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                            return false;
                        // TO detect right to left fling
                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            Log.i("On Right to Left", "Right to Left");
                            onSwipeLeft();

                        }
                        // To detect left to right flings
                        else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            Log.i("On Left to Right", "Left to Right");
                            onSwipeRight();
                        }
                    } catch (Exception e) {
                        // nothing
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });

    public RequestDetails() {
        // Required empty public constructor
    }

    /**
     * On CreateView for creating view and handling items
     * @param inflater inflater for inflating layout
     * @param container viewgroup in the view
     * @param savedInstanceState Bundle with previous state(if any)
     * @return View created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_request_details, container, false);
        setHasOptionsMenu(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        // Get current position to load the request
        currrentRequestPosition  = ((MainActivity) getActivity()).getCurrentPosition();
        listEnd = ((MainActivity) getActivity()).getRequests().size();
        item = (EditText) view.findViewById(R.id.item);
        neededBy = (EditText) view.findViewById(R.id.neededBy);
        requestedBy = (EditText) view.findViewById(R.id.requestedBy);
        requestedOn = (EditText) view.findViewById(R.id.requestedOn);
// Button to take to previous page
        goBack = (Button) view.findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(new StoresList(),true);
            }
        });
// Button to accept a request
        acceptRequest = (Button) view.findViewById(R.id.accept);

        acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Position is "+ currrentRequestPosition);
                System.out.println("Object Id is: "+((MainActivity)getActivity()).getRequests().get(currrentRequestPosition).getObjectId());
                ParseQuery<Requests> query = ParseQuery.getQuery("Requests");



                query.getInBackground(((MainActivity)getActivity()).getRequests().get(currrentRequestPosition).getObjectId(), new GetCallback<Requests>() {
                    public void done(final Requests currentRequest, ParseException e) {
                        if (e == null) {
                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                            Date dateobj = new Date();
                            currentRequest.put("isAccepted", true);
                            currentRequest.put("acceptedBy", ParseUser.getCurrentUser().getUsername());
                            currentRequest.put("acceptedOn",df.format(dateobj));
                            currentRequest.saveInBackground();
                            // ASYNCTASK to generate email and send to requestor and acceptor
                             new AsyncTask<Void, Integer,Integer>() {
                                @Override
                                protected Integer doInBackground(Void... params) {
                                    try {
                                        generateAndSendEmailToAcceptor(currentRequest);
                                        generateAndSendEmailToRequestor(currentRequest);
                                    } catch (MessagingException e1) {
                                        e1.printStackTrace();
                                    }
                                    return 0;
                                }


                            }.execute();

                        }
                        else{e.printStackTrace();}
                    }
                });
                ((MainActivity) getActivity()).replaceFragment(new StoresList(),true);
            }
        });
        // Set listview with an adapter and view
        loadRequestDetails(((MainActivity)getActivity()).getCurrentPosition());
        return view;
    }


    /**
     * Setting Listview with requests
     * @param position
     */
    public void loadRequestDetails(int position){
        System.out.println("Position is "+position);
        Request req = ((MainActivity) getActivity()).getRequests().get(position);
        item.setText(req.getItem());
        neededBy.setText(req.getNeededBy());
        requestedBy.setText(req.getRequestedBy());
        requestedOn.setText(req.getRequestedOn());
        if(req.getRequestedBy().equals(ParseUser.getCurrentUser().getUsername())){
            acceptRequest.setVisibility(View.GONE);
        }
        else
        {
            acceptRequest.setVisibility(View.VISIBLE);
        }


    }

    /**
     * Method to handle siwpes not to take it out of bounds
     * and load previous request
      * @return
     */
    public boolean onSwipeRight() {
        // decrement the position by one to load previous request
        if(currrentRequestPosition > 0){
            currrentRequestPosition = currrentRequestPosition -1;
            loadRequestDetails(currrentRequestPosition);

        }
        // iF the current position is first on the list just display the request
        if(currrentRequestPosition ==0)
            loadRequestDetails(currrentRequestPosition);
        return false;
    }
    /**
     * Method to handle siwpes not to take it out of bounds
     * and load next request
     * @return
     */

    public boolean onSwipeLeft() {
        // Increment the position by one to load next rewuest
        if( currrentRequestPosition < listEnd){
            currrentRequestPosition = currrentRequestPosition + 1;
            loadRequestDetails(currrentRequestPosition);

        }
        // iF the current position is last on the just display the request
        if(currrentRequestPosition == listEnd)
            loadRequestDetails(currrentRequestPosition);

        return false;
    }

    /**
     *  Method to Generate email and send to Acceptor
     * @param req
     * @throws MessagingException
     */
    public void generateAndSendEmailToAcceptor(Requests req) throws  MessagingException {

// Get mailserver properties
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
 // get session for email
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(ParseUser.getCurrentUser().getEmail()));
        generateMailMessage.setSubject("Details of Accepted Request");
 // HTML format of the email
        String emailBody = "Here are the Details of the request you have accepted <br>" +
                "Item: "+ req.getString("item")+"<br>"+
                "Requested By: "+req.getString("RequestedBy")+"<br>"+
        "Requested On: "+ req.getString("requestedOn")+"<br>"+
                "Accepted On: "+req.getString("acceptedOn")+"<br>"+
                "You can Contact the requester at "+ req.getString("requestorEmail")
                +". <br> Thankyou, <br> All Share team";
 // Define type
        generateMailMessage.setContent(emailBody, "text/html");
 //Start Transport
        Transport transport = getMailSession.getTransport("smtp");
        transport.connect("smtp.gmail.com", "dandegus16105@gmail.com", "wwecmallikharjun");
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
   //Close connection
        transport.close();
    }

    /**
     * Method to Generate email and send to requestor
     * @param req
     * @throws MessagingException
     */
    public void generateAndSendEmailToRequestor(Requests req) throws  MessagingException {
// Get mailserver properties
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        // get session for email
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(req.getString("requestorEmail")));
        generateMailMessage.setSubject("An user has accepted to fulfil your request");
        // HTML format of the email
        String emailBody = "You have requested for <br> " +
                "Item: "+ req.getString("item")+"<br>"+
                "Requested By: "+req.getString("RequestedBy")+"<br>"+
                "Requested On:"+ req.getString("requestedOn")+"<br>"+
                "Accepted On: "+req.getString("acceptedOn")+"<br>"+
                "User "+ParseUser.getCurrentUser().getUsername() + " Has accepted your request."+"<br>"+
                "You can contact "+ParseUser.getCurrentUser().getUsername()+" at "+ ParseUser.getCurrentUser().getEmail()
                +". <br> Thankyou, <br> All Share team";
        // Define type
        generateMailMessage.setContent(emailBody, "text/html");
        //Start Transport
        Transport transport = getMailSession.getTransport("smtp");
        transport.connect("smtp.gmail.com", "dandegus16105@gmail.com", "wwecmallikharjun");
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        //Close connection
        transport.close();
    }

}
