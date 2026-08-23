# Data and NLP

## Data sources

The importer configuration references public Bundestag resources for parliamentary protocols, member master data, images, and selected media pages. Download and parsing logic is implemented with Java and Jsoup.

## Domain model

Speeches are the central entities. A speech can be associated with a protocol, agenda item, speaker, text sections, and comments. Members have separate metadata and image structures.

## UIMA representation

`src/main/resources/desc/type/TypeSystem.xml` defines the project-specific UIMA type system. Maven invokes JCasGen during source generation and writes generated classes into the build directory.

## DUUI pipeline

The NLP package provides pipeline and feature classes for:

- token, sentence, POS, dependency, named-entity, and lemma information;
- topic classification;
- sentence-level sentiment;
- sarcasm detection;
- video transcription when a configured Whisper service is available.

Service endpoints are configured in `src/main/resources/nlp.properties`. Availability and access requirements depend on the selected deployment.

## Stored analysis data

The REST aggregation utilities expect NLP data beneath each speech document, including sentence annotations, POS counts, named entities, sentiment values, and top topic scores. Keep the stored field names aligned with `rest.Utils` when changing the NLP serializer.
