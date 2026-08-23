# Multimodal Parliament Explorer

The Multimodal Parliament Explorer combines parliamentary text, member metadata, NLP annotations, and media information in one application. It imports Bundestag data, stores structured documents in MongoDB, exposes analysis results through a Javalin REST API, and renders interactive views with FreeMarker and d3.js.

## Main capabilities

- Import parliamentary protocols and member master data.
- Represent speeches, protocols, agenda items, comments, and members as Java objects.
- Store and query data through a dedicated MongoDB handler.
- Process speech content with UIMA and DUUI components.
- Aggregate topics, POS tags, sentiments, and named entities.
- Search speeches and expose data through REST endpoints.
- Render a dashboard with d3.js visualizations.

Use [Getting Started](getting-started.md) to configure and run the application.

## Source code

The source repository is available on [GitHub](https://github.com/mahkameh13/Multimodal-Parliament-Explorer). Access is restricted to repository collaborators.
