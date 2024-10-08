package com.peiwentang.movieAnalysis.movie;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class MovieController {
    // API_TOKEN for TMDb
    private final String token = System.getenv("TMDB_API_TOKEN");
    private final String omdbApiKey = System.getenv("OMDB_API_KEY");

    @RequestMapping(value = "/movies", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMovieDetailsByMovieID(@RequestParam Map<String, String> queryParameters){
        WebClient client = WebClient.create();
        String response = null;
        String uri = "https://api.themoviedb.org/3/movie/" + queryParameters.get("movieid") + "?language=en-US";

        try {
            response = client.get()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
            System.out.println(obj.get("genres").getAsJsonArray().toString());
        } catch (URISyntaxException e) {
            System.out.println("error");
        }
        return response;
    }

    @RequestMapping(value = "/getIMDbId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getIMDbId(@RequestParam Map<String, String> queryParameters) throws UnsupportedEncodingException {
        WebClient client = WebClient.create();
        String response = null;
        String movieId = queryParameters.get("movieid");
        String uri = "https://api.themoviedb.org/3/movie/" + movieId + "/external_ids";
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



    @RequestMapping(value = "/movies/searchMovie", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchMoviesByTitle(@RequestParam Map<String, String> queryParameters) throws UnsupportedEncodingException {
        WebClient client = WebClient.create();
        String response = null;
        String decodedQuery = URLDecoder.decode(queryParameters.get("title"), "UTF-8");
        String uri = "https://api.themoviedb.org/3/search/movie?query="  + decodedQuery + "&include_adult=false&language=en-US&page=1";
        System.out.println(uri);
        MovieManager movieManager = new MovieManager();
        try {
            response = client.get()
                    .uri(new URI(uri.replace(" ", "%20")))
                    .header("Authorization", "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            movieManager.parseJson(response);
        } catch (URISyntaxException e) {
            System.out.println("error");
        }

//        return movieManager.serialize();
        System.out.println(movieManager.serialize());
        System.out.println(response);
        return movieManager.serialize();
    }

    @RequestMapping(value = "/movies/searchCredits", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchCreditsByMovieID(@RequestParam Map<String, String> queryParameters) throws UnsupportedEncodingException {
        WebClient client = WebClient.create();
        String response = null;
        String decodedQuery = URLDecoder.decode(queryParameters.get("movieid"), "UTF-8");
        String uri = "https://api.themoviedb.org/3/movie/" + decodedQuery + "/credits?language=en-US";
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

    @RequestMapping(value = "/getPlot", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPlot(@RequestParam Map<String, String> queryParameters) {
        WebClient client = WebClient.create();
        String response = null;
        String imdbId = queryParameters.get("imdbid");
        String uri = "https://www.omdbapi.com/?i=" + imdbId + "&apikey=" + omdbApiKey + "&plot=full";
        try {
            response = client.get()
                    .uri(new URI(uri))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (URISyntaxException e) {
            System.out.println("error");
        }
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

        return jsonObject.get("Plot").getAsString();
    }

}
