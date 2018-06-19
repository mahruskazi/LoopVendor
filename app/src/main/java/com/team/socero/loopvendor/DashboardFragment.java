package com.team.socero.loopvendor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahrus Kazi on 2018-06-18.
 */

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private RecyclerView rv;
    private FloatingActionButton createEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_dashboard, container, false);

        rv = (RecyclerView)v.findViewById(R.id.plans);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setTag("RecyclerView");

        FireBaseDataBase dataBase = new FireBaseDataBase();
        dataBase.eventsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                List<Event> events = new ArrayList<>();
                for(DataSnapshot child : children){
                    events.add(child.getValue(Event.class));
                    Log.d(TAG, "onDataChange: Here");
                }
                RVAdapter adapter = new RVAdapter(events);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        createEvent = v.findViewById(R.id.create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        return v;
    }

    public void createEvent() {
        Intent intent = new Intent(getActivity(), NewEvent.class);
        startActivityForResult(intent, 111);
    }
}
