package com.peiwentang.movieAnalysis.movie;

import java.util.List;

/**
 * Representation of a movie including details
 *
 */
public class Movie {
    private int id;
    private String title;
    private List<Integer> genreIds;
    private boolean adult;
    private String language;
    private String backdropURL;
    private String overview;
    private int popularity;
    private String posterURL;
    private String releaseDate;

    public Movie(int id, String title, boolean adult, String language, String backdropURL, String overview, int popularity, String posterURL, String releaseDate, List<Integer> genreIds) {
        this.id = id;
        this.title = title;
        this.genreIds = genreIds;
        this.adult = adult;
        this.language = language;
        this.backdropURL = backdropURL;
        this.overview = overview;
        this.popularity = popularity;
        this.posterURL = posterURL;
        this.releaseDate = releaseDate;
    }
}
