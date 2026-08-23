# Configuration

Configuration files are stored in `src/main/resources`.

| File | Purpose | Repository policy |
| --- | --- | --- |
| `javalin.properties` | HTTP server port | Committed |
| `importwps.properties` | Public Bundestag protocol source | Committed |
| `memberdate.properties` | Public member-data source and local paths | Committed |
| `nlp.properties` | DUUI service endpoints | Committed only when endpoints are non-secret |
| `videos.properties` | Selected public Bundestag media page | Committed |
| `mongodb.example.properties` | Placeholder database configuration | Committed |
| `mongodb.properties` | Real database credentials | Never committed |

## MongoDB keys

The application expects these keys in the local `mongodb.properties` file:

```properties
remote_host = YOUR_MONGODB_HOST
remote_database = YOUR_DATABASE_NAME
remote_user = YOUR_DATABASE_USER
remote_password = YOUR_DATABASE_PASSWORD
remote_port = 27017
```

If a DUUI endpoint requires authentication, move it to a local ignored configuration rather than committing credentials in `nlp.properties`.
