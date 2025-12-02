package models

import "github.com/mbourgeois-fetch/sample-qdev-movies/internal/utils"

// Movie represents a movie in our treasure chest
type Movie struct {
	ID          int64   `json:"id"`
	MovieName   string  `json:"movieName"`
	Director    string  `json:"director"`
	Year        int     `json:"year"`
	Genre       string  `json:"genre"`
	Description string  `json:"description"`
	Duration    int     `json:"duration"`
	IMDBRating  float64 `json:"imdbRating"`
}

// GetIcon returns the pirate-themed icon for this movie
func (m *Movie) GetIcon() string {
	return utils.GetMovieIcon(m.MovieName)
}

// SearchResponse represents the response for movie search API
type SearchResponse struct {
	Movies  []Movie `json:"movies"`
	Message string  `json:"message"`
	Count   int     `json:"count"`
}

// SearchErrorResponse represents error responses for search API
type SearchErrorResponse struct {
	Error     string `json:"error"`
	Timestamp string `json:"timestamp"`
}

// Review represents a customer review for a movie
