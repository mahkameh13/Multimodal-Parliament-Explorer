# Security Policy

## Supported versions

This project is not yet distributed as a stable release. Security fixes are applied to the latest commit on the default branch.

## Reporting a vulnerability

Do not open a public issue for vulnerabilities, exposed credentials, or personal data. Use GitHub's private vulnerability reporting feature under **Security > Advisories > Report a vulnerability**.

Include the affected component, reproduction steps, impact, and any suggested mitigation. Do not access data that you do not own and do not disrupt third-party services while testing.

## Credentials

Real database credentials, API keys, private service URLs, and private keys must never be committed. Use the ignored `src/main/resources/mongodb.properties` file locally and keep only placeholders in `mongodb.example.properties`.

If a secret is exposed, revoke or rotate it immediately before removing it from the repository and its history. See [the detailed security guide](docs/security.md).
