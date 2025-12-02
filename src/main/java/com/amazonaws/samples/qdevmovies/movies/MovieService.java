package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy! Search through our treasure chest of movies like a true pirate!
     * This method filters movies based on the search criteria ye provide, matey.
     * 
     * @param movieName The name of the movie to search for (case-insensitive, partial matches allowed)
     * @param movieId The specific ID of a movie ye be seekin'
     * @param genre The genre to filter by (case-insensitive, partial matches allowed)
     * @return A list of movies that match yer search criteria, or an empty list if no treasure be found
     */
    public List<Movie> searchMovies(String movieName, Long movieId, String genre) {
        logger.info("Ahoy! Searching for movies with criteria - Name: {}, ID: {}, Genre: {}", 
                   movieName, movieId, genre);
        
        List<Movie> searchResults = movies.stream()
            .filter(movie -> matchesSearchCriteria(movie, movieName, movieId, genre))
            .collect(Collectors.toList());
            
        logger.info("Found {} movies matching the search criteria, ye savvy pirate!", searchResults.size());
        return searchResults;
    }

    /**
     * Arrr! This be the crew member that checks if a movie matches our search criteria.
     * 
     * @param movie The movie to examine
     * @param movieName Name to search for (can be null or empty)
     * @param movieId ID to search for (can be null)
     * @param genre Genre to search for (can be null or empty)
     * @return true if the movie matches all provided criteria, false otherwise
     */
    private boolean matchesSearchCriteria(Movie movie, String movieName, Long movieId, String genre) {
        // If searching by ID, that takes precedence over other criteria
        if (movieId != null && movieId > 0) {
            return movie.getId() == movieId;
        }
        
        boolean nameMatches = true;
        boolean genreMatches = true;
        
        // Check movie name (case-insensitive partial match)
        if (movieName != null && !movieName.trim().isEmpty()) {
            nameMatches = movie.getMovieName().toLowerCase()
                              .contains(movieName.trim().toLowerCase());
        }
        
        // Check genre (case-insensitive partial match)
        if (genre != null && !genre.trim().isEmpty()) {
            genreMatches = movie.getGenre().toLowerCase()
                               .contains(genre.trim().toLowerCase());
        }
        
        return nameMatches && genreMatches;
    }

    /**
     * Batten down the hatches! Get all unique genres from our movie treasure chest.
     * This be useful for showing available genres to search through.
     * 
     * @return A list of all unique genres in our movie collection
     */
    public List<String> getAllGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
