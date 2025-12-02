package services

import (
	"testing"

	"github.com/mbourgeois-fetch/sample-qdev-movies/internal/models"
	"github.com/stretchr/testify/assert"
)

// Ahoy! Test class for our MovieService treasure chest functionality.
// These tests ensure our search capabilities work like a well-oiled pirate ship!

func createTestMovieService() *MovieService {
	service := &MovieService{
		movies: []models.Movie{
			{ID: 1, MovieName: "The Prison Escape", Director: "John Director", Year: 1994, Genre: "Drama", Description: "Test description", Duration: 142, IMDBRating: 5.0},
			{ID: 2, MovieName: "The Family Boss", Director: "Michael Filmmaker", Year: 1972, Genre: "Crime/Drama", Description: "Test description", Duration: 175, IMDBRating: 5.0},
			{ID: 3, MovieName: "Action Movie", Director: "Action Director", Year: 2022, Genre: "Action", Description: "Action description", Duration: 110, IMDBRating: 4.0},
			{ID: 4, MovieName: "Comedy Film", Director: "Comedy Director", Year: 2021, Genre: "Comedy", Description: "Comedy description", Duration: 95, IMDBRating: 3.5},
		},
	}
	return service
}

// Arrr! Basic functionality tests

func TestGetAllMovies(t *testing.T) {
	service := createTestMovieService()
	movies := service.GetAllMovies()
	
	assert.NotNil(t, movies)
	assert.False(t, len(movies) == 0)
	assert.True(t, len(movies) > 0)
}

func TestGetMovieByID(t *testing.T) {
	service := createTestMovieService()
	movie, err := service.GetMovieByID(1)
	
	assert.NoError(t, err)
	assert.NotNil(t, movie)
	assert.Equal(t, int64(1), movie.ID)
}

func TestGetMovieByIDNotFound(t *testing.T) {
	service := createTestMovieService()
	movie, err := service.GetMovieByID(999)
	
	assert.Error(t, err)
	assert.Nil(t, movie)
}

func TestGetMovieByIDInvalid(t *testing.T) {
	service := createTestMovieService()
	movie, err := service.GetMovieByID(0)
	
	assert.Error(t, err)
	assert.Nil(t, movie)

	movieNegative, errNegative := service.GetMovieByID(-1)
	assert.Error(t, errNegative)
	assert.Nil(t, movieNegative)
}

func TestGetAllGenres(t *testing.T) {
	service := createTestMovieService()
	genres := service.GetAllGenres()
	
	assert.NotNil(t, genres)
	assert.False(t, len(genres) == 0)
	
	// Check that genres are sorted
	for i := 1; i < len(genres); i++ {
		assert.True(t, genres[i] >= genres[i-1])
	}
}

// Shiver me timbers! Search functionality tests

func TestSearchMoviesByName(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("Prison", nil, "")
	
	assert.NotNil(t, results)
	assert.Equal(t, 1, len(results))
	assert.Contains(t, results[0].MovieName, "Prison")
}

func TestSearchMoviesByNameCaseInsensitive(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("PRISON", nil, "")
	
	assert.NotNil(t, results)
	assert.Equal(t, 1, len(results))
	assert.Contains(t, results[0].MovieName, "Prison")
}

func TestSearchMoviesByNamePartialMatch(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("The", nil, "")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 1) // Should find multiple movies with "The" in the name
	
	for _, movie := range results {
		assert.Contains(t, movie.MovieName, "The")
	}
}

func TestSearchMoviesByGenre(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("", nil, "Drama")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 0)
	
	for _, movie := range results {
		assert.Contains(t, movie.Genre, "Drama")
	}
}

func TestSearchMoviesByGenreCaseInsensitive(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("", nil, "DRAMA")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 0)
	
	for _, movie := range results {
		assert.Contains(t, movie.Genre, "Drama")
	}
}

func TestSearchMoviesByGenrePartialMatch(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("", nil, "Act")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 0)
	
	for _, movie := range results {
		assert.Contains(t, movie.Genre, "Action")
	}
}

func TestSearchMoviesByID(t *testing.T) {
	service := createTestMovieService()
	id := int64(1)
	results := service.SearchMovies("", &id, "")
	
	assert.NotNil(t, results)
	assert.Equal(t, 1, len(results))
	assert.Equal(t, int64(1), results[0].ID)
}

func TestSearchMoviesByIDTakesPrecedence(t *testing.T) {
	service := createTestMovieService()
	// When ID is provided, it should take precedence over other criteria
	id := int64(1)
	results := service.SearchMovies("SomeOtherName", &id, "SomeOtherGenre")
	
	assert.NotNil(t, results)
	assert.Equal(t, 1, len(results))
	assert.Equal(t, int64(1), results[0].ID)
}

func TestSearchMoviesByIDNotFound(t *testing.T) {
	service := createTestMovieService()
	id := int64(999)
	results := service.SearchMovies("", &id, "")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) == 0)
}

func TestSearchMoviesByNameAndGenre(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("The", nil, "Drama")
	
	assert.NotNil(t, results)
	
	for _, movie := range results {
		assert.Contains(t, movie.MovieName, "The")
		assert.Contains(t, movie.Genre, "Drama")
	}
}

func TestSearchMoviesNoResults(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("NonExistentMovie", nil, "")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) == 0)
}

func TestSearchMoviesEmptyName(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("", nil, "Drama")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 0)
	
	// Should ignore empty name and search by genre only
	for _, movie := range results {
		assert.Contains(t, movie.Genre, "Drama")
	}
}

func TestSearchMoviesWhitespaceName(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("   ", nil, "Drama")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 0)
	
	// Should ignore whitespace-only name and search by genre only
	for _, movie := range results {
		assert.Contains(t, movie.Genre, "Drama")
	}
}

func TestSearchMoviesEmptyGenre(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("The", nil, "")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 0)
	
	// Should ignore empty genre and search by name only
	for _, movie := range results {
		assert.Contains(t, movie.MovieName, "The")
	}
}

func TestSearchMoviesWhitespaceGenre(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("The", nil, "   ")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) > 0)
	
	// Should ignore whitespace-only genre and search by name only
	for _, movie := range results {
		assert.Contains(t, movie.MovieName, "The")
	}
}

func TestSearchMoviesAllParametersEmpty(t *testing.T) {
	service := createTestMovieService()
	results := service.SearchMovies("", nil, "")
	
	assert.NotNil(t, results)
	
	// Should return all movies when no criteria provided
	allMovies := service.GetAllMovies()
	assert.Equal(t, len(allMovies), len(results))
}

// Batten down the hatches! Edge case tests

func TestSearchMoviesSpecialCharacters(t *testing.T) {
	service := createTestMovieService()
	// Test with special characters that might be in movie names
	results := service.SearchMovies(":", nil, "")
	
	assert.NotNil(t, results)
	// Should handle special characters gracefully
}

func TestSearchMoviesNumericGenre(t *testing.T) {
	service := createTestMovieService()
	// Test searching for genres that might contain numbers
	results := service.SearchMovies("", nil, "2")
	
	assert.NotNil(t, results)
	// Should handle numeric searches gracefully
}

func TestSearchMoviesZeroID(t *testing.T) {
	service := createTestMovieService()
	id := int64(0)
	results := service.SearchMovies("", &id, "")
	
	assert.NotNil(t, results)
	assert.True(t, len(results) == 0) // ID 0 should not match anything
}
