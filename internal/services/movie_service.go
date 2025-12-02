package services

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"sort"
	"strings"

	"github.com/mbourgeois-fetch/sample-qdev-movies/internal/models"
)

// MovieService handles all movie-related business logic
type MovieService struct {
	movies []models.Movie
}

// NewMovieService creates a new instance of MovieService
func NewMovieService() *MovieService {
	service := &MovieService{}
	service.loadMovies()
	return service
}

// loadMovies loads movie data from JSON file
func (s *MovieService) loadMovies() {
	log.Println("🏴‍☠️ Loading movie treasure from movies.json...")

	data, err := ioutil.ReadFile("data/movies.json")
	if err != nil {
		log.Printf("Arrr! Error reading movies.json: %v", err)
		return
	}

	err = json.Unmarshal(data, &s.movies)
	if err != nil {
		log.Printf("Shiver me timbers! Error parsing movies.json: %v", err)
		return
	}

	log.Printf("⚓ Successfully loaded %d movies into our treasure chest!", len(s.movies))
}

// GetAllMovies returns all movies in our treasure chest
func (s *MovieService) GetAllMovies() []models.Movie {
	return s.movies
}

// GetMovieByID finds a specific movie by its ID
func (s *MovieService) GetMovieByID(id int64) (*models.Movie, error) {
	if id <= 0 {
		return nil, fmt.Errorf("invalid movie ID: %d", id)
	}

	for _, movie := range s.movies {
		if movie.ID == id {
			return &movie, nil
		}
	}

	return nil, fmt.Errorf("movie with ID %d not found", id)
}

// SearchMovies searches through our treasure chest of movies like a true pirate!
// This method filters movies based on the search criteria ye provide, matey.
func (s *MovieService) SearchMovies(movieName string, movieID *int64, genre string) []models.Movie {
	log.Printf("🔍 Ahoy! Searching for movies with criteria - Name: %s, ID: %v, Genre: %s", 
		movieName, movieID, genre)

	var results []models.Movie

	for _, movie := range s.movies {
		if s.matchesSearchCriteria(movie, movieName, movieID, genre) {
			results = append(results, movie)
		}
	}

	log.Printf("⚓ Found %d movies matching the search criteria, ye savvy pirate!", len(results))
	return results
}

// matchesSearchCriteria checks if a movie matches our search criteria
func (s *MovieService) matchesSearchCriteria(movie models.Movie, movieName string, movieID *int64, genre string) bool {
	// If searching by ID, that takes precedence over other criteria
	if movieID != nil && *movieID > 0 {
		return movie.ID == *movieID
	}

	nameMatches := true
	genreMatches := true

	// Check movie name (case-insensitive partial match)
	if movieName != "" && strings.TrimSpace(movieName) != "" {
		nameMatches = strings.Contains(
			strings.ToLower(movie.MovieName),
			strings.ToLower(strings.TrimSpace(movieName)),
		)
	}

	// Check genre (case-insensitive partial match)
	if genre != "" && strings.TrimSpace(genre) != "" {
		genreMatches = strings.Contains(
			strings.ToLower(movie.Genre),
			strings.ToLower(strings.TrimSpace(genre)),
		)
	}

	return nameMatches && genreMatches
}

// GetAllGenres returns all unique genres from our movie treasure chest
// This be useful for showing available genres to search through
func (s *MovieService) GetAllGenres() []string {
	genreMap := make(map[string]bool)
	
	// Collect unique genres
	for _, movie := range s.movies {
		// Handle genres that might have multiple values separated by "/"
		genres := strings.Split(movie.Genre, "/")
		for _, g := range genres {
			trimmed := strings.TrimSpace(g)
			if trimmed != "" {
				genreMap[trimmed] = true
			}
		}
	}

	// Convert to sorted slice
	var genres []string
	for genre := range genreMap {
		genres = append(genres, genre)
	}
	sort.Strings(genres)
	return genres
}
