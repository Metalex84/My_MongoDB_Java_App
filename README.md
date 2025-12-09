# MongoDB Java Application

A Java Maven project demonstrating MongoDB operations using the MongoDB Java Driver with the `sample_mflix` database.

## Overview

This application connects to a MongoDB Atlas cluster and performs CRUD (Create, Read, Update, Delete) operations on the `sample_mflix` database, specifically working with the movies collection.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB Atlas account with access to the sample_mflix database

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── example/
│               └── app/
│                   ├── App.java              # Main application
│                   └── MovieRepository.java  # Repository for movie operations
└── test/
    └── java/
        └── com/
            └── example/
                └── app/
                    ├── AppTest.java              # Basic connection tests
                    └── MovieRepositoryTest.java  # Movie repository unit tests
```

## Database Schema (sample_mflix)

The `sample_mflix` database contains the following collections:

- **movies** (21,349 documents) - Movie information including title, year, genres, plot, cast, ratings
- **comments** (41,079 documents) - User comments on movies
- **users** (185 documents) - User information
- **theaters** (1,564 documents) - Theater locations
- **sessions** (1 document) - Session data
- **embedded_movies** (3,483 documents) - Movies with embedded data

## Features

### MovieRepository CRUD Operations

#### Create
- `createMovie(Document movie)` - Insert a new movie document

#### Read
- `findMovieById(ObjectId id)` - Find a movie by its ID
- `findMoviesByTitle(String title)` - Search movies by title (case-insensitive)
- `findMoviesByYear(int year)` - Find movies released in a specific year
- `findMoviesByGenre(String genre)` - Find movies by genre
- `getAllMovies(int limit)` - Get all movies with a limit
- `countMovies()` - Count total movies
- `countMoviesByYear(int year)` - Count movies by year

#### Update
- `updateMovieRating(ObjectId id, Object rating)` - Update a movie's IMDB rating
- `addGenreToMovie(ObjectId id, String genre)` - Add a genre to a movie

#### Delete
- `deleteMovieById(ObjectId id)` - Delete a movie by ID

## Running the Application

### Build the project
```bash
mvn clean compile
```

### Run tests
```bash
mvn test
```

### Run the application
```bash
mvn exec:java
```

## What the Application Does

When you run the application, it:

1. **Connects** to MongoDB Atlas using the configured connection string
2. **Explores** the database schema by listing all collections and showing sample documents
3. **Demonstrates CRUD operations**:
   - **READ**: Queries movies by title, year, and genre
   - **CREATE**: Inserts a new test movie
   - **UPDATE**: Updates the movie's rating and adds a new genre
   - **DELETE**: Removes the test movie
   - **VERIFY**: Confirms the deletion was successful

## Sample Output

```
✓ Successfully connected to MongoDB!

=== Exploring sample_mflix Database ===

Collections in sample_mflix:
  - theaters
  - comments
  - movies
  - users
  - embedded_movies
  - sessions

=== Demonstrating CRUD Operations on Movies Collection ===

1. READ Operations:
   Total movies in database: 21349
   Movies with 'Titanic' in title: 7
     Example: Raise the Titanic (1980)
   Movies from 2010: 866
   Action movies (limited to 10): 10

2. CREATE Operation:
   Created new movie with ID: 6938635e43b78a68bdc1ffd2

3. UPDATE Operations:
   Updated rating for movie: 1 document(s) modified
   Added genre to movie: 1 document(s) modified
   Updated movie: Test Movie 2025
   New rating: 9.0
   Genres: [Drama, Sci-Fi, Thriller]

4. DELETE Operation:
   Deleted movie: 1 document(s) deleted
   Verification - Movie exists after deletion: false

✓ All CRUD operations completed successfully!
```

## Testing

The project includes comprehensive unit tests:

- **AppTest**: Tests MongoDB connection and settings configuration
- **MovieRepositoryTest**: Tests movie document creation, updates, and field operations

All tests use JUnit 5 and Mockito for mocking MongoDB components.

## Dependencies

- MongoDB Java Driver (Sync): 5.2.1
- JUnit Jupiter: 5.10.1
- Mockito: 5.8.0

## Movie Document Structure

A typical movie document in the collection includes:

```json
{
  "_id": ObjectId,
  "title": String,
  "year": Integer,
  "genres": [String],
  "plot": String,
  "runtime": Integer,
  "rated": String,
  "cast": [String],
  "directors": [String],
  "imdb": {
    "rating": Double,
    "votes": Integer
  }
}
```

## Notes

- The connection string in `App.java` contains URL-encoded credentials
- The application uses MongoDB's ServerAPI V1 for stable API compatibility
- Test operations (create/update/delete) are performed on temporary test data
- The application automatically cleans up test data after demonstrating operations
