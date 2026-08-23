// Visualisierungen für Dashboard (Aufgabe 3.1.a)

// Radar Chart – Topics
function drawTopicRadar(selector, topics) {
    const container = document.querySelector(selector);
    const rect = container.getBoundingClientRect();
    const width = rect.width || 320;
    const height = rect.height || 240;
    const radius = Math.min(width, height) / 2 - 20;

    const angleStep = 2 * Math.PI / topics.length;
    const maxValue = d3.max(topics, d => d.value) || 1;
    const rScale = d3.scaleLinear().domain([0, maxValue]).range([0, radius]);

    const svg = d3.select(selector)
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", `translate(${width/2},${height/2})`);

    const levels = 4;
    for (let l = 1; l <= levels; l++) {
        svg.append("circle")
            .attr("r", radius * l / levels)
            .attr("fill", "none")
            .attr("stroke", "#e5e7eb");
    }

    topics.forEach((d, i) => {
        const angle = i * angleStep - Math.PI/2;
        const x = radius * Math.cos(angle);
        const y = radius * Math.sin(angle);

        svg.append("line")
            .attr("x1", 0).attr("y1", 0)
            .attr("x2", x).attr("y2", y)
            .attr("stroke", "#e5e7eb");

        svg.append("text")
            .attr("x", x * 1.1)
            .attr("y", y * 1.1)
            .attr("text-anchor", "middle")
            .attr("dominant-baseline", "middle")
            .attr("font-size", "10px")
            .text(d.name);
    });

    const points = topics.map((d, i) => {
        const angle = i * angleStep - Math.PI/2;
        return [
            rScale(d.value) * Math.cos(angle),
            rScale(d.value) * Math.sin(angle)
        ];
    });

    svg.append("polygon")
        .attr("points", points.map(p => p.join(",")).join(" "))
        .attr("fill", "#2563eb")
        .attr("fill-opacity", 0.35)
        .attr("stroke", "#2563eb")
        .attr("stroke-width", 2);
}

// Sunburst – POS
function drawPosSunburst(selector, data) {
    const container = document.querySelector(selector);
    const rect = container.getBoundingClientRect();
    const width = Math.min(rect.width || 260, 260);
    const radius = width / 2 - 4;

    const root = d3.hierarchy(data).sum(d => d.value || 0);
    d3.partition().size([2 * Math.PI, radius])(root);

    const svg = d3.select(selector)
        .append("svg")
        .attr("width", width)
        .attr("height", width)
        .append("g")
        .attr("transform", `translate(${width/2},${width/2})`);

    const color = d3.scaleOrdinal(d3.schemeCategory10);

    const arc = d3.arc()
        .startAngle(d => d.x0)
        .endAngle(d => d.x1)
        .innerRadius(d => d.y0)
        .outerRadius(d => d.y1);

    svg.selectAll("path")
        .data(root.descendants().filter(d => d.depth))
        .enter().append("path")
        .attr("d", arc)
        .attr("fill", d => color(d.ancestors().map(a => a.data.name).slice(-2, -1)[0] || d.data.name))
        .append("title")
        .text(d => `${d.ancestors().map(a => a.data.name).reverse().join(" / ")}: ${d.value}`);
}

// Line Chart – Sentiment
function drawSentimentLine(selector, points) {
    const container = document.querySelector(selector);
    const rect = container.getBoundingClientRect();
    const fullWidth = rect.width || 320;
    const fullHeight = rect.height || 240;
    const margin = {top: 10, right: 20, bottom: 24, left: 32};
    const width = fullWidth - margin.left - margin.right;
    const height = fullHeight - margin.top - margin.bottom;

    const svg = d3.select(selector)
        .append("svg")
        .attr("width", fullWidth)
        .attr("height", fullHeight)
        .append("g")
        .attr("transform", `translate(${margin.left},${margin.top})`);

    const x = d3.scaleLinear()
        .domain(d3.extent(points, d => d.x))
        .range([0, width]);

    const y = d3.scaleLinear()
        .domain(d3.extent(points, d => d.sentiment))
        .nice()
        .range([height, 0]);

    const line = d3.line()
        .x(d => x(d.x))
        .y(d => y(d.sentiment))
        .curve(d3.curveMonotoneX);

    svg.append("g")
        .attr("transform", `translate(0,${height})`)
        .call(d3.axisBottom(x).ticks(5).tickSizeOuter(0));

    svg.append("g")
        .call(d3.axisLeft(y).ticks(5).tickSizeOuter(0));

    svg.append("path")
        .datum(points)
        .attr("fill", "none")
        .attr("stroke", "#2563eb")
        .attr("stroke-width", 2)
        .attr("d", line);
}

