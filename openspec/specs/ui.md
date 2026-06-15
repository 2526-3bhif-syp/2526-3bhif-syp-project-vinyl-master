# Specification: User Interface & Features

This specification defines the presentation layout, interactive controls, styling conventions, and cover image processing within the Vinyl Master application UI.

## 1. Visual Layout & Panels

The application consists of a single primary window, designed with a custom warm-grey and maroon color palette, organized in three main visual sections:

### 1.1 Toolbar (Header Panel)
- **Title Label**: Displays "Vinyl Collection".
- **Search Field**: Real-time filtering text box (filters by title, artist, or genre).
- **Add Vinyl Button**: Opens the modal add/edit vinyl form.
- **View Mode Toggle Button**: A button ("📋 Collection" / "📝 Wantlist") to toggle between displaying owned collection records and wanted records.
- **Favorites Filter Button**: A toggle button ("☆ Favorites Only" / "★ Favorites Only") to filter the collection list.
- **Artist Filter Dropdown**: ComboBox to select a specific artist.
- **Genre Filter Dropdown**: ComboBox to select a specific genre.
- **Sort Dropdown**: ComboBox containing sorting options.

### 1.2 Collection View (Left/Main Panel)
- Displays all records matching the active filters as a scrollable `ListView`.
- Each record row (cell) follows the layout: `| image (50x50) | VBox (Title, Artist, Genre) | VBox (Favorite Toggle button, Price) |`.
- Selecting a record highlights the cell with a subtle outline (`-fx-border-color: #8c525c; -fx-border-width: 2;`).

### 1.3 Record Details Panel (Right Slide-In Panel)
- **Visibility**: Hidden by default. Slides in from the right with a fade-and-translate animation (220ms duration) only when a record is selected.
- **Metadata Fields**: Displays large Title, Artist, Genre, Condition, Year, and Price.
- **Interactive Fields**: Editable text fields for **Storage Location** and **Notes** (auto-persisted to disk immediately on focus loss).
- **Actions**:
  - `Edit Vinyl`: Opens the edit form modal with the selected record's current values.
  - `Delete Vinyl`: Opens a confirmation dialog to delete the record.
  - `Favorite Toggle (Star)`: Instantly toggles the favorite status.
  - `Close (X)`: Deselects the active record and hides the panel.

---

## 2. Add / Edit Form Modal

When adding a new record or editing an existing one, a full-screen semi-transparent overlay (`rgba(0, 0, 0, 0.5)`) is placed on top of the main layout with a centered form modal.

### 2.1 Form Fields
- **Title**: Required text field.
- **Artist**: Required text field.
- **Year**: Required integer text field.
- **Price**: Optional decimal text field.
- **Genre**: Optional dropdown (populated from custom user genres).
- **Condition**: Optional dropdown menu containing Goldmine Grading conditions.
- **Add to Wantlist**: CheckBox specifying if the record is in the wantlist.
- **Cover Image Drop Area**: Visual drag-and-drop region with options to:
  - Select an image via a native file chooser dialog.
  - Drag and drop an image file.
  - Clear the custom image and revert to the default placeholder.

### 2.2 Validation & Immediate Visual Feedback
- **On-Blur Validation**: As soon as focus leaves an input field, frontend validation checks if the format is correct (e.g. year must be a 4-digit number between 1900-2100, price must be a valid non-negative decimal).
- **Error Styling**: If validation fails, the border of the input field is highlighted in red.
- **Service Validation**: Clicking "Save" triggers backend validations (including duplicate checks). If these fail, a styled modal error alert is shown detailing the problem field.
- **Action Buttons**:
  - **Save** (bottom right): Validates, saves, refreshes the list, and closes the form.
  - **Cancel** (bottom left): Closes the form without saving changes.

---

## 3. Cover Image Processing

To guarantee consistent sizing and minimize disk space, the application processes all uploaded custom covers:

- **Source Limitations**:
  - Allowed extensions: `.jpg`, `.jpeg`, `.png`.
  - Max file size: `1MB`.
- **Processing Pipeline**:
  - Loaded programmatically via a JavaFX image service (`CoverImageService`).
  - Cropped to a square from the center of the image.
  - Resized and scaled to exactly `256x256` pixels.
  - Compressed and saved locally as a `.jpg` file in the user's home directory (`~/.vinylmaster/covers/vinyl-{id}.jpg`).
- **Default Image**: If no custom image is assigned, the application uses a default record placeholder asset (`vinyl-placeholder.jpg`) preloaded in the view module.

---

## 4. Sorting & Filtering Rules

All sort and filter actions update the `ListView` instantly without reloading the application window.

### 4.1 Filter Criteria
- **Search Query**: Performs a case-insensitive check on Title, Artist, and Genre.
- **Artist Dropdown**: Shows "All" or filters specifically by selected artist.
- **Genre Dropdown**: Shows "All" or filters specifically by selected genre.
- **Favorites Filter**: Shows all records or filters strictly to `favorite == true`.

### 4.2 Sort Criteria
The user can select from a dropdown to order the records by:
- **Title (A–Z)** / **Title (Z–A)**
- **Artist (A–Z)** / **Artist (Z–A)**
- **Price (Ascending)** / **Price (Descending)**
