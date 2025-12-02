package models

// Review represents a customer review for a movie
type Review struct {
	ID         int64   `json:"id"`
	MovieID    int64   `json:"movieId"`
	CustomerID int64   `json:"customerId"`
	Rating     float64 `json:"rating"`
	Comment    string  `json:"comment"`
	Avatar     string  `json:"avatar"`
	Name       string  `json:"name"`
}
