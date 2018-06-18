package com.team.socero.loopvendor;

import java.util.List;

/**
 * Created by Mahrus Kazi on 2018-06-14.
 */

public class Event {
    private String eventName;
    private String description;
    private String startTime;
    private String startDate;
    private String endTime;
    private String endDate;
    private List<Ticket> tickets;
    private Location location;
    private String coverImage;
    private int category = 17; //TODO Add a category chooser to NewEvent

    Event(){
    }

    public String getEventName() {
        return eventName;
    }

    void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndTime() {
        return endTime;
    }

    void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndDate() {
        return endDate;
    }

    void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public String getCoverImage() {
        return coverImage;
    }

    void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getType() {
        return 1;
    }

    public int getAccessLevel() {
        return 1;
    }

    public Location getLocation() {
        return location;
    }

    void setLocation(Location location) {
        this.location = location;
    }


    static class Ticket{
        String ticketName;
        String ticketType;
        double ticketPrice;
        int ticketQuantity;
        int minTicketOrder;

        public Ticket(){
        }
    }

    static class Location{
        String area;
        String address;
        String geoLocation;

        public Location(){
        }
    }
}
