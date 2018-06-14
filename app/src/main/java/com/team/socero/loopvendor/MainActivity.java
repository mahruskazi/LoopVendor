package com.team.socero.loopvendor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(getSharedPreferences("LoopVendor", Context.MODE_PRIVATE).getString("stripe_account", "Nothing")).setValue("new user");
    }

    public void createEvent(View view) {
        Intent intent = new Intent(this, NewEvent.class);
        startActivity(intent);
    }
}
