# Copilot Instructions - Vinyl Master Project

## Project Overview

This is the **Vinyl Master** project - a system for managing vinyl record collections. The project is currently in the documentation/planning phase, using AsciiDoc for all documentation that is automatically converted to HTML and published via GitHub Pages.

### What This Repository Contains

- **Documentation only** - No application code yet (the `src/` folder only contains a `.env` file with API keys)
- System specification (`sysspec.adoc`) defining requirements for a vinyl collection management system
- Project uses German for documentation content (Austrian school project - HTL Leonding)
- Planning includes: product backlog, meeting minutes, and system design

### Project Goals (from sysspec.adoc)

The system aims to help vinyl collectors:
- Catalog and organize collections (manual entry or barcode scanning via EAN/UPC codes)
- Track physical storage locations (e.g., "Shelf A, Compartment 3")
- View collection statistics (total value via Discogs API, genre distribution, count)
- Manage wishlists and detect duplicate purchases
- Search and filter albums by various criteria

**Key APIs**: Discogs API for metadata and market prices (rate limit: 60 req/min)

## Build & Publish Workflow

### Local Development

**Convert AsciiDoc to HTML locally:**
```bash
./local-convert.sh
```
- Requires Docker installed locally
- Uses the `asciidoctor/docker-asciidoctor` image (version 1.58)
- Configuration in `config.sh`: input from `asciidocs/`, output to `dist/`, creates slides

**Publish locally-generated HTML to GitHub Pages:**
```bash
./publish.sh
```
- Runs `local-convert.sh` first
- Force-pushes `dist/` content to the `gh-pages` branch

### Automatic GitHub Actions Build

Workflow file: `.github/workflows/docs.yaml`

**Triggers:**
- Push to `main` branch with changes in `asciidocs/**`
- Manual workflow dispatch

**What it does:**
1. Runs `asciidocs/scripts/docker-convert.sh` in Docker
2. Converts all `.adoc` files to HTML (docs) and reveal.js slides
3. Deploys to `gh-pages` branch automatically

**Environment variables:**
- `INPUTPATH=asciidocs`
- `SLIDES=true` (enables reveal.js slide generation)
- `BRANCH=gh-pages`

## Documentation Structure

```
asciidocs/
├── docs/           # Main documentation (converted to HTML)
│   ├── index.adoc
│   ├── sysspec.adoc          # System specification
│   ├── product-backlog.adoc
│   ├── minutes-of-meeting.adoc
│   ├── demo.adoc
│   ├── docinfo.html          # Custom HTML header injection
│   ├── images/               # Images for docs
│   └── themes/               # Custom themes (favicon, etc.)
├── slides/         # Presentation slides (converted to reveal.js)
│   ├── demo.adoc
│   ├── images/
│   └── css/
└── scripts/        # Conversion scripts (Docker-based)
    ├── docker-convert.sh
    ├── docker-convert-util.sh
    └── PLEASE_DO_NOT_TOUCH.txt
```

## AsciiDoc Conventions

### Standard Document Header
```asciidoc
= Document Title
Version, {docdate}:
ifndef::imagesdir[:imagesdir: images]
:sourcedir: ../src/main/java
:icons: font
:sectnums:
:toc: left
```

### For Slides (reveal.js)
Slides use the `asciidoctor-revealjs` backend with:
- Theme: `league`
- Slide numbering: current/total
- Transition: `slide`
- Hash navigation enabled

### Conversion Details

**HTML (docs):**
- Source highlighter: `rouge` (GitHub theme)
- Table of contents: left sidebar, 2 levels
- Section anchors and numbering enabled
- Asciidoctor Diagram extension for PlantUML/Graphviz
- FontAwesome icons

**Slides:**
- reveal.js version: 5.0.0 (downloaded during build)
- Output in `slides/` subdirectory
- Same diagram support as docs

## Key Files to Avoid Modifying

⚠️ **Do NOT modify scripts in `asciidocs/scripts/`** unless you know exactly what you're doing (per README.adoc warning)

These scripts handle the Docker-based AsciiDoc → HTML conversion pipeline.

## Published Documentation URLs

- Docs: `https://<github-repo-owner>.github.io/<github-repo-name>`
- Slides: `https://<github-repo-owner>.github.io/<github-repo-name>/slides/<file-name>.html`

Example: `https://2526-3bhif-syp.github.io/2526-3bhif-syp-project-vinyl-master/sysspec`

## API Keys & Secrets

- Discogs API key is in `src/.env` (format: `"DISCOGS" = "key"`)
- **Never commit secrets to the repository** - ensure `.env` files are gitignored

## Language & Context

- **Documentation language**: German (Austrian)
- **Target users**: Vinyl record collectors
- **Performance requirement**: Search in collections up to 5,000 records must complete in <200ms
- **Offline requirement**: Basic search must work without internet connection
- **Timeline**: MVP prototype within 3 months (as per sysspec.adoc)

## Resources

- [AsciiDoc Quick Reference](https://docs.asciidoctor.org/asciidoc/latest/syntax-quick-reference/)
- [AsciiDoc Writer's Guide](https://asciidoctor.org/docs/asciidoc-writers-guide/)
- [Asciidoctor reveal.js Features](https://docs.asciidoctor.org/reveal.js-converter/latest/converter/features/)
- [Discogs API](https://www.discogs.com/developers/) - Rate limit: 60 requests/minute
