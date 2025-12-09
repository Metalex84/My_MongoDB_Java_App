package com.example.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("No se encontró el archivo " + CONFIG_FILE);
                System.err.println("Asegúrate de que config.properties existe en la raíz del proyecto");
                throw new RuntimeException("Archivo de configuración no encontrado: " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error al cargar la configuración", ex);
        }
    }

    public static String getDbUser() {
        return properties.getProperty("db.user");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public static String getDbHost() {
        return properties.getProperty("db.host");
    }

    public static String getDbName() {
        return properties.getProperty("db.name");
    }

    public static String getConnectionString() {
        String user = getDbUser();
        String password = getDbPassword();
        String host = getDbHost();
        String dbName = getDbName();
        
        return String.format(
            "mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority&appName=%s",
            user, password, host, dbName
        );
    }
}
