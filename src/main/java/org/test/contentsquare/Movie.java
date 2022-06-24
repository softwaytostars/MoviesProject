package org.test.contentsquare;

import java.util.HashSet;
import java.util.Set;

public class Movie {
    private String id;
    private int year=0;
    private int length=0;
    private final Set<String> genre = new HashSet<>();
    private final Set<String> directors = new HashSet<>();
    private final Set<String> actors = new HashSet<>();

    public Movie(String id, int year, int length) {
        this.id = id;
        this.year = year;
        this.length = length;
    }

    public Movie() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Set<String> getGenre() {
        return genre;
    }

    public Set<String> getDirectors() {
        return directors;
    }

    public Set<String> getActors() {
        return actors;
    }

    public void validate() throws Exception {
        if (id == null) throw new Exception("id is null");
        if (year == 0) throw new Exception("year is not filled");
        if (length == 0) throw new Exception("length is not filled");
    }

}
