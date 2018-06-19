package com.team.socero.loopvendor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PostEvents {

    @SerializedName("events")
    @Expose
    private Events events;

    public PostEvents(Events postEvent)
    {
        this.events = postEvent;
    }

    public Events  getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }
}
