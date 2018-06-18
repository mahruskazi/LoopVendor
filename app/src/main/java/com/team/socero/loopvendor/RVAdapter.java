package com.team.socero.loopvendor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mahrus Kazi on 2018-06-17.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder>{

    List<Event> events;

    RVAdapter(List<Event> events){
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Log.d("RVAdapter", "onBindViewHolder: " + events.get(position).getEventName());
        holder.eventTitle.setText(events.get(position).getEventName());
        holder.eventDate.setText(events.get(position).getStartDate());
        holder.eventAddress.setText(events.get(position).getLocation().address);
        byte[] imageBytes = Base64.decode(events.get(position).getCoverImage(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.eventPhoto.setImageBitmap(decodedImage);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView eventTitle;
        TextView eventDate;
        TextView eventAddress;
        ImageView eventPhoto;

        EventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.event_card);
            eventTitle = itemView.findViewById(R.id.event_card_title);
            eventDate = itemView.findViewById(R.id.event_card_date);
            eventAddress = itemView.findViewById(R.id.event_card_address);
            eventPhoto = itemView.findViewById(R.id.event_card_photo);
        }
    }

}