# Frontend and Visualizations

The frontend is served by Javalin and rendered with FreeMarker. The dashboard template is located at `src/main/resources/templates/dashboard.ftl`, and its d3.js logic is in `src/main/resources/js/visualization.js`.

## Dashboard

The dashboard links four NLP aggregations to the required chart types:

- topics as a radar chart;
- POS tags as a sunburst chart;
- sentiments as a line chart;
- named entities as a horizontal bar chart.

The browser requests JSON from the visualization endpoints and updates charts when filters change.

## Search

`/api/search/speeches` supports speech search and returns compact result objects with speech ID, speaker, fraction, date, preview text, topic information, and sentiment where available.

## Extending the interface

1. Add or update the REST aggregation first.
2. Document its response in `openapi.yaml`.
3. Add the required FreeMarker controls and containers.
4. Render the response with d3.js.
5. Test empty results, malformed fields, and mobile layout.
