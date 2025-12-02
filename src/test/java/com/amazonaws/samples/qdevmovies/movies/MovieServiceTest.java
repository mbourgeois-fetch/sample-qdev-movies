package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Test class for our MovieService treasure chest functionality.
 * These tests ensure our search capabilities work like a well-oiled pirate ship!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    // Arrr! Basic functionality tests

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.size() > 0);
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());

        Optional<Movie> movieNegative = movieService.getMovieById(-1L);
        assertFalse(movieNegative.isPresent());
    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Check that genres are sorted and unique
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i).compareTo(genres.get(i-1)) >= 0);
        }
    }

    // Shiver me timbers! Search functionality tests

    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getMovieName().contains("Prison"));
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getMovieName().toLowerCase().contains("prison"));
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertNotNull(results);
        assertTrue(results.size() > 1); // Should find multiple movies with "The" in the name
        
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "DRAMA");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Sci");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("sci"));
        }
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesByIdTakesPrecedence() {
        // When ID is provided, it should take precedence over other criteria
        List<Movie> results = movieService.searchMovies("SomeOtherName", 1L, "SomeOtherGenre");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByNameAndGenre() {
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        assertNotNull(results);
        
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesEmptyName() {
        List<Movie> results = movieService.searchMovies("", null, "Drama");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Should ignore empty name and search by genre only
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesWhitespaceName() {
        List<Movie> results = movieService.searchMovies("   ", null, "Drama");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Should ignore whitespace-only name and search by genre only
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesEmptyGenre() {
        List<Movie> results = movieService.searchMovies("The", null, "");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Should ignore empty genre and search by name only
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesWhitespaceGenre() {
        List<Movie> results = movieService.searchMovies("The", null, "   ");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Should ignore whitespace-only genre and search by name only
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesAllParametersNull() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertNotNull(results);
        
        // Should return all movies when no criteria provided
        List<Movie> allMovies = movieService.getAllMovies();
        assertEquals(allMovies.size(), results.size());
    }

    @Test
    public void testSearchMoviesAllParametersEmpty() {
        List<Movie> results = movieService.searchMovies("", null, "");
        assertNotNull(results);
        
        // Should return all movies when only empty criteria provided
        List<Movie> allMovies = movieService.getAllMovies();
        assertEquals(allMovies.size(), results.size());
    }

    // Batten down the hatches! Edge case tests

    @Test
    public void testSearchMoviesSpecialCharacters() {
        // Test with special characters that might be in movie names
        List<Movie> results = movieService.searchMovies(":", null, null);
        assertNotNull(results);
        // Should handle special characters gracefully
    }

    @Test
    public void testSearchMoviesNumericGenre() {
        // Test searching for genres that might contain numbers
        List<Movie> results = movieService.searchMovies(null, null, "2");
        assertNotNull(results);
        // Should handle numeric searches gracefully
    }

    @Test
    public void testSearchMoviesVeryLongString() {
        String longString = "a".repeat(1000);
        List<Movie> results = movieService.searchMovies(longString, null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Should not crash and return empty results
    }

    @Test
    public void testSearchMoviesZeroId() {
        List<Movie> results = movieService.searchMovies(null, 0L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty()); // ID 0 should not match anything
    }

    @Test
    public void testSearchMoviesNegativeId() {
        List<Movie> results = movieService.searchMovies(null, -5L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Negative ID should not match anything
    }
}