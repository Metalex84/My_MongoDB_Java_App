package com.example.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://metalex84:ktmEXC-2025%40@cluster0.g2mpfuw.mongodb.net/?appName=Cluster0";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase adminDb = mongoClient.getDatabase("admin");
                adminDb.runCommand(new Document("ping", 1));
                System.out.println("✓ Successfully connected to MongoDB!");
                System.out.println();

                // Connect to sample_mflix database
                MongoDatabase mflixDb = mongoClient.getDatabase("sample_mflix");
                System.out.println("=== Exploring sample_mflix Database ===");
                System.out.println();

                // List all collections in the database
                System.out.println("Collections in sample_mflix:");
                List<String> collectionNames = new ArrayList<>();
                mflixDb.listCollectionNames().into(collectionNames);
                
                for (String collectionName : collectionNames) {
                    System.out.println("  - " + collectionName);
                }
                System.out.println();

                // Explore schema of each collection by getting sample documents
                for (String collectionName : collectionNames) {
                    System.out.println("--- Collection: " + collectionName + " ---");
                    MongoCollection<Document> collection = mflixDb.getCollection(collectionName);
                    
                    // Get count
                    long count = collection.countDocuments();
                    System.out.println("Document count: " + count);
                    
                    // Get a sample document to show schema
                    Document sampleDoc = collection.find().limit(1).first();
                    if (sampleDoc != null) {
                        System.out.println("Sample document structure:");
                        System.out.println(sampleDoc.toJson());
                    } else {
                        System.out.println("No documents found in this collection.");
                    }
                    System.out.println();
                }

                // Demonstrate CRUD operations with MovieRepository
                System.out.println("=== Demonstrating CRUD Operations on Movies Collection ===");
                System.out.println();
                
                MovieRepository movieRepo = new MovieRepository(mflixDb);
                
                // READ operations
                System.out.println("1. READ Operations:");
                System.out.println("   Total movies in database: " + movieRepo.countMovies());
                
                // Find movies by title
                List<Document> titanicMovies = movieRepo.findMoviesByTitle("Titanic");
                System.out.println("   Movies with 'Titanic' in title: " + titanicMovies.size());
                if (!titanicMovies.isEmpty()) {
                    Document firstTitanic = titanicMovies.get(0);
                    System.out.println("     Example: " + firstTitanic.getString("title") + " (" + firstTitanic.getInteger("year") + ")");
                }
                
                // Find movies by year
                List<Document> movies2010 = movieRepo.findMoviesByYear(2010);
                System.out.println("   Movies from 2010: " + movies2010.size());
                
                // Find movies by genre
                List<Document> actionMovies = movieRepo.findMoviesByGenre("Action");
                System.out.println("   Action movies (limited to 10): " + actionMovies.size());
                if (!actionMovies.isEmpty()) {
                    System.out.println("     Example: " + actionMovies.get(0).getString("title"));
                }
                System.out.println();
                
                // CREATE operation
                System.out.println("2. CREATE Operation:");
                Document newMovie = new Document("title", "Test Movie 2025")
                        .append("year", 2025)
                        .append("genres", List.of("Drama", "Sci-Fi"))
                        .append("plot", "A test movie for MongoDB operations demo")
                        .append("runtime", 120)
                        .append("imdb", new Document("rating", 8.5).append("votes", 1000));
                
                ObjectId newMovieId = movieRepo.createMovie(newMovie);
                System.out.println("   Created new movie with ID: " + newMovieId);
                System.out.println();
                
                // UPDATE operations
                System.out.println("3. UPDATE Operations:");
                long updatedRating = movieRepo.updateMovieRating(newMovieId, 9.0);
                System.out.println("   Updated rating for movie: " + updatedRating + " document(s) modified");
                
                long addedGenre = movieRepo.addGenreToMovie(newMovieId, "Thriller");
                System.out.println("   Added genre to movie: " + addedGenre + " document(s) modified");
                
                // Verify update
                Document updatedMovie = movieRepo.findMovieById(newMovieId);
                if (updatedMovie != null) {
                    System.out.println("   Updated movie: " + updatedMovie.getString("title"));
                    System.out.println("   New rating: " + updatedMovie.get("imdb", Document.class).get("rating"));
                    System.out.println("   Genres: " + updatedMovie.getList("genres", String.class));
                }
                System.out.println();
                
                // DELETE operation
                System.out.println("4. DELETE Operation:");
                long deleted = movieRepo.deleteMovieById(newMovieId);
                System.out.println("   Deleted movie: " + deleted + " document(s) deleted");
                
                // Verify deletion
                Document deletedMovie = movieRepo.findMovieById(newMovieId);
                System.out.println("   Verification - Movie exists after deletion: " + (deletedMovie != null));
                System.out.println();
                
                System.out.println("✓ All CRUD operations completed successfully!");

            } catch (MongoException e) {
                System.err.println("MongoDB error occurred:");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Error occurred:");
            e.printStackTrace();
        }
    }
}