// Horizontale Bar Chart – Named Entities
function drawNerBars(selector, entities) {
    const container = document.querySelector(selector);
    const rect = container.getBoundingClientRect();
    const fullWidth = rect.width || 320;
    const fullHeight = rect.height || 240;
    const margin = {top: 10, right: 20, bottom: 10, left: 80};
    const width = fullWidth - margin.left - margin.right;
    const height = fullHeight - margin.top - margin.bottom;

    const svg = d3.select(selector)
        .append("svg")
        .attr("width", fullWidth)
        .attr("height", fullHeight)
        .append("g")
        .attr("transform", `translate(${margin.left},${margin.top})`);

    const y = d3.scaleBand()
        .domain(entities.map(d => d.name))
        .range([0, height])
        .padding(0.15);

    const x = d3.scaleLinear()
        .domain([0, d3.max(entities, d => d.count) || 1])
        .range([0, width]);

    svg.append("g")
        .call(d3.axisLeft(y).tickSizeOuter(0));

    svg.selectAll("rect")
        .data(entities)
        .enter().append("rect")
        .attr("y", d => y(d.name))
        .attr("height", y.bandwidth())
        .attr("x", 0)
        .attr("width", d => x(d.count))
        .attr("fill", "#2563eb");

    svg.selectAll("text.value")
        .data(entities)
        .enter().append("text")
        .attr("class", "value")
        .attr("x", d => x(d.count) + 4)
        .attr("y", d => y(d.name) + y.bandwidth() / 2)
        .attr("dominant-baseline", "middle")
        .attr("font-size", "10px")
        .text(d => d.count);
}

// Initialisierung mit Dummy-Daten (später durch REST ersetzen)
document.addEventListener("DOMContentLoaded", () => {
    drawTopicRadar("#topicRadar", [
        {name: "Wirtschaft", value: 0.8},
        {name: "Soziales", value: 0.6},
        {name: "Außenpolitik", value: 0.3},
        {name: "Innenpolitik", value: 0.5},
        {name: "Umwelt", value: 0.4}
    ]);

    drawPosSunburst("#posSunburst", {
        name: "POS",
        children: [
            {name: "NOUN", value: 500},
            {name: "VERB", value: 400},
            {name: "ADJ", value: 200},
            {name: "ADV", value: 150}
        ]
    });

    drawSentimentLine("#sentimentLine", [
        {x: 1, sentiment: -0.1},
        {x: 2, sentiment: 0.2},
        {x: 3, sentiment: 0.4},
        {x: 4, sentiment: 0.1},
        {x: 5, sentiment: -0.3},
        {x: 6, sentiment: 0.0},
        {x: 7, sentiment: 0.5}
    ]);

    drawNerBars("#nerBars", [
        {name: "Deutschland", count: 120},
        {name: "SPD", count: 90},
        {name: "Berlin", count: 80},
        {name: "CDU/CSU", count: 60},
        {name: "Europa", count: 40}
    ]);

    // Suche (Aufgabe 3.1.c) – Enter im Suchfeld startet Suche
    const searchInput = document.querySelector(".hero-search");
    if (searchInput) {
        searchInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                performSearch();
            }
        });
    }
});

// ----- Suche (Aufgabe 3.1.c) -----

function renderSearchResults(items) {
    const container = document.getElementById("searchResults");
    if (!container) return;

    container.innerHTML = "";

    if (!items || items.length === 0) {
        container.textContent = "Keine Treffer gefunden.";
        return;
    }

    items.forEach(item => {
        const card = document.createElement("div");
        card.className = "search-result-card";
        card.style.border = "1px solid #e5e7eb";
        card.style.borderRadius = "8px";
        card.style.padding = "8px 12px";
        card.style.marginBottom = "8px";
        card.style.background = "#ffffff";

        const title = document.createElement("div");
        title.style.fontWeight = "600";
        title.textContent = (item.speaker || "Unbekannter Sprecher") +
            (item.fraction ? " (" + item.fraction + ")" : "");

        const meta = document.createElement("div");
        meta.style.fontSize = "0.85rem";
        meta.style.color = "#6b7280";
        meta.textContent = (item.date || "") + (item.speechId ? " · ID: " + item.speechId : "");

        const preview = document.createElement("div");
        preview.style.marginTop = "4px";
        preview.textContent = item.preview || "";

        const sentiment = document.createElement("div");
        sentiment.style.marginTop = "4px";
        sentiment.style.fontSize = "0.85rem";
        if (typeof item.sentiment === "number") {
            let label = "neutral";
            if (item.sentiment > 0.1) label = "positiv";
            if (item.sentiment < -0.1) label = "negativ";
            sentiment.textContent = "Sentiment: " + item.sentiment.toFixed(2) + " (" + label + ")";
        }

        const link = document.createElement("button");
        link.textContent = "Ansehen";
        link.style.marginTop = "4px";
        link.style.fontSize = "0.85rem";
        link.style.padding = "4px 8px";
        link.style.borderRadius = "999px";
        link.style.border = "1px solid #d1d5db";
        link.style.background = "#f9fafb";
        link.onclick = () => {
            alert("Detailansicht für Rede " + (item.speechId || ""));
        };

        card.appendChild(title);
        card.appendChild(meta);
        card.appendChild(preview);
        card.appendChild(sentiment);
        card.appendChild(link);

        container.appendChild(card);
    });
}

function performSearch() {
    const input = document.querySelector(".hero-search");
    if (!input) return;
    const query = input.value.trim();
    const params = new URLSearchParams();
    if (query) params.set("query", query);

    fetch("/api/search/speeches?" + params.toString())
        .then(r => r.json())
        .then(renderSearchResults)
        .catch(err => {
            console.error(err);
            const container = document.getElementById("searchResults");
            if (container) container.textContent = "Fehler bei der Suche.";
        });
}
