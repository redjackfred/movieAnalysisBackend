package com.peiwentang.movieAnalysis.movie;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieManager {
    private Map<Integer, Movie> movies = new HashMap<>();
    private final String token = System.getenv("TMDB_API_TOKEN");

    public MovieManager() {}

    public String getGenreNames(){
        WebClient client = WebClient.create();
        String response = null;
        String uri = "https://api.themoviedb.org/3/genre/movie/list?language=en";
        try {
            response = client.get()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (URISyntaxException e) {
            System.out.println("error");
        }

        return response;
    }

    public void parseJson(String json) {
        // Prepare genre names hashmap from TMDb
        String genresJsonString = getGenreNames();
        JsonObject genreNamesObject = (JsonObject)JsonParser.parseString(genresJsonString);
        JsonArray genreNamesArray = genreNamesObject.getAsJsonArray("genres");
        Map<Integer, String> genreNames = new HashMap<>();
        for(int j = 0; j < genreNamesArray.size(); j++){
            genreNames.put(genreNamesArray.get(j).getAsJsonObject().get("id").getAsInt(),
                    genreNamesArray.get(j).getAsJsonObject().get("name").getAsString());
        }

        JsonObject jsonObject = (JsonObject)JsonParser.parseString(json);
        JsonArray results = jsonObject.getAsJsonArray("results");
        for(int i = 0; i < results.size(); i++){
            JsonObject movie = (JsonObject)results.get(i);
            int id = movie.get("id").getAsInt();
            String title = movie.get("title").getAsString();
            boolean adult = movie.get("adult").getAsBoolean();
            String language = movie.get("original_language").getAsString();
            String backdropURL = null;
            if(!movie.get("backdrop_path").isJsonNull()) {
                backdropURL = movie.get("backdrop_path").getAsString();
            }
            String posterURL = null;
            if(!movie.get("poster_path").isJsonNull()) {
                posterURL = movie.get("poster_path").getAsString();
            }
            String releaseDate = movie.get("release_date").getAsString();
            String overview = movie.get("overview").getAsString();
            int popularity = movie.get("popularity").getAsInt();
            JsonArray genreArray = movie.get("genre_ids").getAsJsonArray();
            List<String> genres = new ArrayList<>();

            for(int j = 0; j < genreArray.size(); j++){
                genres.add(genreNames.get(genreArray.get(j).getAsInt()));
            }
            movies.put(id, new Movie(id, title, adult, language, backdropURL, overview, popularity, posterURL, releaseDate, genres));
        }

        System.out.println(movies);
    }

    public String serialize() {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"results\":[");
        for(Movie movie : movies.values()){
            sb.append(gson.toJson(movie, Movie.class));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]}");

        return sb.toString();
    }

    @Override
    public String toString() {
        return "MovieManager{" +
                "movies=" + movies +
                '}';
    }
}
