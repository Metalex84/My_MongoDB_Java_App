package com.example.app;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MovieRepository class
 * Note: These are mock-based tests. For integration tests with real MongoDB,
 * you would need a test database or use testcontainers.
 */
class MovieRepositoryTest {

    @Mock
    private MongoDatabase mockDatabase;

    @Mock
    private MongoCollection<Document> mockCollection;

    private MovieRepository movieRepository;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(mockDatabase.getCollection(anyString())).thenReturn(mockCollection);
        movieRepository = new MovieRepository(mockDatabase);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should create MovieRepository instance")
    void testMovieRepositoryCreation() {
        assertNotNull(movieRepository);
    }

    @Test
    @DisplayName("Should create a valid movie document")
    void testCreateMovieDocument() {
        Document movie = new Document("title", "Test Movie")
                .append("year", 2024)
                .append("genres", List.of("Drama", "Action"))
                .append("plot", "A test plot")
                .append("runtime", 120);

        assertNotNull(movie);
        assertEquals("Test Movie", movie.getString("title"));
        assertEquals(2024, movie.getInteger("year"));
        assertEquals(120, movie.getInteger("runtime"));
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) movie.get("genres");
        assertNotNull(genres);
        assertEquals(2, genres.size());
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action"));
    }

    @Test
    @DisplayName("Should create a movie with nested IMDB document")
    void testCreateMovieWithIMDB() {
        Document imdb = new Document("rating", 8.5)
                .append("votes", 10000);

        Document movie = new Document("title", "Test Movie")
                .append("year", 2024)
                .append("imdb", imdb);

        assertNotNull(movie.get("imdb"));
        Document retrievedImdb = (Document) movie.get("imdb");
        assertEquals(8.5, retrievedImdb.getDouble("rating"));
        assertEquals(10000, retrievedImdb.getInteger("votes"));
    }

    @Test
    @DisplayName("Should handle ObjectId generation")
    void testObjectIdHandling() {
        ObjectId id = new ObjectId();
        assertNotNull(id);
        assertTrue(id.toString().length() > 0);

        Document movie = new Document("_id", id)
                .append("title", "Test Movie");

        assertEquals(id, movie.getObjectId("_id"));
    }

    @Test
    @DisplayName("Should create movie document with all fields")
    void testCompleteMovieDocument() {
        Document movie = new Document("title", "Complete Test Movie")
                .append("year", 2024)
                .append("genres", List.of("Drama", "Sci-Fi"))
                .append("plot", "A comprehensive test plot")
                .append("runtime", 135)
                .append("rated", "PG-13")
                .append("cast", List.of("Actor 1", "Actor 2"))
                .append("directors", List.of("Director 1"))
                .append("imdb", new Document("rating", 7.8).append("votes", 5000));

        // Verify all fields
        assertEquals("Complete Test Movie", movie.getString("title"));
        assertEquals(2024, movie.getInteger("year"));
        assertEquals(135, movie.getInteger("runtime"));
        assertEquals("PG-13", movie.getString("rated"));
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) movie.get("genres");
        assertEquals(2, genres.size());
        
        @SuppressWarnings("unchecked")
        List<String> cast = (List<String>) movie.get("cast");
        assertEquals(2, cast.size());
        
        Document imdb = (Document) movie.get("imdb");
        assertNotNull(imdb);
        assertEquals(7.8, imdb.getDouble("rating"));
    }

    @Test
    @DisplayName("Should handle null and missing fields gracefully")
    void testNullAndMissingFields() {
        Document movie = new Document("title", "Minimal Movie");

        // Test getting non-existent fields
        assertNull(movie.getInteger("year"));
        assertNull(movie.getString("plot"));
        assertNull(movie.get("genres"));
    }

    @Test
    @DisplayName("Should update movie fields correctly")
    void testMovieFieldUpdates() {
        Document movie = new Document("title", "Original Title")
                .append("year", 2024);

        // Update fields
        movie.put("title", "Updated Title");
        movie.put("year", 2025);
        movie.append("newField", "New Value");

        assertEquals("Updated Title", movie.getString("title"));
        assertEquals(2025, movie.getInteger("year"));
        assertEquals("New Value", movie.getString("newField"));
    }

    @Test
    @DisplayName("Should handle array operations on genres")
    void testGenreArrayOperations() {
        List<String> genres = List.of("Drama", "Action", "Thriller");
        Document movie = new Document("title", "Test Movie")
                .append("genres", genres);

        @SuppressWarnings("unchecked")
        List<String> retrievedGenres = (List<String>) movie.get("genres");
        
        assertNotNull(retrievedGenres);
        assertEquals(3, retrievedGenres.size());
        assertTrue(retrievedGenres.contains("Drama"));
        assertTrue(retrievedGenres.contains("Action"));
        assertTrue(retrievedGenres.contains("Thriller"));
        assertFalse(retrievedGenres.contains("Comedy"));
    }
}
