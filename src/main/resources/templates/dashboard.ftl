<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <title>Multimodal Parliament Explorer – Dashboard</title>
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <link rel="stylesheet" href="/static/css/styles.css"><!-- optional -->
    <style>
        body { font-family: system-ui, sans-serif; margin: 0; background:#f5f5f7; }
        header, footer { background:#111827; color:#fff; padding:12px 32px; }
        .nav { display:flex; align-items:center; gap:24px; }
        .nav-logo { font-weight:bold; }
        .nav-links a { color:#e5e7eb; text-decoration:none; margin-right:16px; }
        .nav-links a.active { color:#fff; font-weight:600; }

        main { padding:24px 32px 48px; max-width:1200px; margin:0 auto; }

        .hero-title { font-size: 1.8rem; margin-bottom:4px; }
        .hero-subtitle { color:#6b7280; margin-bottom:16px; }
        .hero-search { width:100%; max-width:480px; padding:8px 12px; border-radius:999px;
                       border:1px solid #d1d5db; }

        .section-title { margin-top:24px; margin-bottom:4px; font-size:1.1rem; }
        .section-subtitle { color:#6b7280; margin-bottom:16px; }

        .dashboard-grid {
            display:grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap:16px;
        }

        .card {
            background:#fff;
            border-radius:12px;
            padding:16px;
            box-shadow:0 1px 3px rgba(0,0,0,0.08);
            display:flex;
            flex-direction:column;
            gap:8px;
        }
        .card h2 { font-size:1rem; margin:0; }
        .card p { font-size:0.9rem; color:#6b7280; margin:0; }

        .chart-wrapper { width:100%; height:240px; }

        .btn-details {
            align-self:flex-start;
            margin-top:4px;
            padding:6px 10px;
            font-size:0.85rem;
            border-radius:999px;
            border:1px solid #e5e7eb;
            background:#f9fafb;
            color:#374151;
            text-decoration:none;
        }

        .filters {
            margin-top:24px;
            background:#fff;
            border-radius:12px;
            padding:16px;
            box-shadow:0 1px 3px rgba(0,0,0,0.06);
        }
        .filters h2 { margin-top:0; font-size:1rem; }
        .filter-buttons {
            display:flex;
            flex-wrap:wrap;
            gap:8px;
            margin-top:8px;
        }
        .filter-pill {
            padding:6px 12px;
            border-radius:999px;
            border:1px solid #d1d5db;
            background:#f9fafb;
            font-size:0.85rem;
            cursor:pointer;
        }
        .filter-pill.primary {
            background:#111827;
            color:#fff;
            border-color:#111827;
        }

        footer { font-size:0.8rem; text-align:center; }
    </style>
</head>
<body>
<header>
    <div class="nav">
        <div class="nav-logo">Multimodal Parliament Explorer</div>
        <nav class="nav-links">
            <a href="/" class="active">Home</a>
            <a href="/dashboard">Dashboard</a>
            <a href="/speeches">Reden</a>
            <a href="/analysis">Analysen</a>
            <a href="/export">Export</a>
            <a href="/settings">Einstellungen</a>
        </nav>
    </div>
</header>

<main>
    <!-- Hero-Bereich wie im Mockup -->
    <section>
        <h1 class="hero-title">Willkommen beim Parliament Explorer</h1>
        <p class="hero-subtitle">Suche nach Reden, Sprechern, Themen...</p>
        <input type="search" class="hero-search"
               placeholder="Suche nach Reden, Sprechern, Themen..." />
    </section>

    <!-- Beschreibung des Dashboards -->
    <section>
        <h2 class="section-title">Dashboard: Analyse parlamentarischer Debatten</h2>
        <p class="section-subtitle">
            Übersicht der zentralen Kennzahlen und visuellen Analysen für Ihre parlamentarischen Datensätze.
        </p>
    </section>

    <!-- Suchergebnisse -->
    <section id="searchResultsSection">
        <h2 class="section-title">Suchergebnisse</h2>
        <div id="searchResults"></div>
    </section>

    <!-- Vier Visualisierungskarten wie im Mockup -->
    <section class="dashboard-grid">
        <!-- Radar Chart – Topics -->
        <article class="card">
            <h2>Radar Chart – Verteilung der Topics</h2>
            <p>Verteilung der Topics in einer netzartigen Struktur.</p>
            <div id="topicRadar" class="chart-wrapper"></div>
            <a href="/topics/details" class="btn-details">Details anzeigen</a>
        </article>

        <!-- Sunburst – POS -->
        <article class="card">
            <h2>Sunburst Chart – Verteilung der POS</h2>
            <p>Hierarchische Darstellung der Part-of-Speech-Tags.</p>
            <div id="posSunburst" class="chart-wrapper"></div>
            <a href="/pos/details" class="btn-details">Details anzeigen</a>
        </article>

        <!-- Line Chart – Sentiment -->
        <article class="card">
            <h2>Line Chart – Verteilung der Sentiments</h2>
            <p>Zeitlicher Verlauf der Sentiment-Werte.</p>
            <div id="sentimentLine" class="chart-wrapper"></div>
            <a href="/sentiment/details" class="btn-details">Details anzeigen</a>
        </article>

        <!-- Bar Chart – Named Entities -->
        <article class="card">
            <h2>Horizontal Bar Chart – Verteilung der Named Entities</h2>
            <p>Rangliste der identifizierten Named Entities.</p>
            <div id="nerBars" class="chart-wrapper"></div>
            <a href="/entities/details" class="btn-details">Details anzeigen</a>
        </article>
    </section>

    <!-- Filteroptionen wie im Mockup -->
    <section class="filters">
        <h2>Filteroptionen</h2>
        <div class="filter-buttons">
            <button class="filter-pill primary" data-scope="all">Alle Reden (Standard)</button>
            <button class="filter-pill" data-scope="single">Einzelne Rede</button>
            <button class="filter-pill" data-scope="multiple">Mehrere Reden</button>
            <button class="filter-pill" data-scope="topic">Nach Topic</button>
        </div>
    </section>
</main>

<footer>
    © 2026 Multimodal Parliament Explorer
</footer>

<script src="/js/visualization.js"></script>
</body>
</html>
