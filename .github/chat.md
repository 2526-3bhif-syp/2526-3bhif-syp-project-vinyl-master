# Issue #1: Add Vinyl to Collection - Requirements Discussion

## Issue Details
**Title:** add vinyl to collection 
**Status:** Open 
**URL:** https://github.com/2526-3bhif-syp/2526-3bhif-syp-project-vinyl-master/issues/1

---

## Your Requirements

Please describe what you want for this implementation:

As a collector I want to manually add a vinyl record with details such as title, artist, genre and additional metadata so that I can manage my collection

Acceptance Criteria
    A button or option to manually add a new record
    An input form is necessary to complete with title,artist, genre and other fields
    Validation of the meta data
    Vinyl is added and saved in the overview / collection
    (The user can optionally assign a storage location (shelf, box))

### 1. Scope
(What should be included? What's out of scope?)
Since this is the very first issue, we will have to lay the groundwork for this project. It will be a java-fx project, we will use a postgreSQL database as primary db.

The scope is:   1. Create the Java-FX project with gradle. 
                2. Create the db in Posgress.
                3. Add the ability to add vinyls.

### 2. Technology Stack
(What technologies/frameworks should be used?)
Java, Java-FX for the UI, PostgreSQL as the db.

Aditionally, the db has to be dockered.

### 3. Implementation Details
(Any specific requirements, constraints, or preferences?)
Split the App in UI / Backend. Make the division clear.

### 4. Additional Context
(Anything else I should know?)

---

## Clarification Questions

### Project Structure & Architecture
1. **Project location**: Should the JavaFX project be created in the existing `src/` directory, or should we create a separate structure (e.g., `backend/`, `frontend/`)?

In the src/ folder, make 2 new dirs: backend/ and frontend/

2. **Gradle structure**: Single-module Gradle project or multi-module (separate modules for UI and backend)?

Since we are a rather large team, i prefer a multi-module approach to further seperate backend and frontend. 

3. **Backend architecture**: What pattern should the backend follow? (e.g., DAO pattern, Repository pattern, Service layer architecture?)

Service + Repository

For a management system, this is the most reliable stack.

The Repository Layer: Handles the data. 
The Service Layer: Where Management Logic lives.

4. **JavaFX version**: Which JavaFX version should be used? (e.g., JavaFX 17, 19, 21?)

The latest avaliable.

5. **Java version**: Which Java version should the project target? (e.g., Java 17 LTS, Java 21 LTS?)

The latest avaliable.

### Database Details
6. **Database connection**: Should we use a connection pool? If yes, which library? (e.g., HikariCP?)

Yes, use a Pool. Stick with HikariCP, since its the industry-standard.

7. **Database schema**: 
   - What specific fields should the `vinyl` table have beyond title, artist, genre? (e.g., release year, label, country, condition, barcode/EAN, purchase date, purchase price?)
        Stick with the basics for now, we will add more details later.
   - Should storage location be a separate table or just fields in the vinyl table?
        Skip over that part for now, I just want the ability to add a vinyl with the basic attributes.
   - Do we need any initial schema migration tool? (e.g., Flyway, Liquibase?)
        Simplicity is everything, let's stick with Flyway.

8. **Docker setup**: 
   - Should there be a `docker-compose.yml` for easy database startup?
    Yes.
   - Any specific PostgreSQL version preference?
    Latest.
   - Should sample data be included for development?
    No. For now our goal is just to make it work. We will mock-up data at a later point in time.

### UI/UX Details
9. **UI framework within JavaFX**: Plain JavaFX, or should we use any additional UI libraries? (e.g., ControlsFX, MaterialFX?)

Just plain JavaFX.

10. **Form validation**: What specific validations are needed?
    - Required fields: title and artist only, or more?
    - Any format validations? (e.g., year must be 4 digits, genre from predefined list?)
    - How should validation errors be displayed to the user?

When the user presses the "create" button, a new menu should open, where he can input the basic atrributes like titel, band, year, price, ... The form validation must happen in the frontend, any more complex validation (like duplicats) will be handled by the service layer.

11. **Main window structure**: 
    - Should the "add vinyl" form be in a separate window/dialog, or embedded in the main view?
    A new Menu should appear, it cannot be a new Window though.
    - What should the "overview/collection" view look like? (table, cards, list?)
    The overview is a list of all saved vynls in the main/start view.
    It should be stacked over one-another with the titel and a small picture being the main focus. Just use a generic vynal as the standart picture for now.
    
12. **Storage location (optional field)**:
    - Free text input or structured? (e.g., dropdowns for shelf, box?)
    Structured for things like date or times. Free for any Names.
    - If structured, should there be predefined locations or user-defined?
    Structured can only be a calendar for example.

### Data Persistence & Flow
13. **Save behavior**: When should the vinyl be saved?
    - Immediately on form submit?
    - Should there be a confirmation message after successful save?
    - What happens on validation failure or database error?
    
    There should be a save button on the bottom right, Upon pressing, all validation in the service layer will be started, if it fails, a error dialog will pop up, that explains which field is the problem. Basic validation in the frontend should happen right after the cursor leaves the text field for input. If an error happens, the field should be marked red.

14. **Collection view refresh**: After adding a vinyl, should the collection view automatically refresh/update?

Yes, The overview will update every time the view is loaded or a change was made.

### Build & Development Setup
15. **Build configuration**: 
    - Any specific Gradle plugins needed? (e.g., for JavaFX, shadow jar?)
    - Should there be a runnable JAR target?

org.openjfx.javafxplugin - for UI
com.gradleup.shadow - golden standart for deployment.

16. **Configuration management**: How should database connection details be configured? (properties file, environment variables, both?)

env file in the root of the proj.

17. **Testing**: Should unit tests be included in this first issue, or deferred? If included, which testing framework? (JUnit 5?)

Unit tests have to be included, use Junit, latest version.

### Documentation
18. **README updates**: Should the README.adoc be updated with setup/run instructions for the JavaFX application?

Yes, update the Readme.

19. **Code documentation**: Any specific documentation standards? (Javadoc required?)

After every session you will summarize the work that has been done and update the copilot-instructuions file.
---

## Follow-up Questions

### Database Schema Clarification
20. **Vinyl table fields**: You mentioned "basic attributes like titel, band, year, price, ..." - can you confirm the complete list of fields for the initial vinyl table?
    - Suggested based on your input: `id`, `title`, `artist` (band), `genre`, `year`, `price`
    - Any other fields needed? (e.g., `condition`, `format` like LP/EP/Single, `label`, `country`?)
    
the following list should be included: 

id - db
title - text/free
artist - text/free
genre - user-defined/drop-down
year - standard/calender-widget
price - in Euros €

### UI/Form Details
21. **Form fields**: Which fields should be in the "add vinyl" form?
    - Based on Q20, which attributes should be user-fillable in the form?
    - Should all fields be required, or only title and artist?

Required are: titel, artist, year
The db will automatically choose an id.

22. **Generic vinyl image**: Should I include a placeholder image in the project, or just show a colored rectangle/icon initially?

Include a placeholder "default" image in a seperate folder, we can add the feature to let users add theyre own images in the future. 

23. **Main view navigation**: You mentioned "a new menu should appear" for adding vinyl. Can you clarify:
    - Does this mean the main view should switch/navigate to show the add form (replacing the list view temporarily)?
    - Or should it be a modal overlay that appears on top of the list?
    - How does the user get back to the list view after adding/canceling?

A new menu should appear over the List, after adding/cancelling the list should be updated and reloaded and the menu should disappear.

24. **List view details**: For the vinyl list in the main view, what should be displayed for each vinyl item?
    - Title + small picture (confirmed)
    - Should artist/band also be shown?
    - Any other info? (year, genre?)

show artist as well as price. the layout should look something like this:

| img | titel | artist              | price |

### Validation Details
25. **Frontend validation specifics**: What validations should happen in the frontend when cursor leaves the field?
    - Title/Artist: Required (not empty)?
    - Year: Must be a valid 4-digit number?
    - Price: Must be a valid number/decimal?
    - Genre: Free text or selection from predefined list?

All of these are correct, genre should be a dropdown that can be configured in a seperate menu aswell.

26. **Service layer validation**: You mentioned duplicate checking - what constitutes a duplicate?
    - Same title + artist?
    - Same title + artist + year?
    - Something else?

Same title + artist + year

### Project Structure
27. **Package naming**: What should the base package name be? (e.g., `at.htl.leonding.vinylmaster`, `com.vinylmaster`, etc.)

at.htl.leonding.vinylmaster

28. **Multi-module Gradle structure**: Should the modules be:
    - `backend/` and `frontend/` as separate Gradle subprojects?
    - Or a different structure like `vinyl-backend/` and `vinyl-frontend/`?

Just plain backend / frontend

### Docker & Database
29. **Database name and credentials**: What should be the default database name, username, and password in docker-compose and .env?
    - Suggested: db=`vinylmaster`, user=`vinyl`, password=`vinyl` (for development)

db=`vinylmaster`, user=`app`, password=`app`

30. **Docker compose services**: Should docker-compose.yml include:
    - Just PostgreSQL?
    - Or also pgAdmin for database management?

Just PostgreSQL.

---

## Requirements Summary - Final Confirmation

Before I create the implementation plan, let me confirm I understand everything correctly:

### Technical Stack
- **Java**: Latest LTS (Java 21)
- **JavaFX**: Latest version (23+)
- **Build Tool**: Gradle (multi-module: backend + frontend)
- **Database**: PostgreSQL (latest, Dockerized)
- **Connection Pool**: HikariCP
- **Migration Tool**: Flyway
- **Testing**: JUnit 5 (latest)
- **Gradle Plugins**: `org.openjfx.javafxplugin`, `com.gradleup.shadow`

### Project Structure
```
src/
├── backend/           (Gradle submodule)
│   └── at.htl.leonding.vinylmaster
│       ├── repository/
│       └── service/
└── frontend/          (Gradle submodule)
    └── at.htl.leonding.vinylmaster.ui
```

### Database Schema (Initial)
**Table: vinyl**
- `id` (Primary Key, auto-generated)
- `title` (TEXT, required)
- `artist` (TEXT, required)
- `genre` (TEXT, optional, user-defined dropdown)
- `year` (INTEGER, required)
- `price` (DECIMAL, optional, in Euros)

**Docker Setup:**
- Database: `vinylmaster`
- User: `app`
- Password: `app`
- No pgAdmin, just PostgreSQL

### UI/UX Flow
1. **Main View (Collection Overview)**
   - List of vinyls with layout: `| image | title | artist | price |`
   - Button to "Add New Vinyl"
   - Uses placeholder/default image (stored in project)

2. **Add Vinyl Form** (modal overlay on main view)
   - Required fields: title, artist, year
   - Optional fields: genre (dropdown), price
   - Frontend validation: on-blur (field turns red if invalid)
   - Save button (bottom right)
   - Cancel option to return to list
   - After save/cancel: return to updated list view

3. **Validation Rules**
   - Frontend: title/artist not empty, year = 4-digit number, price = valid decimal
   - Backend (Service): duplicate check (title + artist + year)
   - Error handling: dialog with specific field error

### Genre Management
- Genre is a dropdown with user-configurable options
- Can be configured in a separate menu (future work, but structure should support it)

### Configuration
- `.env` file in project root for database connection details

### Documentation
- Update README.adoc with setup/run instructions
- Update `.github/copilot-instructions.md` after session

---

**Is this understanding correct? If yes, I'll proceed to create the implementation plan. If anything needs correction, please let me know.**

---

## Session Notes - 2026-04-12

### What we did in this session
- Reviewed GitHub issue **#2: browse collection** and mapped each acceptance criterion against the current implementation.
- Implemented UI updates for browse flow without removing existing features:
  - Kept add/search/delete behavior.
  - Added a details panel shown when selecting a record.
  - Included visible metadata (title, artist, genre, year, price) in details.
  - Added editable **storage location** and **notes** fields per selected record (session-level state).
  - Added genre display in the list row.
- Switched placeholder image from PNG to the new JPG resource.
- Diagnosed run failure root cause (`:frontend:run --info`): database connection refused because PostgreSQL is not reachable; also found `.env` parsing issue affecting docker-compose.
- Optimized image loading performance by caching pre-scaled placeholder images (list + detail) instead of reloading/decoding repeatedly.

### Clarification Questions for "Add/Change Image per Record"
1. **Persistence scope:** Should the selected image be persisted permanently in the database, or is session-only storage acceptable?
All images HAVE to be persisted.
2. **Storage model (if persistent):** Do you want to store:
   - a filesystem path to the image, or
   - the binary image data (BLOB) in PostgreSQL?
Only store the path to the image, i want the users to be able to change the image themselves by just dropping a new image in the folder and naming it correctly
3. **UI placement:** Where should the button live?
   - in the details panel (`Change Image`),
   - in each list row,
   - or both?
The edit button should live in the side panel only, there also needs to be an option for the user to add an image at the time of creation.
4. **Selection flow:** Should image selection use a native file chooser dialog restricted to image files (`.jpg`, `.jpeg`, `.png`)?
Yes, the selection flow should use a native FileChooser restricted to .jpg, .jpeg, and .png files, 
after which the application will programmatically process the input to ensure the final cover is forced to a 256x256 
resolution and saved in .jpg format for database consistency.
5. **Fallback behavior:** If a record has no custom image, should we always show the placeholder image?
Yes.
6. **Replace behavior:** When changing an existing image, should the previous reference be overwritten immediately after confirmation?
yes, overwrite the original image with the newly added one.
7. **Removal behavior:** Do you also want a `Remove Image` action to revert to placeholder?
Yes, when choosing the change image option or the add image option, a dialog similar to the add vinyl menu should be shown with a drag and drop for the image 
and an option to clear and revert back to the placeholder
8. **Image processing:** Should the app auto-resize/compress selected images on save (recommended), and if yes, what max size should be enforced (e.g., 512x512)?
ref to question 4. the resolution should be forced to 256 by 256
9. **Validation rules:** What is the maximum allowed file size (MB) for uploaded images?
1MB
10. **Data migration:** Is it okay to add a new Flyway migration and extend the `vinyl` model/repository/service now?
Yes.

---

### Context Validation (Image Feature) - 2026-04-12

#### Confirmed and clear
- Images must be persisted.
- Database stores an image path (not BLOB data).
- Change-image action belongs in the side/details panel.
- Add-vinyl flow also needs image support at creation time.
- Use native FileChooser with `.jpg`, `.jpeg`, `.png` filter.
- Convert/normalize saved covers to `256x256` JPG.
- Placeholder is used when no custom image exists.
- Replacing an image should overwrite previous content.
- Include an option to clear/revert to placeholder.
- A Flyway migration is approved.

#### Resolved implementation decisions
1. Path strategy: store relative paths in an app-managed folder.
2. Naming convention: deterministic per-record filename `vinyl-{id}.jpg`.
3. 1MB rule: validate source upload size only.

#### Implementation completed
- Added Flyway migration `V2__add_image_path_to_vinyl.sql` to extend `vinyl` with `image_path`.
- Extended backend model/repository/service:
  - `Vinyl` now includes `imagePath`.
  - `VinylRepositoryImpl` now persists/loads `image_path`.
  - `VinylService` and `VinylServiceImpl` now support `updateVinyl(...)`.
- Implemented frontend image processing service:
  - New `CoverImageService` handles validation, conversion to JPG, center-crop + resize to `256x256`, overwrite, and delete.
  - Persisted files are stored as relative paths in `covers/vinyl-{id}.jpg`.
- Updated **Add Vinyl** form:
  - Added drag & drop image area, file chooser, clear action, preview, and filename display.
  - Added validation for allowed types (`.jpg/.jpeg/.png`) and max source size (`1MB`).
  - Added image save during record creation and path persistence.
- Updated **Details panel**:
  - Added `Change Image` and `Clear Image` actions for the selected record.
  - `Change Image` replaces the existing cover and updates DB path.
  - `Clear Image` removes custom cover and reverts to placeholder.
- Updated list/detail rendering to use custom cover when available, otherwise placeholder.

---

Using Template A

# Issue #3: edit vinyls

## Entry Metadata
- **Date:** 2026-04-12
- **Issue URL:** https://github.com/2526-3bhif-syp/2526-3bhif-syp-project-vinyl-master/issues/3
- **Issue Status:** open (reopened)
- **Session Goal:** Identify missing implementation to satisfy edit-vinyl acceptance criteria and prepare execution context.

## User Request
Check issue #3 and determine what is still missing to mark it as done.

## Acceptance Criteria (from issue/user)
1. Each vinyl record has an edit option (e.g. button or menu).
2. There is an editing-form with current data pre-filled.
3. The user can modify all editable fields.
4. Validation of input.
5. After saving, the edited vinyl is shown again in the collection.

## Clarification Questions
1. **Where should the primary edit action be placed? (list row, details panel, or both)**
   - **Answer:** In the side panel, also move the delete button there.
2. **Should the existing add form be reused in edit mode, or should a separate edit form be created?**
   - **Answer:** Reuse the add form.
3. **On editing title/artist/year, should duplicate checks run against other records (excluding current record id)?**
   - **Answer:** Yes, just like at creation.

## Confirmed Decisions
- Existing image edit support alone does not satisfy issue #3.
- Full metadata edit flow is required for completion.

## Open Questions (Blocking)
- None. Current clarifications are sufficient to start implementation.

---

Using Template B

## Session Notes - 2026-04-12

### Issue Context
- **Issue:** #3 - edit vinyls
- **Scope in this session:** Implement full edit workflow, align image editing UX to edit form only, persist detail fields (storage location/notes), and fix local DB startup problem.

### Changes Implemented
- Implemented full **edit vinyl** flow via side panel:
  - Added `Edit Vinyl` action in details panel.
  - Moved delete action to details panel (`Delete Vinyl`).
  - Reused add form in edit mode with pre-filled values and update-save behavior.
- Added duplicate-safe update checks:
  - Repository method to detect duplicates excluding current ID.
  - Service update flow now applies duplicate validation like create.
- Refined image UX:
  - Removed side-panel image change/clear buttons (image edits now happen in edit form only).
  - Fixed stale-image issue by clearing image cache when form closes.
- Fixed persistence for detail fields:
  - Added migration `V3__add_storage_location_and_notes_to_vinyl.sql`.
  - Extended `Vinyl` model with `storageLocation` and `notes`.
  - Updated repository SQL mapping for both fields.
  - Replaced in-memory notes/storage maps with database-backed persistence in details panel.
- Fixed Docker/PostgreSQL startup mismatch:
  - Root cause: existing data volume is PG15 while compose used `postgres:18`.
  - Updated compose image to `postgres:15` so current volume starts successfully.
- Improved selection/readability UX in collection view:
  - Details panel is hidden before a vinyl is selected.
  - Details panel appears on selection with a subtle slide-in + fade animation.
  - Selection highlight is now subtle (light background + blue outline) instead of strong blue fill.

### Questions and Answers (Session)
1. **Q:** Why does Docker DB startup fail?
   - **A:** Version mismatch between data volume (PG15) and image (PG18).
2. **Q:** Should image buttons remain in side panel?
   - **A:** No, removed as requested since image editing is available in edit form.
3. **Q:** Should notes/storage be per-vinyl and persistent?
   - **A:** Yes, implemented with DB columns and persistence logic.
4. **Q:** Should side panel be hidden before selection and selection styling be more subtle?
   - **A:** Yes, implemented (hidden-by-default panel + subtle outlined selection state).

### Validation / Outcome
- Build/test pipeline succeeds after the above changes.
- Current status: **done** for implemented scope in this session.

### Next Steps
- Re-check issue #3 acceptance criteria against live UI behavior and close if all criteria are satisfied.

---

Using Template A

# Issue #14: Cross-machine image portability and blank startup state

## Entry Metadata
- **Date:** 2026-04-15
- **Issue URL:** https://github.com/2526-3bhif-syp/2526-3bhif-syp-project-vinyl-master/issues/14
- **Issue Status:** open
- **Session Goal:** Ensure the app runs on any machine without crashes caused by missing local image/user data and avoid committing machine-specific image data.

## User Request
Create a Template A entry and clarify what must be fixed so the project runs on any machine with a clean, blank state and without committing local machine image data.

## Acceptance Criteria (from issue/user)
1. Frontend image handling must not depend on machine-specific repository files.
2. Local user image data must be excluded from version control.
3. New contributors can start with a blank project state (no user-specific images).

## Clarification Questions
1. **Should uploaded cover images remain in the repository working directory (`src/frontend/covers`) or be moved to a user-local app data directory?**
   - **Answer:** Move to user-local app data directory so repository stays clean.
2. **Should existing legacy images stored in repository-relative paths still be readable to avoid regressions on old setups?**
   - **Answer:** Yes, keep backward-compatible read behavior.
3. **Should the generic placeholder image (`src/frontend/src/main/resources/images/vinyl-placeholder.jpg`) stay versioned as an application asset?**
   - **Answer:** Yes, keep placeholder versioned; only user-uploaded images stay local/untracked.

## Confirmed Decisions
- Persist user-uploaded covers to `~/.vinylmaster/covers` instead of repository-relative `covers/`.
- Keep compatibility for reading/deleting legacy repository-relative cover paths.
- Ignore repository-local cover folders in `.gitignore` to prevent accidental commits.
- Keep placeholder image as a tracked app resource.

## Open Questions (Blocking)
- None.

---

Using Template B

## Session Notes - 2026-04-15

### Issue Context
- **Issue:** #14 - Cross-machine image portability and startup stability
- **Scope in this session:** Root-cause analysis focused on Git strategy and machine-independent behavior.

### Changes Implemented
- Identified the core Git/process failure: repository assets and runtime user data were not consistently separated.
- Confirmed that placeholder assets must be versioned, while user-generated covers must never be committed.
- Documented a machine-safe strategy:
  - Track app assets in `src/frontend/src/main/resources/**`.
  - Store runtime cover files in user-local app data (`~/.vinylmaster/covers`).
  - Keep repository-local runtime folders ignored.

### Questions and Answers (Session)
1. **Q:** Why did this fail on another machine even though it worked locally?
   - **A:** Local state masked the issue: missing/unsynced resource files and repository-relative runtime image paths depended on one developer's working directory and files.
2. **Q:** What Git strategy prevents this class of bug?
   - **A:** Strictly separate versioned application assets from unversioned runtime/user data and enforce this with path conventions plus `.gitignore`.

### Validation / Outcome
- Root cause was process-level (Git/data-boundary strategy), not a compile-time dependency failure.
- Project now follows a portable model: deterministic tracked resources + per-user local runtime files.

### Next Steps
- Keep issue #14 as the canonical reference for cross-machine asset/runtime-data boundaries.

---

Using Template B

## Session Notes - 2026-04-15

### Issue Context
- **Issue:** #14 - BUG: Images cause problems on other mashines
- **Scope in this session:** Investigate `:frontend:run` startup crash on a colleague machine and identify the concrete root cause.

### Changes Implemented
- Traced the crash to a database schema mismatch during app startup (`VinylRepositoryImpl.findAll` querying `image_path`).
- Confirmed Flyway only applied `V1` on the affected machine, while runtime code expects columns introduced in later migrations.
- Identified repository state problem: `V2__add_image_path_to_vinyl.sql` and `V3__add_storage_location_and_notes_to_vinyl.sql` existed locally but were not tracked in git.
- Defined the fix path: add and push both migration files so all machines run the same schema evolution.

### Questions and Answers (Session)
1. **Q:** Is the SLF4J warning the reason `:frontend:run` fails?
   - **A:** No. It is non-fatal and unrelated to the crash.
2. **Q:** Why does PostgreSQL report `column "image_path" does not exist`?
   - **A:** The teammate database was created from `V1` only, so the `image_path` column from `V2` was never applied.

### Validation / Outcome
- Root cause confirmed as missing tracked Flyway migrations in git, not JavaFX/FXML loading logic.
- Startup failure is resolved by versioning and sharing `V2` and `V3` migrations so Flyway can migrate teammates consistently.

### Next Steps
- Commit and push:
  - `src/backend/src/main/resources/db/migration/V2__add_image_path_to_vinyl.sql`
  - `src/backend/src/main/resources/db/migration/V3__add_storage_location_and_notes_to_vinyl.sql`

---

Using Template A

# Issue #15: Remove Docker and DB from the project

## Entry Metadata
- **Date:** 2026-04-22
- **Issue URL:** https://github.com/2526-3bhif-syp/2526-3bhif-syp-project-vinyl-master/issues/15
- **Issue Status:** closed
- **Session Goal:** Document completion of Docker and PostgreSQL removal, transition to local file-based persistence.

## User Request
Vinyl Master is a local desktop app. Having Docker and a PostgreSQL database is unnecessary overhead. Replace the database with local file-based persistence (JSON serialization) and remove Docker entirely.

## Acceptance Criteria (from issue/user)
1. Remove Docker setup from the project
2. Remove PostgreSQL database dependency
3. Replace with local file-based persistence (e.g., JSON ObjectOutputStream)
4. Ensure no conflicts with the backend architecture after removal

## Clarification Questions
None - scope was clear and all decisions were captured during implementation.

## Confirmed Decisions
- **Persistence strategy:** Jackson-based JSON serialization to local files (`data/vinyls.json`, `data/genres.json`)
- **Data location:** `data/` directory in project root (auto-created on first run)
- **Repository pattern:** File-based `FileVinylRepository` and `FileGenreRepository` with service layer
- **Backwards compatibility:** Not applicable (no production data to migrate)
- **Cover storage:** User home directory `~/.vinylmaster/covers/` (not in repository)

## Open Questions (Blocking)
None - issue closed as completed.

Using Template [A]

# Issue #16: Fixing the project structure

## Entry Metadata
- **Date:** 2026-04-22
- **Issue URL:** https://github.com/2526-3bhif-syp/2526-3bhif-syp-project-vinyl-master/issues/16
- **Issue Status:** open
- **Session Goal:** Consolidate data folders, update repository paths, adjust tests, and update documentation

## User Request
Reorganize the project structure after Docker removal so frontend and backend follow the MVP pattern and share a single root-level data folder. Update code and documentation accordingly.

## Acceptance Criteria (from issue/user)
1. Single root-level data folder used by backend and frontend
2. Repositories updated to use root data path and accept custom data dir for tests
3. Duplicate data folders removed from src/backend and src/frontend
4. Unit tests updated to use temporary directories and pass
5. Documentation (README) updated to reflect new structure

## Clarification Questions
1. None — scope was clarified by the user and decisions were recorded.

## Confirmed Decisions
- Data location: `data/` at project root (auto-created on first run)
- Repositories: `FileVinylRepository` and `FileGenreRepository` now resolve paths relative to application root and accept an optional data-dir constructor for testing
- Tests: Use JUnit `@TempDir` to avoid altering project files during tests
- .gitignore: keep `data/` ignored so local data isn't committed

## Open Questions (Blocking)
- None

Using Template [A]

# Issue #16: Fixing the project structure

## Entry Metadata
- **Date:** 2026-04-22
- **Issue URL:** https://github.com/2526-3bhif-syp/2526-3bhif-syp-project-vinyl-master/issues/16
- **Issue Status:** open
- **Session Goal:** Consolidate data folders, update repository paths, adjust tests, and update documentation

## User Request
Reorganize the project structure after Docker removal so frontend and backend follow the MVP pattern and share a single root-level data folder. Update code and documentation accordingly.

## Acceptance Criteria (from issue/user)
1. Single root-level data folder used by backend and frontend
2. Repositories updated to use root data path and accept custom data dir for tests
3. Duplicate data folders removed from src/backend and src/frontend
4. Unit tests updated to use temporary directories and pass
5. Documentation (README) updated to reflect new structure

## Clarification Questions
1. None — scope was clarified by the user and decisions were recorded.

## Confirmed Decisions
- Data location: `data/` at project root (auto-created on first run)
- Repositories: `FileVinylRepository` and `FileGenreRepository` now resolve paths relative to application root and accept an optional data-dir constructor for testing
- Tests: Use JUnit `@TempDir` to avoid altering project files during tests
- .gitignore: keep `data/` ignored so local data isn't committed

## Open Questions (Blocking)
- None

Using Template [A]

# Issue #16: Refactored Project Structure - Model/View/Controller Pattern

## Entry Metadata
- **Date:** 2026-04-29
- **Issue URL:** https://github.com/2526-3bhif-syp/2526-3bhif-syp-project-vinyl-master/issues/16
- **Issue Status:** open
- **Session Goal:** Restructure project from backend/frontend modules to Model/View/Controller architectural pattern

## User Request
Update the project structure according to Issue #16 by splitting folders from the current backend/frontend split into Controller, Model, and View modules following the MVC pattern. Update all documentation accordingly.

## Acceptance Criteria (from issue/user)
1. Project structure refactored from `src/backend` and `src/frontend` to `src/model`, `src/view`, and `src/controller`
2. Model module contains: entity classes, repositories, services, exceptions, and configuration
3. View module contains: FXML definitions and UI assets (images)
4. Controller module contains: UI controllers, image services, and Main.java entry point
5. All package imports updated to reflect new module structure
6. settings.gradle.kts and build.gradle.kts files updated for three modules
7. Tests continue to pass
8. README.adoc updated with new structure documentation
9. Session documented in chat.md

## Clarification Questions
1. **Target architecture:** Should the project follow MVC with Model/View/Controller split?
   - **Answer:** Yes, confirmed with user. Structure: `src/model/`, `src/view/`, `src/controller/`

## Confirmed Decisions
- **Module structure:** Three modules (model, view, controller) instead of two (backend, frontend)
  - Model: All business logic, data models, repositories, services
  - View: FXML layouts and static image assets
  - Controller: JavaFX controllers, services to orchestrate UI, Main entry point
- **Build configuration:** Updated settings.gradle.kts to include three modules
- **Build files:** Created/updated build.gradle.kts for each module with appropriate dependencies
- **Package structure:** Maintained `at.htl.leonding.vinylmaster.*` naming convention
- **Entry point:** Main.java remains in controller module at `ui/Main.java`
- **Data location:** Maintained `data/` at project root (no changes)
- **Gradle plugins:** Used consistent plugin configuration across modules

## Implementation Summary
1. ✅ Created directory structure for model, view, and controller modules
2. ✅ Copied backend files to model module (model/, repository/, service/, config/)
3. ✅ Copied frontend controllers to controller module (ui/controller/, ui/image/)
4. ✅ Moved FXML and image assets to view module (resources/fxml/, resources/images/)
5. ✅ Created build.gradle.kts for each module with correct dependencies
6. ✅ Updated settings.gradle.kts to reference three modules instead of backend/frontend
7. ✅ Removed old src/backend/ and src/frontend/ directories
8. ✅ Verified build succeeds: `./gradlew clean build` ✓
9. ✅ Verified all tests pass: `./gradlew test` ✓
10. ✅ Updated README.adoc with new project structure and build commands
11. ✅ Documented in chat.md using Template A

## Open Questions (Blocking)
- None