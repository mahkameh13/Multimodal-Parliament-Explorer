# Contributing

## Before contributing

- Open an issue before making a substantial behavioral or architectural change.
- Do not submit credentials, private URLs, database exports, copyrighted media, or personal data.
- Only contribute material that you have the right to publish.
- Preserve source attribution and applicable third-party notices.

## Development workflow

1. Create a branch from `main`.
2. Configure Java 21 and Maven.
3. Copy `src/main/resources/mongodb.example.properties` to the ignored `mongodb.properties` file and add only local credentials.
4. Run `mvn clean verify` before opening a pull request.
5. Explain the change, its tests, and any data or license implications in the pull request.

## License of contributions

Once the project license is finalized, contributions are accepted under that same license. By submitting a contribution, you certify that you have the right to do so and that no undisclosed third-party restrictions apply.

Until a root `LICENSE` file is present, no license to copy, modify, or redistribute the project is granted.
