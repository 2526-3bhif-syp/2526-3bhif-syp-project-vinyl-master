# AI Developer Hand-off Guidelines

Welcome, developer! This guide provides critical context, stack details, environment specifics, and coding conventions to help you work efficiently in the Vinyl Master repository.

---

## 1. Project Overview & MVC Architecture

Vinyl Master is a JavaFX desktop application structured as a multi-module Gradle project:

```text
vinylmaster/
├── src/
│   ├── model/         # Domain models, repositories, and services
│   ├── view/          # UI resource assets (FXML layout definitions, placeholder images)
│   └── controller/    # Application launcher, JavaFX UI Controllers, image scaling logic
```

- **Data Flow Pattern**: Service + Repository.
- **Persistence**: File-based Jackson JSON serializers/deserializers targeting files in the root folder (`data/vinyls.json` and `data/genres.json`).
- **User Image Storage**: Saved as `.jpg` in the user's home folder (`~/.vinylmaster/covers`) to keep the working repository directory clean and portable across machines.

---

## 2. Environment Constraints & Commands

### 2.1 Java 21 Compilation
- **Required Version**: Java 21 is mandated by the Gradle toolchain configuration.
- **Java environment warning**: Default command line setups on the user's system may lack JDK 21 or point to a JRE instead of a JDK. If Gradle fails due to a missing compiler, look for the IntelliJ JetBrains Runtime (JBR) on the machine, which bundles a compatible JDK 21:
  `C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr`

### 2.2 Compilation, Testing & Execution Commands
To execute build actions, explicitly supply the JDK 21 path:

```bash
# Run the test suite
$env:JAVA_HOME="C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr"; ./gradlew test

# Compile and clean build the project
$env:JAVA_HOME="C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr"; ./gradlew clean build

# Start the JavaFX desktop UI
$env:JAVA_HOME="C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr"; ./gradlew :controller:run
```

---

## 3. Code & Design Conventions

- **Duplicate Check rule**: A vinyl duplicate matches when `title + artist + year` are identical. This check is performed inside `VinylServiceImpl`.
- **Validation**: UI fields validate input format on blur (immediate feedback), highlighting invalid fields in red. Service layer validation verifies required fields and throws explicit business logic errors (e.g. `ValidationException`, `DuplicateVinylException`).
- **Git Hygiene**:
  - Never commit runtime or user-created files (like `data/` folder files or custom `~/.vinylmaster` covers). These are ignored by `.gitignore`.
  - Always stage new production files immediately (`git add <file>`).
  - Document each session in `.github/chat.md` using the templates defined in `.github/copilot-instructions.md`.
