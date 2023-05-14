package models;

import java.util.ArrayList;

public class Movie {
    private String id;
    private String title;
    private int year;
    private String director;
    private ArrayList<String> genres;

    public Movie(String id, String title, Integer year, String director, ArrayList<String> genres) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

}