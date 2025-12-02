# 🏴‍☠️ Pirate's Movie Treasure Chest - Go Web Application

Ahoy matey! Welcome to the most swashbuckling movie catalog web application on the seven seas! Built with Go and the Gin web framework, infused with proper pirate spirit, this treasure chest demonstrates Go web development best practices while keeping things fun and engaging.

## ⚓ Features

- **🎬 Movie Catalog**: Browse 12 classic movies with detailed information, displayed like precious treasures
- **🔍 Movie Search & Filtering**: Search through our treasure chest by movie name, ID, or genre with our new pirate-powered search functionality!
- **📋 Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **⭐ Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **📱 Responsive Design**: Mobile-first design that works on all devices, from ship to shore
- **🎨 Modern UI**: Dark theme with gradient backgrounds, smooth animations, and pirate flair
- **🔍 Advanced Search**: Real-time search with multiple filters and pirate-themed error messages

## 🛠️ Technology Stack

- **Go 1.21** - The foundation of our ship
- **Gin Web Framework** - Our trusty sailing framework
- **Go Modules** for dependency management - Keeping our cargo organized
- **Logrus** - For tracking our adventures
- **Testify** - Testing our treasure maps
- **HTML Templates** - For crafting beautiful HTML templates
- **JavaScript** - For interactive search functionality

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher - Ye need this to sail these waters
- Go 1.21 or higher - Ye need this to sail these waters
- Git - For managing our treasure
### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
git clone https://github.com/mbourgeois-fetch/sample-qdev-movies.git
mvn spring-boot:run
go mod tidy
go run main.go

The application will start on `http://localhost:8080` - Chart your course there, matey!

### Access the Application

- **🏴‍☠️ Movie Treasure Chest**: http://localhost:8080/movies
- **📋 Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🔍 Search API**: http://localhost:8080/movies/search (see API documentation below)

## 🏗️ Building for Production

```bash
mvn clean package
go build -o movie-server main.go
./movie-server

## 📁 Project Structure

```
src/
├── main.go                                   # Main application entry point
├── go.mod                                    # Go module dependencies
├── internal/
│   ├── handlers/
│   │   ├── movie_handler.go                  # HTTP handlers with search endpoints
│   │   └── movie_handler_test.go             # Handler tests
│   ├── services/
│   │   ├── movie_service.go                  # Business logic with search functionality
│   │   ├── movie_service_test.go             # Service tests for search methods
│   │   └── review_service.go                 # Review business logic
│   ├── models/
│   │   ├── movie.go                          # Movie data model
│   │   └── review.go                         # Review data model
│   └── utils/
│       └── movie_icon_utils.go               # Movie icon utilities
├── data/
│   ├── movies.json                           # Movie treasure data
│   └── mock-reviews.json                     # Mock review data
├── templates/
│   ├── movies.html                           # Main movie list with search form
│   └── movie-details.html                    # Movie details page
└── static/css/
    ├── movies.css                            # Styling with search form styles
    └── movie-details.css                     # Movie details styling

## 🗺️ API Endpoints

### Get All Movies (HTML)
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a search form for finding specific treasures.

**Features:**
- Interactive search form with movie name, ID, and genre filters
- Real-time search results with pirate-themed messages
- Responsive design that works on all devices

### Get Movie Details (HTML)
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🔍 Search Movies (REST API)
```
GET /movies/search
```
**Ahoy! This be our new treasure hunting endpoint!** Search through our movie collection using various criteria with proper pirate flair.

**Query Parameters:**
- `name` (optional): Movie name to search for (case-insensitive, partial matches allowed)
- `id` (optional): Specific movie ID to find (takes precedence over other parameters)
- `genre` (optional): Genre to filter by (case-insensitive, partial matches allowed)

**Examples:**
```bash
# Search by movie name
GET /movies/search?name=Prison

# Search by genre
GET /movies/search?genre=Drama

# Search by specific ID
GET /movies/search?id=1

# Search by name and genre combined
GET /movies/search?name=The&genre=Drama

# Case-insensitive search
GET /movies/search?name=PRISON&genre=DRAMA
```

**Response Format:**

**Success Response (200 OK):**
```json
{
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "message": "Ahoy! Found 1 movie matching yer search, ye savvy pirate!",
  "count": 1
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Arrr! Ye must provide at least one search parameter, matey! Use 'name', 'id', or 'genre' to find yer treasure.",
  "timestamp": "2023-12-07T10:30:00Z"
}
```

**No Results Response (200 OK):**
```json
{
  "movies": [],
  "message": "No movies found matching yer search criteria, matey! Try different search terms or check yer spelling.",
  "count": 0
}
```

### 🔍 Search Features

**Search Capabilities:**
- **Case-insensitive**: Search for "PRISON" or "prison" - both work!
- **Partial matching**: Search "The" to find all movies with "The" in the name
- **Multiple criteria**: Combine name and genre filters for precise results
- **ID precedence**: When searching by ID, other parameters are ignored
- **Flexible input**: Empty or whitespace-only parameters are ignored gracefully

**Error Handling:**
- Validates that at least one search parameter is provided
- Ensures movie IDs are positive numbers
- Returns pirate-themed error messages for better user experience
- Handles edge cases like very long strings or special characters

## 🧪 Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test
go test ./...
# Run specific test class
# Run specific test package
go test ./internal/services
# Run tests with coverage
mvn test jacoco:report
go test -cover ./...

# Run tests with verbose output
go test -v ./...

**Test Coverage:**
- **MovieServiceTest**: Tests all search functionality, edge cases, and data validation
- **movie_service_test.go**: Tests all search functionality, edge cases, and data validation
- **movie_handler_test.go**: Tests HTTP handlers, error handling, and response formats
- **Integration tests**: Complete request/response cycle testing
## 🐛 Troubleshooting

### Port 8080 already in use

```bash
# Find and kill process using port 8080 (macOS/Linux)
lsof -ti:8080 | xargs kill

# Or change the port in main.go
log.Fatal(http.ListenAndServe(":8081", router))
```

### Build issues

Clean module cache and rebuild:
```bash
go clean -modcache
go mod tidy
go build
```

### Search not working

Check the browser console for JavaScript errors and ensure:
- The application is running on the correct port
- JSON data files are in the `data/` directory
- Templates are in the `templates/` directory
- Static files are in the `static/` directory

### Missing dependencies

```bash
go mod download
go mod tidy
```

## 🤝 Contributing

This project is designed as a demonstration application with pirate flair! Feel free to:
- Add more movies to the treasure chest
- Enhance the UI/UX with more pirate themes
- Improve search functionality with additional filters
- Add new features like movie ratings or favorites
- Enhance the responsive design for better mobile experience
- Add more comprehensive error handling
- Implement database storage instead of JSON files
- Add authentication and user management
- Create a proper logging system
- Add metrics and monitoring

## 📜 License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

**Arrr! May fair winds fill yer sails as ye explore this movie treasure chest! 🏴‍☠️**
