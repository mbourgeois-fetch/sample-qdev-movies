package utils

import "strings"

// GetMovieIcon returns a pirate-themed icon based on the movie name
func GetMovieIcon(movieName string) string {
	// Convert to lowercase for case-insensitive matching
	name := strings.ToLower(movieName)
	
	// Map movie themes to pirate-appropriate icons
	if strings.Contains(name, "prison") || strings.Contains(name, "escape") {
		return "⛓️"
	} else if strings.Contains(name, "family") || strings.Contains(name, "boss") {
		return "👑"
	} else if strings.Contains(name, "hero") || strings.Contains(name, "masked") {
		return "🦸"
	} else if strings.Contains(name, "space") || strings.Contains(name, "wars") {
		return "🚀"
	} else if strings.Contains(name, "quest") || strings.Contains(name, "ring") {
		return "💍"
	} else if strings.Contains(name, "virtual") || strings.Contains(name, "matrix") {
		return "💊"
	}
	return "🎬" // Default movie icon
}
