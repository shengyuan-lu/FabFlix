package models;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;


/* In the table

id: varchar
title: varchat
year: int
director: varchar
price: float

*/


public class Movie {
    private String id; // id in table (can't be null)
    private String title; // title in table (can't be null)
    private int year; // year in table, int (can't be null)
    private String director; // director in table (can't be null)
    private float price; // price in table (can't be null), amount doesn't matter
    private Set<String> genres; // 1 movie can have multiple genres

    private Random rd = new Random();

    public Movie(String id, String title, int year, String director, Set<String> genres) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.price = (float) Math.round((rd.nextFloat() * 9 + 1) * 100) / 100;
    }

    public Movie() {
        genres = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void addGenre(String genres) {
        this.genres.add(genres);
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }


    public boolean validate() {
        if (this.checkStringNullOrEmpty(this.id) || this.checkStringNullOrEmpty(this.title) || this.checkStringNullOrEmpty(this.director) || this.price == null || this.year == null ) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkStringNullOrEmpty(String str) {
        if (str != null && !str.trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public String GetDetails() {
        StringBuilder sb = new StringBuilder();

        sb.append("Movie ID: " + this.id + "\n");
        sb.append("Movie Title: " + this.title + "\n");
        sb.append("Movie Year: " + this.year + "\n");
        sb.append("Movie Director: " + this.director + "\n");
        sb.append("Movie Price: " + this.price + "\n");
        sb.append("Movie Genres: " + this.genres.toString() + "\n");

        return sb.toString();
    }

}