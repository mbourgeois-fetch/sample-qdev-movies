package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                    new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String movieName, Long movieId, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (movieId != null && movieId > 0) {
                        matches = movie.getId() == movieId;
                    } else {
                        if (movieName != null && !movieName.trim().isEmpty()) {
                            matches = matches && movie.getMovieName().toLowerCase().contains(movieName.toLowerCase());
                        }
                        if (genre != null && !genre.trim().isEmpty()) {
                            matches = matches && movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                        }
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Drama", "Action", "Comedy");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    // Ahoy! New tests for our search treasure functionality
    
    @Test
    public void testSearchMoviesByName() {
        ResponseEntity<?> response = moviesController.searchMovies("Test", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(1, searchResponse.getCount());
        assertEquals("Test Movie", searchResponse.getMovies().get(0).getMovieName());
        assertTrue(searchResponse.getMessage().contains("Found 1 movie"));
    }
    
    @Test
    public void testSearchMoviesByGenre() {
        ResponseEntity<?> response = moviesController.searchMovies(null, null, "Action");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(1, searchResponse.getCount());
        assertEquals("Action Movie", searchResponse.getMovies().get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesById() {
        ResponseEntity<?> response = moviesController.searchMovies(null, 2L, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(1, searchResponse.getCount());
        assertEquals("Action Movie", searchResponse.getMovies().get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesNoResults() {
        ResponseEntity<?> response = moviesController.searchMovies("NonExistent", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(0, searchResponse.getCount());
        assertTrue(searchResponse.getMessage().contains("No movies found"));
    }
    
    @Test
    public void testSearchMoviesNoParameters() {
        ResponseEntity<?> response = moviesController.searchMovies(null, null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchErrorResponse);
        
        MoviesController.SearchErrorResponse errorResponse = (MoviesController.SearchErrorResponse) response.getBody();
        assertTrue(errorResponse.getError().contains("must provide at least one search parameter"));
    }
    
    @Test
    public void testSearchMoviesEmptyParameters() {
        ResponseEntity<?> response = moviesController.searchMovies("", null, "");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchErrorResponse);
        
        MoviesController.SearchErrorResponse errorResponse = (MoviesController.SearchErrorResponse) response.getBody();
        assertTrue(errorResponse.getError().contains("must provide at least one search parameter"));
    }
    
    @Test
    public void testSearchMoviesInvalidId() {
        ResponseEntity<?> response = moviesController.searchMovies(null, -1L, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchErrorResponse);
        
        MoviesController.SearchErrorResponse errorResponse = (MoviesController.SearchErrorResponse) response.getBody();
        assertTrue(errorResponse.getError().contains("must be a positive number"));
    }
    
    @Test
    public void testSearchMoviesCaseInsensitive() {
        ResponseEntity<?> response = moviesController.searchMovies("ACTION", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(1, searchResponse.getCount());
        assertEquals("Action Movie", searchResponse.getMovies().get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesPartialMatch() {
        ResponseEntity<?> response = moviesController.searchMovies("Com", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(1, searchResponse.getCount());
        assertEquals("Comedy Film", searchResponse.getMovies().get(0).getMovieName());
    }
    
    @Test
    public void testGetMoviesIncludesGenres() {
        String result = moviesController.getMovies(model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("genres");
        assertNotNull(genres);
        assertEquals(3, genres.size());
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action"));
        assertTrue(genres.contains("Comedy"));
    }
}
