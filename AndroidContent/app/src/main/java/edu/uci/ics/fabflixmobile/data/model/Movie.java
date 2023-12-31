package edu.uci.ics.fabflixmobile.data.model;

import org.json.JSONArray;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String title;
    private final String id;
    private final int year;
    private final String director;
    private final JSONArray genres;
    private final JSONArray stars;


    public Movie(String title, String id, int year, String director, JSONArray genres, JSONArray stars) {
        this.title = title;
        this.id = id;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getDirector() {
        return director;
    }

    public JSONArray getGenres() {
        return genres;
    }

    public JSONArray getStars() {
        return stars;
    }

    public int getYear() {
        return year;
    }

}