package handlers

import (
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/mbourgeois-fetch/sample-qdev-movies/internal/models"
	"github.com/mbourgeois-fetch/sample-qdev-movies/internal/services"
)

// MovieHandler handles HTTP requests for movie operations
type MovieHandler struct {
	movieService  *services.MovieService
	reviewService *services.ReviewService
}

// NewMovieHandler creates a new MovieHandler instance
func NewMovieHandler(movieService *services.MovieService, reviewService *services.ReviewService) *MovieHandler {
	return &MovieHandler{
		movieService:  movieService,
		reviewService: reviewService,
	}
}

// GetMovies handles the main movies page with search form
func (h *MovieHandler) GetMovies(c *gin.Context) {
	movies := h.movieService.GetAllMovies()
	genres := h.movieService.GetAllGenres()

	c.HTML(http.StatusOK, "movies.html", gin.H{
		"movies": movies,
		"genres": genres,
	})
}

// GetMovieDetails handles the movie details page
func (h *MovieHandler) GetMovieDetails(c *gin.Context) {
	idParam := c.Param("id")
	id, err := strconv.ParseInt(idParam, 10, 64)
	if err != nil {
		c.HTML(http.StatusBadRequest, "error.html", gin.H{
			"error": "Arrr! Invalid movie ID, ye scurvy dog!",
		})
		return
	}

	movie, err := h.movieService.GetMovieByID(id)
	if err != nil {
		c.HTML(http.StatusNotFound, "error.html", gin.H{
			"error": "Shiver me timbers! Movie not found in our treasure chest!",
		})
		return
	}

	reviews := h.reviewService.GetReviewsForMovie(id)

	c.HTML(http.StatusOK, "movie-details.html", gin.H{
		"movie":   movie,
		"reviews": reviews,
	})
}

// SearchMovies handles the REST API endpoint for searching movies
// Ahoy matey! This be the REST endpoint for searching through our movie treasure!
// Accepts query parameters to filter movies like a true pirate captain.
func (h *MovieHandler) SearchMovies(c *gin.Context) {
	name := c.Query("name")
	idStr := c.Query("id")
	genre := c.Query("genre")

	// Log the search request
	c.Header("Content-Type", "application/json")

	// Validate that at least one search parameter is provided
	if (name == "" || strings.TrimSpace(name) == "") &&
		idStr == "" &&
		(genre == "" || strings.TrimSpace(genre) == "") {

		c.JSON(http.StatusBadRequest, models.SearchErrorResponse{
			Error:     "Arrr! Ye must provide at least one search parameter, matey! Use 'name', 'id', or 'genre' to find yer treasure.",
			Timestamp: time.Now().Format(time.RFC3339),
		})
		return
	}

	var movieID *int64
	if idStr != "" {
		id, err := strconv.ParseInt(idStr, 10, 64)
		if err != nil || id <= 0 {
			c.JSON(http.StatusBadRequest, models.SearchErrorResponse{
				Error:     "Shiver me timbers! Movie ID must be a positive number, ye landlubber!",
				Timestamp: time.Now().Format(time.RFC3339),
			})
			return
		}
		movieID = &id
	}

	// Perform the search
	searchResults := h.movieService.SearchMovies(name, movieID, genre)

	if len(searchResults) == 0 {
		c.JSON(http.StatusOK, models.SearchResponse{
			Movies:  searchResults,
			Message: "No movies found matching yer search criteria, matey! Try different search terms or check yer spelling.",
			Count:   0,
		})
		return
	}

	// Create success message
	var message string
	if len(searchResults) == 1 {
		message = "Ahoy! Found 1 movie matching yer search, ye savvy pirate!"
	} else {
		message = "Ahoy! Found " + strconv.Itoa(len(searchResults)) + " movies matching yer search, ye savvy pirate!"
	}

	c.JSON(http.StatusOK, models.SearchResponse{
		Movies:  searchResults,
		Message: message,
		Count:   len(searchResults),
	})
}

// Helper function to handle errors gracefully
func (h *MovieHandler) handleError(c *gin.Context, statusCode int, message string) {
	if c.GetHeader("Accept") == "application/json" || strings.Contains(c.GetHeader("Accept"), "application/json") {
		c.JSON(statusCode, models.SearchErrorResponse{
			Error:     message,
			Timestamp: time.Now().Format(time.RFC3339),
		})
	} else {
		c.HTML(statusCode, "error.html", gin.H{
			"error": message,
		})
	}
}

// Additional helper methods for template rendering

// GenerateStars creates a star rating string for templates
func GenerateStars(rating float64) string {
	stars := ""
	for i := 1; i <= 5; i++ {
		if float64(i) <= rating {
			stars += "★"
		} else if float64(i)-0.5 == rating {
			stars += "⭐"
		} else {
			stars += "☆"
		}
	}
	return stars
}

// FormatDuration formats movie duration for display
func FormatDuration(minutes int) string {
	hours := minutes / 60
	mins := minutes % 60
	if hours > 0 {
		return strconv.Itoa(hours) + "h " + strconv.Itoa(mins) + "m"
	}
	return strconv.Itoa(mins) + " minutes"
}

// TruncateDescription truncates long descriptions for card display
func TruncateDescription(description string, maxLength int) string {
	if len(description) <= maxLength {
		return description
	}
	return description[:maxLength] + "..."
}
