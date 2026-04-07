# Copilot Instructions - Vinyl Master Project

## Project Overview

**Vinyl Master** is a JavaFX desktop application for managing vinyl record collections. It combines a robust backend (Service + Repository pattern) with an intuitive JavaFX UI, backed by PostgreSQL for data persistence.

The project also includes comprehensive AsciiDoc documentation that is automatically converted to HTML and published via GitHub Pages.

### Current Status

**Version 1.0.0** - MVP Complete
- ✅ Full JavaFX application with add vinyl functionality
- ✅ PostgreSQL database with Flyway migrations
- ✅ Backend with Service + Repository layers
- ✅ Frontend validation and duplicate detection
- ✅ Unit tests for service layer
- ✅ Multi-module Gradle build

### Project Goals

The system helps vinyl collectors:
- ✅ **Catalog and organize collections** (manual entry implemented)
- 🔜 Track physical storage locations
- 🔜 View collection statistics (total value via Discogs API, genre distribution)
- ✅ **Manage wishlists and detect duplicate purchases**
- 🔜 Search and filter albums by various criteria
- 🔜 Barcode scanning via EAN/UPC codes

**Key APIs**: Discogs API for metadata and market prices (rate limit: 60 req/min) - planned for future implementation

## Build, Test, and Run Commands

### Build
```bash
# Full build (both modules)
./gradlew build

# Build specific module
./gradlew :backend:build
./gradlew :frontend:build

# Clean build
./gradlew clean build
```

### Test
```bash
# Run all tests
./gradlew test

# Run backend tests only
./gradlew :backend:test

# Run with detailed output
./gradlew test --info

# Run single test class
./gradlew :backend:test --tests "VinylServiceImplTest"
```

### Run Application
```bash
# Run directly (requires Docker database running)
./gradlew :frontend:run

# Build and run standalone JAR
./gradlew :frontend:shadowJar
java -jar src/frontend/build/libs/frontend-1.0.0-all.jar
```

### Database
```bash
# Start PostgreSQL
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs postgres

# Stop
docker-compose down

# Reset (WARNING: deletes all data)
docker-compose down -v
```

## High-Level Architecture

### Multi-Module Gradle Structure

```
vinylmaster/
├── build.gradle.kts          # Root build config (Java 21, common settings)
├── settings.gradle.kts       # Module definitions
├── .env                      # Database credentials
├── docker-compose.yml        # PostgreSQL setup
│
├── src/backend/              # Backend module (business logic + data)
│   ├── build.gradle.kts     # Dependencies: PostgreSQL, HikariCP, Flyway
│   └── src/
│       ├── main/java/at/htl/leonding/vinylmaster/
│       │   ├── config/      # DatabaseConfig (HikariCP + Flyway)
│       │   ├── model/       # Vinyl entity
│       │   ├── repository/  # Data access (VinylRepository, GenreRepository)
│       │   └── service/     # Business logic (VinylService, validation, duplicates)
│       ├── main/resources/
│       │   └── db/migration/  # Flyway SQL migrations
│       └── test/java/       # JUnit 5 tests
│
└── src/frontend/             # Frontend module (JavaFX UI)
    ├── build.gradle.kts     # Dependencies: JavaFX, backend module, shadow plugin
    └── src/
        ├── main/java/at/htl/leonding/vinylmaster/ui/
        │   ├── Main.java              # Application entry point
        │   └── controller/            # JavaFX controllers
        │       ├── MainViewController.java      # Collection list view
        │       └── AddVinylFormController.java  # Add vinyl form
        └── main/resources/
            ├── fxml/          # FXML view definitions
            │   ├── main-view.fxml
            │   └── add-vinyl-form.fxml
            └── images/        # UI assets (vinyl-placeholder.png)
```

### Backend Architecture (Service + Repository Pattern)

**1. Repository Layer** (`repository/`)
- Handles all database operations using JDBC
- Uses HikariCP connection pool for efficiency
- Repositories: `VinylRepository`, `GenreRepository`
- Methods: `save()`, `findAll()`, `findById()`, `findByTitleAndArtistAndYear()`, `existsByTitleAndArtistAndYear()`

**2. Service Layer** (`service/`)
- Contains all business logic
- Validates data before persistence
- Checks for duplicates (same title + artist + year)
- Services: `VinylService`, `GenreService`
- Exceptions: `ValidationException`, `DuplicateVinylException`

**3. Database Configuration** (`config/`)
- `DatabaseConfig`: Initializes HikariCP, runs Flyway migrations
- Connection pool settings: max 10 connections, min 2 idle
- Reads credentials from `.env` file via `dotenv-java`

**4. Database Schema** (Flyway: `V1__create_vinyl_table.sql`)
```sql
vinyl table:
  - id (serial primary key)
  - title (text, required)
  - artist (text, required)
  - genre (text, optional)
  - year (integer, required)
  - price (numeric, optional)
  - created_at (timestamp)
  - Index on (title, artist, year) for duplicate checks

genre table:
  - id (serial primary key)
  - name (text unique, required)
  - Pre-populated with 8 default genres
```

### Frontend Architecture (JavaFX MVC)

**1. Main Application** (`Main.java`)
- Loads `main-view.fxml`
- Sets up primary stage (800x600)
- Closes database connection pool on shutdown

**2. Main View Controller** (`MainViewController.java`)
- Displays collection as ListView with custom cells
- Each cell shows: `| image | title | artist | price |`
- "Add Vinyl" button triggers modal overlay
- Auto-refreshes list after adding vinyl
- Custom `VinylListCell` renders each vinyl item

