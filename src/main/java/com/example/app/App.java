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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Cargar las credenciales de forma separada desde el archivo de configuración
        String dbUser;
        String dbPassword;
        String dbHost;
        String dbName;
        
        try {
            dbUser = ConfigManager.getDbUser();
            dbPassword = ConfigManager.getDbPassword();
            dbHost = ConfigManager.getDbHost();
            dbName = ConfigManager.getDbName();
        } catch (RuntimeException e) {
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.err.println("ERROR: No se pudo cargar el archivo de configuración");
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.err.println();
            System.err.println("Para conectarse a MongoDB, necesitas crear un archivo de configuración.");
            System.err.println();
            System.err.println("PASOS A SEGUIR:");
            System.err.println();
            System.err.println("1. Crea un archivo llamado 'config.properties' en src/main/resources/");
            System.err.println();
            System.err.println("2. Agrega el siguiente contenido al archivo:");
            System.err.println();
            System.err.println("   ┌─────────────────────────────────────────────────────────┐");
            System.err.println("   │ db.user=tu_usuario_mongodb                              │");
            System.err.println("   │ db.password=tu_contraseña_mongodb                       │");
            System.err.println("   │ db.host=tu_cluster.mongodb.net                          │");
            System.err.println("   │ db.name=nombre_de_tu_aplicacion                         │");
            System.err.println("   └─────────────────────────────────────────────────────────┘");
            System.err.println();
            System.err.println("3. Reemplaza los valores con tus credenciales de MongoDB Atlas:");
            System.err.println("   - db.user: Tu nombre de usuario de MongoDB");
            System.err.println("   - db.password: Tu contraseña de MongoDB");
            System.err.println("   - db.host: El host de tu cluster (ej: cluster0.abc123.mongodb.net)");
            System.err.println("   - db.name: El nombre de tu aplicación");
            System.err.println();
            System.err.println("NOTA: Asegúrate de que config.properties esté en tu .gitignore");
            System.err.println("      para no subir tus credenciales al repositorio.");
            System.err.println();
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.err.println();
            System.err.println("Causa del error: " + e.getMessage());
            System.err.println();
            System.exit(1);
            return;
        }
        
        // Construir la cadena de conexión sin exponer credenciales en el código fuente
        // Codificar usuario y contraseña para manejar caracteres especiales
        String encodedUser = URLEncoder.encode(dbUser, StandardCharsets.UTF_8);
        String encodedPassword = URLEncoder.encode(dbPassword, StandardCharsets.UTF_8);
        
        String connectionString = String.format(
            "mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority&appName=%s",
            encodedUser, encodedPassword, dbHost, dbName
        );

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
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error occurred:");
            e.printStackTrace();
            System.exit(1);
        }
        
        System.exit(0);
    }
}
