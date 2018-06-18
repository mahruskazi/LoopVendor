package com.team.socero.loopvendor;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahrus Kazi on 2018-06-14.
 */

public class FireBaseDataBase {

    private static final String TAG = "FireBaseDataBase";
    private DatabaseReference mDatabase;
    private static String user;
    private Profile profile;
    private boolean stripeConnected = false;
    private List<Event> events;

    private static FireBaseDataBase mInstance;

    /*public static FireBaseDataBase getInstance(String reference){
        if(mInstance == null || !user.contentEquals(reference)){
            user = reference;
            return new FireBaseDataBase(reference);
        }else{
            return mInstance;
        }
    }

    private FireBaseDataBase(String reference){
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(reference);
    }*/

    //Must call at the beginning of the program
    public static void initialize(String referenceUser){
        user = referenceUser;
    }

    public void addEvent(Event event){

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user).child("events");

        if(events == null){
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    events = new ArrayList<>();
                    for(DataSnapshot child : children){
                        events.add(child.getValue(Event.class));
                        Log.d(TAG, "onDataChange: Here");
                    }
                    events.add(event);
                    mDatabase.setValue(events);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            events.add(event);
            mDatabase.setValue(events);
        }

    }

    public void addProfile(Profile profile){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user).child("profile");
        ref.setValue(profile);
    }

    public Profile addStripeID(String id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user).child("profile");

        //while(profile == null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    profile = dataSnapshot.getValue(Profile.class);
                    if(profile != null) {
                        profile.setStripeID(id);
                        ref.setValue(profile);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        //}

        return profile;
    }

    public DatabaseReference profileReference(){
        return FirebaseDatabase.getInstance().getReference("users").child(user).child("profile");
    }

    public DatabaseReference eventsReference(){
        return FirebaseDatabase.getInstance().getReference("users").child(user).child("events");
    }

    public void addEventAWS(Event e, String user){
        /*JSONObject eventholder = new JSONObject();
        JSONObject event = new JSONObject();
        try {
            event.put("type", 1);
            event.put("access_level", 1);
            event.put("title", ideaHeader);
            event.put("event_desc", ideaDesc);
            if (latitude == 1 && longitude == 1) {
                event.put("geo_location", null);
            } else {
                event.put("geo_location", latitude + "," + longitude);
            }
            event.put("category", selection);
            if (image64 == null || image64.equals("")) {
                event.put("cover_image", p.getUserImage());
            } else {
                event.put("cover_image", imageString);
            }
            event.put("area", placeName);
            event.put("city", address);
            event.put("state", address);
            event.put("country", address);
            event.put("event_url", contact_information);
            event.put("start_time", date2);
            event.put("end_time", date3);
            event.put("price", price);
            event.put("creator_account_id", user);

            eventholder.put("events", event);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
