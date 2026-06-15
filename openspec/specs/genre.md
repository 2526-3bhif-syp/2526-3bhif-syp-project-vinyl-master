# Specification: Genre Management

This specification defines the representation, validation, and storage structure for the genres dropdown list within the Vinyl Master application.

## 1. Domain Model: Genre

Unlike vinyl records, genres are not complex database models. They are simple unique string values used to classify records.

- **Representation**: `String`
- **Validation**:
  - Must not be null or empty.
  - Must be unique (case-insensitive deduplication).
- **Default / Preconfigured list**: The system provides a set of standard genres if no user genres are configured, but allows the user to define their own custom genres.

---

## 2. Storage & Persistence Layout

Genres are persisted in a local JSON file separate from the vinyl records:
- **Filename**: `data/genres.json` (located at the application root directory).
- **Format**: Pretty-printed JSON array of strings.
- **Auto-creation**: The file and parent `data/` directory are automatically created on first application start if they do not exist.
- **Example format**:
  ```json
  [
    "Rock",
    "Jazz",
    "Classical",
    "Pop",
    "Hip-hop",
    "Electronic"
  ]
  ```
