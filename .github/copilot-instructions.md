# Copilot Instructions - Vinyl Master Project

## Project Snapshot
- **App:** JavaFX desktop app for vinyl collection management.
- **Architecture:** Multi-module Gradle project with clear Backend/Frontend split.
- **Backend pattern:** Service + Repository, local JSON file persistence.
- **Status:** MVP complete (add vinyl flow, validation, duplicate detection, service-layer tests).
- **Domain direction:** storage locations, statistics, search/filter, barcode scan, Discogs integration.

## Core Stack
- Java 21, JavaFX 23
- Gradle multi-module (`src/backend`, `src/frontend`)
- Jackson (JSON serialization)
- Local file-based data storage (`data/` directory)
- JUnit 5

## Build, Test, Run
```bash
# Build
./gradlew build
./gradlew clean build
./gradlew :backend:build
./gradlew :frontend:build

# Test
./gradlew test
./gradlew :backend:test
./gradlew :backend:test --tests "VinylServiceImplTest"

# Run app (no database setup needed)
./gradlew :frontend:run

# Fat JAR
./gradlew :frontend:shadowJar
java -jar src/frontend/build/libs/frontend-1.0.0-all.jar
```

## Architecture and Data Model

### Module layout
```text
src/backend
  - model/ (Vinyl, exceptions)
  - repository/ (FileVinylRepository, FileGenreRepository)
  - service/ (business validation, duplicate checks)

src/frontend
  - ui/Main.java
  - ui/controller/
  - resources/fxml/
  - resources/images/

data/
  - vinyls.json (auto-created on first run)
  - genres.json (auto-created on first run)
```

### Main behavioral conventions
- Required fields: `title`, `artist`, `year`
- Optional fields: `genre`, `price`
- Duplicate rule: same `title + artist + year`
- Frontend validation: on blur with red field borders
- Backend validation: service layer throws explicit validation/duplicate exceptions
- UI list focus: `| image | title | artist | price |`

### Default data storage
- Data location: `data/` directory (auto-created)
- Vinyl records file: `data/vinyls.json` (JSON array)
- Genres file: `data/genres.json` (JSON string array)
- Format: Pretty-printed JSON (human-readable)

## AsciiDoc Docs Pipeline
- Source: `asciidocs/docs/`
- Conversion scripts: `asciidocs/scripts/` (**do not modify**)
- Local convert: `./local-convert.sh`
- Publish: `./publish.sh` (pushes generated output to `gh-pages`)
- CI workflow: `.github/workflows/docs.yaml` (triggered by changes in `asciidocs/**`)

## Git rules (keep it clean)
- Never commit `.env` or hardcoded credentials.
- Runtime/user files do not belong in git.
- App files that are part of the product **do** belong in git.
- If you create a new file that matters for the system, **stage it immediately** (`git add <file>`).
- For image/runtime portability (issue **#14**):
  - Keep static app assets in `src/frontend/src/main/resources/**`
  - Store user cover files in `~/.vinylmaster/covers`
  - Do not use repository-relative paths for user runtime data
  - Keep `.gitignore` in sync with generated/runtime folders

## Context
- Docs language: German
- Code language: English
- Institution: HTL Leonding (3BHIF SYP)
- Target users: vinyl collectors
- Performance goal: search under 200ms at ~5,000 records (planned target)

---

## `chat.md` Entry Templates (Required)
Before every new `chat.md` entry, add:
`Using Template [A/B/C]`

### Template A: Issue Kickoff + Clarifications
```markdown
# Issue #[ISSUE_NUMBER]: [ISSUE_TITLE]

## Entry Metadata
- **Date:** [YYYY-MM-DD]
- **Issue URL:** [https://github.com/.../issues/...]
- **Issue Status:** [open/closed/reopened]
- **Session Goal:** [short summary]

## User Request
[Short quote/paraphrase of what the user wants.]

## Acceptance Criteria (from issue/user)
1. [Criterion 1]
2. [Criterion 2]
3. [Criterion 3]

## Clarification Questions
1. **[Question]**
   - **Answer:** [Pending]
2. **[Question]**
   - **Answer:** [Pending]

## Confirmed Decisions
- [Decision 1]
- [Decision 2]

## Open Questions (Blocking)
- [Question still unresolved]
```

### Template B: Session Progress / Implementation Update
```markdown
## Session Notes - [YYYY-MM-DD]

### Issue Context
- **Issue:** #[ISSUE_NUMBER] - [ISSUE_TITLE]
- **Scope in this session:** [short scope]

### Changes Implemented
- [Change 1]
- [Change 2]
- [Change 3]

### Questions and Answers (Session)
1. **Q:** [Question]
   - **A:** [Answer]
2. **Q:** [Question]
   - **A:** [Answer]

### Validation / Outcome
- [What was verified at a high level]
- [Current status: done / partial / blocked]

### Next Steps
- [Next action 1]
- [Next action 2]
```

### Template C: Context Validation Block
```markdown
### Context Validation - [Feature/Topic]

#### Confirmed and clear
- [Confirmed point 1]
- [Confirmed point 2]

#### Resolved implementation decisions
1. [Decision]
2. [Decision]

#### Still unclear (if any)
1. [Unclear point]
```

### Usage rules
- Always include **Date** and **Issue reference**.
- Keep Q/A explicit and update answers as soon as resolved.
- Use `Pending` when unknown; do not guess.
- Add incremental entries; do not overwrite older decisions.
- If scope changes, add a fresh Template C block before implementation.
