# Getting Started

## Requirements

- Java Development Kit 21
- Maven 3.9 or newer
- Access to a MongoDB database
- Network access to the configured Bundestag and DUUI services when importing or processing data

## Configure MongoDB

Create the local configuration from the supplied template:

```bash
cp src/main/resources/mongodb.example.properties src/main/resources/mongodb.properties
```

Replace all placeholder values in `mongodb.properties`. The real file is ignored by Git and must never be committed.

## Build

```bash
mvn clean package
```

The Maven build targets Java 21 and generates JCas classes from `src/main/resources/desc/type/TypeSystem.xml`.

## Run the web service

Run the main method in `rest.testHandler`. The configured default port is `7777`.

Available entry points:

- Dashboard: `http://localhost:7777/dashboard`
- Swagger UI: `http://localhost:7777/swagger`
- OpenAPI: `http://localhost:7777/openapi.yaml`

## First checks

1. Confirm that the application can connect to MongoDB.
2. Open `/swagger` and test a read-only endpoint.
3. Open `/dashboard` and verify that the template and JavaScript assets load.
4. Run import or NLP operations only after confirming the configured external services are reachable.
