package com.gitmad.peepshow.view;

/**
 * TODO: Enter class description.
 */
public class Peep {
    /*
    "votes": 4, "artist": "test", "title": "testtitle", "lon": 65.400000000000006, "lat": 80.099999999999994, "type": "audio"
     */

    private double lon, lat;
    private int votes;
    private String artist, title, type, url;

    public Peep() { }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
