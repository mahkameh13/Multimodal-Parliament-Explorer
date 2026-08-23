# Third-Party Notices

This project depends on third-party software. The dependency's own license controls that dependency. Consult the linked upstream project and the resolved Maven artifact for the exact license text and version-specific notices.

| Component | Version | License |
| --- | --- | --- |
| Docker Unified UIMA Interface | 1.5.3 | AGPL-3.0-or-later |
| Text Technology Lab UIMA TypeSystem | 3.0.14 | AGPL-3.0-or-later |
| Apache UIMA | 3.5.0 | Apache-2.0 |
| Javalin and Javalin Rendering | 6.7.0 | Apache-2.0 |
| MongoDB Java Driver | 5.6.1 | Apache-2.0 |
| FreeMarker | 2.3.34 | Apache-2.0 |
| jsoup | 1.18.1 | MIT |
| SLF4J | 2.0.16 | MIT |
| JSON-java | 20250517 | Public Domain |
| JUnit Jupiter | 5.8.1 | EPL-2.0 |
| D3.js | 7.x, loaded at runtime | ISC |

The AGPL-licensed DUUI and UIMA TypeSystem dependencies affect the compatible licensing choices for distributions of the combined application. This project must not be released under a conflicting license.

Maven resolves additional transitive dependencies. A release must include a dependency inventory or SBOM generated from the exact release revision; this file is not a substitute for that complete inventory.
