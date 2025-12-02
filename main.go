package main

import (
	"html/template"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/mbourgeois-fetch/sample-qdev-movies/internal/handlers"
	"github.com/mbourgeois-fetch/sample-qdev-movies/internal/services"
)

// Ahoy! Main application entry point for our pirate movie treasure chest
func main() {
	log.Println("🏴‍☠️ Starting Pirate's Movie Treasure Chest server...")

	// Initialize services
	movieService := services.NewMovieService()
	reviewService := services.NewReviewService()

	// Initialize handlers
	movieHandler := handlers.NewMovieHandler(movieService, reviewService)

	// Setup Gin router
	router := gin.Default()

	// Setup template functions
	router.SetFuncMap(template.FuncMap{
		"generateStars": handlers.GenerateStars,
	})

	// Load HTML templates with functions
	router.LoadHTMLGlob("templates/*")

	// Serve static files (CSS, JS, images)
	router.Static("/css", "./static/css")
	router.Static("/js", "./static/js")
	router.Static("/images", "./static/images")

	// Setup routes
	setupRoutes(router, movieHandler)

	log.Println("🚢 Server ready to sail on port 8080!")
	log.Println("⚓ Navigate to http://localhost:8080/movies to start yer adventure!")
	log.Fatal(http.ListenAndServe(":8080", router))
}

func setupRoutes(router *gin.Engine, movieHandler *handlers.MovieHandler) {
	// Redirect root to movies page
	router.GET("/", func(c *gin.Context) {
		c.Redirect(http.StatusMovedPermanently, "/movies")
	})
	
	// Movie routes
	router.GET("/movies", movieHandler.GetMovies)
	router.GET("/movies/:id/details", movieHandler.GetMovieDetails)
	router.GET("/movies/search", movieHandler.SearchMovies)
}
