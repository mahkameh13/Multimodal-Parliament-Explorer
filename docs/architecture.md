# Architecture

The application is organized as a Maven project with a Java backend and server-rendered frontend.

## Packages

| Package | Responsibility |
| --- | --- |
| `databaseSpeech` | Speech, protocol, agenda, comment, and text data structures |
| `databaseMember` | Member metadata, images, and video-related structures |
| `interfaces` | Contracts for speech and member domain objects |
| `mongodbConnection` | MongoDB configuration and CRUD, aggregation, and count operations |
| `importer` | Protocol discovery, download, parsing, and scheduled imports |
| `InputDatabase` | Database-oriented import helpers |
| `nlp` | DUUI pipeline, CAS mapping, feature extraction, and serialization |
| `rest` | Javalin configuration, routes, templates, search, and visualization aggregation |

## Request flow

1. `rest.testHandler` loads the local MongoDB configuration.
2. `MongoDatabaseHandler` opens the database connection.
3. `ConfigJavalin` loads the server port.
4. `RestHandler` registers API, Swagger, dashboard, and search routes.
5. REST handlers query MongoDB and use `rest.Utils` for visualization aggregates.
6. FreeMarker renders the dashboard and d3.js requests the visualization endpoints.

## Data flow

Bundestag sources are downloaded and parsed into domain objects. Those objects are serialized as MongoDB documents. NLP processing maps speech text into UIMA CAS objects, applies DUUI components, and stores the resulting annotations for later aggregation and display.
