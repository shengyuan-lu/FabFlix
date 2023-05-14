package models;

import java.util.ArrayList;

public class Movie {
    private String id; // id in table (can't be null)
    private String title; // title in table (can't be null)
    private int year; // year in table, int (can't be null)
    private String director; // director in table (can't be null)
    private double price = 7.99; // price in table (can't be null), amount doesn't matter so fixed for now
    private ArrayList<String> genres; // 1 movie can have multiple genres

    public Movie(String id, String title, int year, String director, ArrayList<String> genres) {
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

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public double getPrice() {
        return price;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

}