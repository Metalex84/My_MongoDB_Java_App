# Configuración de Credenciales de MongoDB

Este proyecto utiliza un archivo de configuración local `config.properties` para almacenar las credenciales de MongoDB de forma segura.

## 🔒 Seguridad

El archivo `config.properties` está **excluido de Git** (ver `.gitignore`) para evitar que las credenciales se versionen públicamente. Las credenciales se cargan en tiempo de ejecución y nunca se exponen en el código fuente.

## 📋 Configuración Inicial

### 1. Crear el archivo de configuración

Crea el archivo `config.properties` en `src/main/resources/`:

**Windows (PowerShell):**
```powershell
New-Item -Path "src\main\resources\config.properties" -ItemType File -Force
```

**Linux/Mac:**
```bash
mkdir -p src/main/resources
touch src/main/resources/config.properties
```

### 2. Completar los valores

Edita `config.properties` con tus credenciales reales de MongoDB Atlas:

```properties
# Configuración de MongoDB
db.user=tu_usuario
db.password=tu_contraseña
db.host=tu_cluster.mongodb.net
db.name=tu_aplicacion
```

**Ejemplo:**
```properties
db.user=myuser
db.password=MyP@ssw0rd!
db.host=cluster0.abc123.mongodb.net
db.name=MyApp
```

> **Importante**: La contraseña debe escribirse **sin codificar** (usa caracteres normales como `@`, `#`, etc.). La aplicación se encarga automáticamente de la codificación URL.

### 3. Verificar la ubicación

El archivo debe estar en `src/main/resources/` para que Maven lo incluya en el classpath:

```
My_MongoDB_Java_App/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       └── config.properties  ← AQUÍ
│   └── test/
├── target/
├── pom.xml
└── .gitignore
```

## 🔧 Uso en el Código

La clase `ConfigManager` carga automáticamente las credenciales desde `config.properties`:

```java
// Acceder a valores individuales
String user = ConfigManager.getDbUser();
String password = ConfigManager.getDbPassword();
String host = ConfigManager.getDbHost();
String dbName = ConfigManager.getDbName();

// La aplicación construye la cadena de conexión automáticamente
// con codificación URL para caracteres especiales
String encodedUser = URLEncoder.encode(user, StandardCharsets.UTF_8);
String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8);
String connectionString = String.format(
    "mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority&appName=%s",
    encodedUser, encodedPassword, host, dbName
);
```

### Características de Seguridad

1. **Separación de credenciales**: Las credenciales nunca están en el código fuente
2. **Codificación automática**: Los caracteres especiales en contraseñas se manejan automáticamente
3. **Carga dinámica**: Las credenciales se cargan solo en tiempo de ejecución
4. **No versionado**: El archivo `.gitignore` excluye `config.properties`

## ⚠️ Importante

- **Nunca** hagas commit de `config.properties` al repositorio
- Si accidentalmente commiteas credenciales, cambia inmediatamente la contraseña en MongoDB Atlas
- Verifica que `config.properties` esté en tu `.gitignore`
- No uses codificación URL en el archivo de configuración (escribe `@` en lugar de `%40`)

## 🚀 Compilación y Ejecución

### Compilar el proyecto

```bash
mvn clean compile
```

### Ejecutar la aplicación

```bash
mvn exec:java
```

La aplicación cargará automáticamente `config.properties` desde `src/main/resources/` y se conectará a MongoDB.

### Ejecutar tests

```bash
mvn test
```

## 🐛 Solución de Problemas

### Error: "Archivo de configuración no encontrado"

1. Verifica que `config.properties` existe en `src/main/resources/`
2. Verifica los permisos del archivo
3. Reconstruye el proyecto: `mvn clean compile`

### Error: "MongoTimeoutException" o "Failed looking up SRV record"

1. Verifica que `db.host` tiene el valor correcto (e.g., `cluster0.abc123.mongodb.net`)
2. Verifica tu conexión a Internet
3. Verifica que tu IP está en la lista blanca de MongoDB Atlas

### Error: "Authentication failed"

1. Verifica que `db.user` y `db.password` son correctos
2. Asegúrate de no usar codificación URL en la contraseña (usa `@` no `%40`)
3. Verifica los permisos del usuario en MongoDB Atlas

### La aplicación no termina (pregunta "Desea terminar el trabajo por lotes?")

Esto ya está solucionado. La aplicación ahora usa `System.exit(0)` para terminar limpiamente.
