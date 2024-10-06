package com.peiwentang.movieAnalysis.movie;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieManager {
    private Map<Integer, Movie> movies = new HashMap<>();

    public MovieManager() {}

    public void parseJson(String json) {
        JsonObject jsonObject = (JsonObject)JsonParser.parseString(json);
        JsonArray results = jsonObject.getAsJsonArray("results");
        for(int i = 0; i < results.size(); i++){
            JsonObject movie = (JsonObject)results.get(i);
            int id = movie.get("id").getAsInt();
            String title = movie.get("title").getAsString();
            boolean adult = movie.get("adult").getAsBoolean();
            String language = movie.get("original_language").getAsString();
            String backdropURL = movie.get("backdrop_path").getAsString();
            String posterURL = movie.get("poster_path").getAsString();
            String releaseDate = movie.get("release_date").getAsString();
            String overview = movie.get("overview").getAsString();
            int popularity = movie.get("popularity").getAsInt();
            JsonArray genreArray = movie.get("genre_ids").getAsJsonArray();
            List<Integer> genreIds = new ArrayList<>();
            for(int j = 0; j < genreArray.size(); j++){
                genreIds.add(genreArray.get(j).getAsInt());
            }
            movies.put(id, new Movie(id, title, adult, language, backdropURL, overview, popularity, posterURL, releaseDate, genreIds));
        }
    }
}
