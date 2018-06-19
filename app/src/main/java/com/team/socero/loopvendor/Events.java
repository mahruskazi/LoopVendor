package com.team.socero.loopvendor;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Events {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("access_level")
    @Expose
    private Integer accessLevel;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("geo_location")
    @Expose
    private String geoLocation;
    @SerializedName("category")
    @Expose
    private Integer category;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("country")
    @Expose
    private String country;


    public Events(Integer type, Integer accessLevel, String title, String geoLocation, Integer category,
                  String coverImage, String area, String city, String state, String country)
    {
        this.type = type;
        this.accessLevel = accessLevel;
        this.title = title;
        this.category = category;
        this.geoLocation = geoLocation;
        this.area = area;
        this.city = city;
        this.state = state;
        this.country = country;
        this.coverImage = coverImage;


    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