**3. Add Vinyl Form Controller** (`AddVinylFormController.java`)
- Modal overlay form (semi-transparent background)
- Fields: title*, artist*, year*, genre (dropdown), price (optional)
- **Frontend validation** (on-blur):
  - Title/Artist: not empty → red border if invalid
  - Year: 4-digit number (1900-2100) → red border if invalid
  - Price: valid decimal, non-negative → red border if invalid
- **Backend validation** (on save):
  - Calls `VinylService.addVinyl()`
  - Shows error dialog for ValidationException or DuplicateVinylException
- Genre dropdown: editable, loads from `GenreRepository`, saves new genres

**4. FXML Views**
- `main-view.fxml`: StackPane with VBox (for modal overlay support)
- `add-vinyl-form.fxml`: Centered VBox with form fields, styled buttons

## Key Conventions

### Package Naming
- Base package: `at.htl.leonding.vinylmaster`
- Backend: `at.htl.leonding.vinylmaster.{config,model,repository,service}`
- Frontend: `at.htl.leonding.vinylmaster.ui.{controller,view}`

### Data Flow
1. User fills form in `AddVinylFormController`
2. Frontend validates on blur (visual feedback)
3. On save, calls `VinylService.addVinyl()`
4. Service validates data (throws `ValidationException` if invalid)
5. Service checks duplicates (throws `DuplicateVinylException` if exists)
6. Repository saves to database via JDBC
7. Controller refreshes list view
8. Modal closes, main view updates

### Validation Rules
**Required Fields:**
- Title (not empty)
- Artist (not empty)
- Year (1900-2100, 4-digit integer)

**Optional Fields:**
- Genre (dropdown, user-definable)
- Price (non-negative decimal if provided)

**Duplicate Detection:**
- Same `title` + `artist` + `year` = duplicate

### Error Handling
- **Frontend errors**: Red border on field (on-blur validation)
- **Backend errors**: Alert dialog with specific error message
- **Database errors**: RuntimeException with descriptive message

### Testing Strategy
- **Service layer**: JUnit 5 tests with mock repository
- **Repository layer**: Could use H2 in-memory or Testcontainers (not yet implemented)
- **UI**: Manual testing (JavaFX testing not included in MVP)

### Configuration Management
- Database credentials in `.env` file (gitignored)
- HikariCP settings in `DatabaseConfig.java`
- Flyway migrations in `src/backend/src/main/resources/db/migration/`

### Gradle Plugins
- `java`: Core Java compilation
- `application`: Run JavaFX app with `./gradlew :frontend:run`
- `org.openjfx.javafxplugin`: JavaFX dependencies and configuration
- `com.gradleup.shadow`: Create fat JAR with all dependencies

### Dependencies (Key Ones)
**Backend:**
- `org.postgresql:postgresql:42.7.4` - PostgreSQL JDBC driver
- `com.zaxxer:HikariCP:6.2.1` - Connection pooling
- `org.flywaydb:flyway-core:11.1.0` - Database migrations
- `io.github.cdimascio:dotenv-java:3.0.2` - .env file support

**Frontend:**
- `javafx-controls:23.0.1` - JavaFX UI components
- `javafx-fxml:23.0.1` - FXML support
- `project(":backend")` - Backend module dependency

**Testing:**
- `org.junit.jupiter:junit-jupiter:5.11.4` - JUnit 5

## AsciiDoc Documentation System

### Build & Publish Workflow

**Convert AsciiDoc to HTML locally:**
```bash
./local-convert.sh
```
- Requires Docker installed locally
- Uses `asciidoctor/docker-asciidoctor:1.58`
- Input: `asciidocs/`, Output: `dist/`

**Publish to GitHub Pages:**
```bash
./publish.sh
```
- Runs `local-convert.sh`
- Force-pushes `dist/` to `gh-pages` branch

**Automatic CI/CD:**
- Workflow: `.github/workflows/docs.yaml`
- Triggers: Push to `main` with changes in `asciidocs/**`
- Converts `.adoc` → HTML + reveal.js slides
- Deploys to `gh-pages` branch

### Documentation Structure
```
asciidocs/
├── docs/                    # Main documentation
│   ├── index.adoc          # Landing page
│   ├── sysspec.adoc        # System specification (German)
│   ├── product-backlog.adoc # User stories
│   ├── images/             # Documentation images
│   │   └── use-case-diagram-1.png
│   └── themes/             # Custom themes
└── scripts/                 # Conversion scripts (DO NOT TOUCH)
    ├── docker-convert.sh
    └── docker-convert-util.sh
```

**Published URLs:**
- Docs: `https://2526-3bhif-syp.github.io/2526-3bhif-syp-project-vinyl-master/`
- Example: `https://2526-3bhif-syp.github.io/2526-3bhif-syp-project-vinyl-master/sysspec`

## Important Notes

### What to Avoid
- ⚠️ **Do NOT modify** `asciidocs/scripts/` (Docker conversion pipeline)
- ⚠️ **Do NOT commit** `.env` files (gitignored, contains DB credentials)
- ⚠️ **Do NOT hardcode** database credentials (use `.env`)

### Database Setup Requirement
- **Before running the app**, start PostgreSQL: `docker-compose up -d`
- Flyway migrations run automatically on first app startup
- Database: `vinylmaster`, User: `app`, Password: `app`

### Future Enhancements (from sysspec.adoc)
- Barcode scanning for automatic metadata retrieval
- Discogs API integration for market prices
- Physical storage location tracking
- Advanced search and filtering
- Collection statistics dashboard
- Wishlist management
- Export/import functionality

### Context
- **Language**: German documentation, English code
- **Institution**: HTL Leonding, Austria
- **Course**: 3BHIF System Engineering (SYP)
- **Target Users**: Vinyl record collectors
- **Performance Target**: Search <200ms for 5,000 records
- **Offline Support**: Basic search without internet (planned)
