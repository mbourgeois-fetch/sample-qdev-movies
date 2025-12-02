package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! This be the REST endpoint for searching through our movie treasure!
     * Accepts query parameters to filter movies like a true pirate captain.
     * 
     * @param name The movie name to search for (optional, case-insensitive partial match)
     * @param id The specific movie ID ye be seekin' (optional)
     * @param genre The genre to filter by (optional, case-insensitive partial match)
     * @return JSON response with matching movies or appropriate error message
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<?> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! Search request received - Name: {}, ID: {}, Genre: {}", name, id, genre);
        
        try {
            // Validate that at least one search parameter is provided
            if ((name == null || name.trim().isEmpty()) && 
                id == null && 
                (genre == null || genre.trim().isEmpty())) {
                
                logger.warn("Arrr! No search criteria provided, ye scurvy dog!");
                return ResponseEntity.badRequest()
                    .body(new SearchErrorResponse("Arrr! Ye must provide at least one search parameter, matey! " +
                                                "Use 'name', 'id', or 'genre' to find yer treasure."));
            }
            
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided: {}", id);
                return ResponseEntity.badRequest()
                    .body(new SearchErrorResponse("Shiver me timbers! Movie ID must be a positive number, ye landlubber!"));
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            if (searchResults.isEmpty()) {
                logger.info("No movies found matching the search criteria");
                return ResponseEntity.ok(new SearchResponse(searchResults, 
                    "No movies found matching yer search criteria, matey! Try different search terms or check yer spelling."));
            }
            
            logger.info("Found {} movies matching search criteria", searchResults.size());
            return ResponseEntity.ok(new SearchResponse(searchResults, 
                String.format("Ahoy! Found %d movie%s matching yer search, ye savvy pirate!", 
                             searchResults.size(), searchResults.size() == 1 ? "" : "s")));
                             
        } catch (Exception e) {
            logger.error("Arrr! Error occurred during movie search: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new SearchErrorResponse("Batten down the hatches! Something went wrong during the search. " +
                                            "Our crew is working to fix this scurvy bug!"));
        }
    }

    /**
     * Response wrapper for successful search results with pirate flair
     */
    public static class SearchResponse {
        private final List<Movie> movies;
        private final String message;
        private final int count;

        public SearchResponse(List<Movie> movies, String message) {
            this.movies = movies;
            this.message = message;
            this.count = movies.size();
        }

        public List<Movie> getMovies() { return movies; }
        public String getMessage() { return message; }
        public int getCount() { return count; }
    }

    /**
     * Response wrapper for search errors with pirate language
     */
    public static class SearchErrorResponse {
        private final String error;
        private final String timestamp;

        public SearchErrorResponse(String error) {
            this.error = error;
            this.timestamp = java.time.Instant.now().toString();
        }

        public String getError() { return error; }
        public String getTimestamp() { return timestamp; }
    }
}