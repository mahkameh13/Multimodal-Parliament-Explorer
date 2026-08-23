# REST API

The Javalin service exposes JSON endpoints and an OpenAPI document. Use `/swagger` for the interactive API view.

## Core resources

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/api/member` | Read member data |
| `POST` | `/api/member` | Create member data |
| `DELETE` | `/api/member/` | Delete member data |
| `GET` | `/api/speech` | Read a speech |
| `POST` | `/api/speech` | Create speech data |
| `DELETE` | `/api/speech/` | Delete speech data |
| `GET` | `/api/search/speeches` | Search and filter speeches |

## Visualization data

| Method | Path | Returned aggregation |
| --- | --- | --- |
| `GET` | `/api/visualization/topics` | Topic distribution |
| `GET` | `/api/visualization/pos` | POS distribution |
| `GET` | `/api/visualization/sentiment` | Sentiment values by speech |
| `GET` | `/api/visualization/namedEntities` | Named-entity counts |

## API documentation

- OpenAPI source: `src/main/resources/openapi.yaml`
- Runtime specification: `/openapi.yaml`
- Swagger UI: `/swagger`

When routes or parameters change, update both `RestHandler` and `openapi.yaml` in the same commit.
