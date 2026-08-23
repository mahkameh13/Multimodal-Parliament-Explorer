# Multimodal Parliament Explorer

Java application for importing, processing, querying, and visualizing parliamentary debates from the German Bundestag.

## Technology

- Java 21 and Maven
- Javalin REST service
- FreeMarker templates
- MongoDB
- Apache UIMA and DUUI
- d3.js visualizations

## Setup

1. Install Java 21 and Maven.
2. Copy `src/main/resources/mongodb.example.properties` to `src/main/resources/mongodb.properties`.
3. Enter your own MongoDB connection values in the new local file.
4. Build the project with `mvn clean package`.
5. Start `rest.testHandler` from the IDE or Maven.
6. Open `http://localhost:7777/dashboard`.

The Swagger UI is available at `http://localhost:7777/swagger` and the OpenAPI specification at `http://localhost:7777/openapi.yaml`.

## Documentation

The documentation is organized as a GitBook-compatible set of Markdown pages in [`docs/`](docs/README.md). Start with the [Getting Started guide](docs/getting-started.md).

## Security

Credentials are intentionally not stored in this repository. Keep `mongodb.properties`, environment files, private keys, and course-specific access files local. See [Security](docs/security.md).

## Data and third-party software

Bundestag data and media are not covered by the repository's software license. Review the [data and content notice](DATA_AND_CONTENT_NOTICE.md) before importing, redistributing, or publishing derived material. Direct dependency licenses are summarized in [third-party notices](THIRD_PARTY_NOTICES.md).

## Contributing and licensing status

See [Contributing](CONTRIBUTING.md) and the [Code of Conduct](CODE_OF_CONDUCT.md). Until a root `LICENSE` file is added with authorization from all contributors, no permission to copy, modify, or redistribute this project is granted.
