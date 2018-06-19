package com.team.socero.loopvendor;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Mahrus Kazi on 2018-06-14.
 */

public class FireBaseDataBase {

    private static final String TAG = "FireBaseDataBase";
    private static String user;
    private static FireBaseDataBase mInstance;
    private DatabaseReference mDatabase;
    private static Profile profile;
    private boolean stripeConnected = false;
    private static List<Event> events;

    @Inject
    public BaroServiceProvider baroServiceProvider;

    public static FireBaseDataBase getInstance(){
        if(mInstance == null){
            mInstance = new FireBaseDataBase();
            return mInstance;
        }else{
            return mInstance;
        }
    }

    //private FireBaseDataBase(){
        //Application.inject(this);
        //Log.d("new event id token", baroServiceProvider.getAuthToken().getToken());
    //}

    //Must call at the beginning of the program
    public static void initialize(String referenceUser) {
        user = referenceUser;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user).child("profile");

        //while(profile == null) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profile = dataSnapshot.getValue(Profile.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref = FirebaseDatabase.getInstance().getReference("users").child(user).child("events");

        if (events == null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    events = new ArrayList<>();
                    for (DataSnapshot child : children) {
                        events.add(child.getValue(Event.class));
                        Log.d(TAG, "onDataChange: Here");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //getInstance();
    }

    public void addEvent(Event event) {

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user).child("events");

        if (events == null) {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    events = new ArrayList<>();
                    for (DataSnapshot child : children) {
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
        } else {
            events.add(event);
            mDatabase.setValue(events);
        }

    }

    public void addProfile(Profile profile) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user).child("profile");
        ref.setValue(profile);
    }

    public Profile addStripeID(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user).child("profile");

        //while(profile == null) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profile = dataSnapshot.getValue(Profile.class);
                if (profile != null) {
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

    public Profile getProfile(){
        return profile;
    }

    public DatabaseReference profileReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(user).child("profile");
    }

    public DatabaseReference eventsReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(user).child("events");
    }

    public void addEventAWS(Event e) {
        JSONObject eventholder = new JSONObject();
        JSONObject event = new JSONObject();
        try {
            event.put("type", 1);
            event.put("access_level", 1);
            event.put("title", e.getEventName());
            event.put("event_desc", e.getDescription());

            event.put("geo_location", e.getLocation().geoLocation);
            event.put("category", e.getCategory());
            event.put("cover_image", e.getCoverImage());
            event.put("area", e.getLocation().area);
            event.put("city", e.getLocation().address);
            event.put("state", e.getLocation().address);
            event.put("country", e.getLocation().address);
            event.put("event_url", "Email us");
            event.put("start_time", e.getStartDate());
            event.put("end_time", e.getEndDate());
            event.put("price", e.getTickets().get(0).ticketPrice);
            event.put("creator_account_id", profile.getStripeID());

            eventholder.put("events", event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://g2lirn54ch.execute-api.us-east-1.amazonaws.com/staging/")
//                            .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
//                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        SoceroAPI api = retrofit.create(SoceroAPI.class);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), eventholder.toString());

        String finalDate = e.getStartDate();
        api.setPost(baroServiceProvider.getAuthToken().getToken(), body)
                .enqueue(new Callback<PostEvents>() {
                    @Override
                    public void onResponse(Call<PostEvents> call, Response<PostEvents> response) {
                        //prefs.edit().putInt("postedNewEvent", 1).apply();

                        Log.d("jsonpostevent", new GsonBuilder().setPrettyPrinting().create().toJson(response));

                        long threeHoursinMil = 10800000;
                        String timeToConvert = finalDate;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = null;
                        try {
                            date = dateFormat.parse(timeToConvert);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long unixTime1 = date.getTime();
                        long delay = (unixTime1 - threeHoursinMil) - System.currentTimeMillis();
                        //scheduleNotification(getNotification("'" + ideaHeader + "' begins soon."), delay);
                    }

                    @Override
                    public void onFailure(Call<PostEvents> call, Throwable throwable) {
                        Log.d(TAG, "onFailure: SetPost");
                        throwable.printStackTrace();
                    }
                });
    }
}
