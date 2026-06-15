# Specification: Vinyl Collection Domain

This specification defines the core `Vinyl` domain entity, its properties, validation rules, duplicate prevention rules, and persistence layout in the Vinyl Master application.

## 1. Domain Model: Vinyl

Each vinyl record contains the following metadata:

| Field Name | Type | Required | Description / Constraints |
| :--- | :--- | :---: | :--- |
| `id` | Long | Yes (DB/Repository auto-generated) | Unique identifier for database consistency and operations. |
| `title` | String | Yes | The album title. Must not be null or empty. |
| `artist` | String | Yes | The performer or band name. Must not be null or empty. |
| `genre` | String | No | The musical genre (value from predefined dropdown). |
| `year` | Integer | Yes | Release year. Must be between `1900` and `2100` inclusive. |
| `price` | BigDecimal | No | The price in Euros. If provided, it must be non-negative. |
| `imagePath` | String | No | Path to the custom cover image file (relative path to local user covers folder). |
| `storageLocation` | String | No | Free-text string representing the physical location (e.g. "Shelf A, Box 3"). |
| `notes` | String | No | Free-text notes for user-specific collection details. |
| `favorite` | boolean | Yes (defaults to `false`) | Flag indicating if the record is starred/favorited by the collector. |

---

## 2. Validation Rules (Service Layer)

The system validates vinyl entries before saving them to disk. Validations are executed in the service layer.

- **Title Validation**:
  - Must not be null.
  - Trimmed length must be greater than 0.
- **Artist Validation**:
  - Must not be null.
  - Trimmed length must be greater than 0.
- **Year Validation**:
  - Must not be null.
  - Range: `1900 <= year <= 2100`.
- **Price Validation**:
  - If not null, the value must be `>= 0.00` (non-negative).

---

## 3. Business Rules: Duplicate Prevention

A vinyl record is considered a duplicate if there already exists a record in the collection with the exact same combination of:
- **Title** (case-sensitive)
- **Artist** (case-sensitive)
- **Year**

### Operations
- **Creation**: Before inserting a new record, the system verifies if another record matches the combination. If a match exists, a `DuplicateVinylException` is thrown.
- **Modification**: When updating a record, the system checks for duplicates matching the new combination, excluding the current record's ID (`existsByTitleAndArtistAndYearAndIdNot`).

---

## 4. Storage & Persistence Layout

The collection is persisted in a local JSON file:
- **Filename**: `data/vinyls.json` (located at the application root directory).
- **Format**: Pretty-printed JSON array of Vinyl objects.
- **Auto-creation**: The file and its parent `data/` directory are automatically created on first application start if they do not exist.
- **Example format**:
  ```json
  [
    {
      "id": 1,
      "title": "Abbey Road",
      "artist": "The Beatles",
      "genre": "Rock",
      "year": 1969,
      "price": 29.99,
      "imagePath": "covers/vinyl-1.jpg",
      "storageLocation": "Shelf B",
      "notes": "Original UK pressing.",
      "favorite": true
    }
  ]
  ```
