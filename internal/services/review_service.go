package services

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"

	"github.com/mbourgeois-fetch/sample-qdev-movies/internal/models"
)

// ReviewService handles all review-related business logic
type ReviewService struct {
	reviews []models.Review
}

// NewReviewService creates a new instance of ReviewService
func NewReviewService() *ReviewService {
	service := &ReviewService{}
	service.loadReviews()
	return service
}

// loadReviews loads review data from JSON file
func (s *ReviewService) loadReviews() {
	log.Println("📝 Loading customer reviews from mock-reviews.json...")

	data, err := ioutil.ReadFile("data/mock-reviews.json")
	if err != nil {
		log.Printf("Arrr! Error reading mock-reviews.json: %v", err)
		return
	}

	err = json.Unmarshal(data, &s.reviews)
	if err != nil {
		log.Printf("Shiver me timbers! Error parsing mock-reviews.json: %v", err)
		return
	}

	log.Printf("⭐ Successfully loaded %d reviews!", len(s.reviews))
}

// GetReviewsForMovie returns all reviews for a specific movie
func (s *ReviewService) GetReviewsForMovie(movieID int64) []models.Review {
	var movieReviews []models.Review

	for _, review := range s.reviews {
		if review.MovieID == movieID {
			movieReviews = append(movieReviews, review)
		}
	}

	return movieReviews
}

// GetReviewByID finds a specific review by its ID
func (s *ReviewService) GetReviewByID(id int64) (*models.Review, error) {
	for _, review := range s.reviews {
		if review.ID == id {
			return &review, nil
		}
	}

	return nil, fmt.Errorf("review with ID %d not found", id)
}
