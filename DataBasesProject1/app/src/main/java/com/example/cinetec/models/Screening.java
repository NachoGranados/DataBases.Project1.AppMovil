package com.example.cinetec.models;

public class Screening {

    private int id;
    private int cinemaNumber;
    private String movieOriginalName;
    private int hour;
    private int capacity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCinemaNumber() {
        return cinemaNumber;
    }

    public void setCinemaNumber(int cinemaNumber) {
        this.cinemaNumber = cinemaNumber;
    }

    public String getMovieOriginalName() {
        return movieOriginalName;
    }

    public void setMovieOriginalName(String movieOriginalName) {
        this.movieOriginalName = movieOriginalName;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
