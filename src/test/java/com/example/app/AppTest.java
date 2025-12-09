package com.example.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for App class
 */
class AppTest {
    
    @Test
    void testMongoClientSettingsCreation() {
        String connectionString = "mongodb+srv://metalex84:<db_password>@cluster0.g2mpfuw.mongodb.net/?appName=Cluster0";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        assertNotNull(settings);
        assertNotNull(settings.getServerApi());
        assertEquals(ServerApiVersion.V1, settings.getServerApi().getVersion());
    }

    @Test
    void testConnectionStringParsing() {
        String connectionString = "mongodb+srv://metalex84:<db_password>@cluster0.g2mpfuw.mongodb.net/?appName=Cluster0";
        ConnectionString connStr = new ConnectionString(connectionString);
        
        assertNotNull(connStr);
        assertEquals("cluster0.g2mpfuw.mongodb.net", connStr.getHosts().get(0));
    }
}
