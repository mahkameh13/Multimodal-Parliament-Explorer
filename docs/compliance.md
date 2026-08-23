# Compliance and Reuse

## Licensing status

The project cannot be treated as open source until a root `LICENSE` file is present and every copyright holder has authorized that license. Removing names or attribution comments does not transfer copyright or replace that authorization.

The direct DUUI and Text Technology Lab UIMA TypeSystem dependencies use `AGPL-3.0-or-later`. Any project license must be compatible with those dependencies. See [Third-Party Notices](../THIRD_PARTY_NOTICES.md).

## Data and media

The software license does not grant rights to Bundestag data, photographs, videos, or third-party NLP models. The application downloads member data at runtime instead of versioning a local copy. Review the [Data and Content Notice](../DATA_AND_CONTENT_NOTICE.md) before publishing imported or derived content.

## Release checklist

- Obtain written publication and license approval from every contributor.
- Confirm that publication complies with the applicable course and institutional rules.
- Add the approved compatible license and copyright notices.
- Publish only the sanitized single-commit release history; do not expose the earlier project history.
- Run a secret and personal-data scan across that release history.
- Generate a dependency inventory or SBOM from the release revision.
- Complete `mvn clean verify` in a clean environment.
- Review all screenshots, examples, and generated artifacts for third-party rights and personal data.

## Security

Follow the [Security Policy](../SECURITY.md). Real credentials and private infrastructure details must remain outside Git, and exposed credentials must be rotated even if they are later removed from history.
