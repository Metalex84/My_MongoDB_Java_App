package com.example.app;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for performing CRUD operations on the movies collection
 */
public class MovieRepository {
    private final MongoCollection<Document> moviesCollection;

    public MovieRepository(MongoDatabase database) {
        this.moviesCollection = database.getCollection("movies");
    }

    /**
     * Create - Insert a new movie document
     */
    public ObjectId createMovie(Document movie) {
        moviesCollection.insertOne(movie);
        return movie.getObjectId("_id");
    }

    /**
     * Read - Find a movie by ID
     */
    public Document findMovieById(ObjectId id) {
        return moviesCollection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Read - Find movies by title (case-insensitive partial match)
     */
    public List<Document> findMoviesByTitle(String title) {
        List<Document> movies = new ArrayList<>();
        Bson filter = Filters.regex("title", title, "i");
        moviesCollection.find(filter).into(movies);
        return movies;
    }

    /**
     * Read - Find movies by year
     */
    public List<Document> findMoviesByYear(int year) {
        List<Document> movies = new ArrayList<>();
        moviesCollection.find(Filters.eq("year", year)).into(movies);
        return movies;
    }

    /**
     * Read - Find movies by genre
     */
    public List<Document> findMoviesByGenre(String genre) {
        List<Document> movies = new ArrayList<>();
        Bson filter = Filters.in("genres", genre);
        moviesCollection.find(filter).limit(10).into(movies);
        return movies;
    }

    /**
     * Read - Get all movies (with limit)
     */
    public List<Document> getAllMovies(int limit) {
        List<Document> movies = new ArrayList<>();
        moviesCollection.find().limit(limit).into(movies);
        return movies;
    }

    /**
     * Update - Update a movie's rating
     */
    public long updateMovieRating(ObjectId id, Object rating) {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.set("imdb.rating", rating);
        UpdateResult result = moviesCollection.updateOne(filter, update);
        return result.getModifiedCount();
    }

    /**
     * Update - Add a genre to a movie
     */
    public long addGenreToMovie(ObjectId id, String genre) {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.addToSet("genres", genre);
        UpdateResult result = moviesCollection.updateOne(filter, update);
        return result.getModifiedCount();
    }

    /**
     * Delete - Delete a movie by ID
     */
    public long deleteMovieById(ObjectId id) {
        DeleteResult result = moviesCollection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount();
    }

    /**
     * Count total movies
     */
    public long countMovies() {
        return moviesCollection.countDocuments();
    }

    /**
     * Count movies by filter
     */
    public long countMoviesByYear(int year) {
        return moviesCollection.countDocuments(Filters.eq("year", year));
    }
}
