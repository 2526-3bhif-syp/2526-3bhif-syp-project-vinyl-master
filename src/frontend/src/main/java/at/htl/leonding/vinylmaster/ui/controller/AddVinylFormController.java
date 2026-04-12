package at.htl.leonding.vinylmaster.ui.controller;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.service.DuplicateVinylException;
import at.htl.leonding.vinylmaster.service.GenreService;
import at.htl.leonding.vinylmaster.service.ValidationException;
import at.htl.leonding.vinylmaster.service.VinylServiceImpl;
import at.htl.leonding.vinylmaster.ui.image.CoverImageService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

public class AddVinylFormController {
    @FXML
    private VBox formContainer;

    @FXML
    private Label formTitleLabel;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextField artistField;
    
    @FXML
    private ComboBox<String> genreComboBox;
    
    @FXML
    private TextField yearField;
    
    @FXML
    private TextField priceField;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;

    @FXML
    private VBox imageDropZone;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button clearImageButton;

    @FXML
    private Label imageFileLabel;

    @FXML
    private ImageView imagePreview;
    
    private final VinylServiceImpl vinylService;
    private final GenreService genreService;
    private final CoverImageService coverImageService;
    private MainViewController mainViewController;
    private File selectedImageFile;
    private Vinyl editingVinyl;
    private boolean editMode;
    private boolean imageClearedInEditMode;

    public AddVinylFormController() {
        this.vinylService = new VinylServiceImpl();
        this.genreService = new GenreService();
        this.coverImageService = new CoverImageService();
    }

    @FXML
    public void initialize() {
        loadGenres();
        setupValidation();
        setupImageSelection();
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
        chooseImageButton.setOnAction(event -> chooseImageFile());
        clearImageButton.setOnAction(event -> clearSelectedImage());
    }

    private void loadGenres() {
        List<String> genres = genreService.getAllGenres();
        genreComboBox.getItems().clear();
        genreComboBox.getItems().addAll(genres);
        genreComboBox.setEditable(true);
    }

    private void setupValidation() {
        // Title validation
        titleField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Lost focus
                validateTitle();
            }
        });
        
        // Artist validation
        artistField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validateArtist();
            }
        });
        
        // Year validation
        yearField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validateYear();
            }
        });
        
        // Price validation
        priceField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validatePrice();
            }
        });
    }

    private void setupImageSelection() {
        imageDropZone.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        imageDropZone.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()) {
                success = handleSelectedImageFile(dragboard.getFiles().getFirst());
            }
            event.setDropCompleted(success);
            event.consume();
        });
        updateImagePreview();
    }

    private void chooseImageFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Cover Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
        );
        Window window = formContainer.getScene() != null ? formContainer.getScene().getWindow() : null;
        File selectedFile = chooser.showOpenDialog(window);
        if (selectedFile != null) {
            handleSelectedImageFile(selectedFile);
        }
    }

    private boolean handleSelectedImageFile(File file) {
        if (file == null) {
            return false;
        }
        String filename = file.getName().toLowerCase();
        if (!(filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))) {
            showError("Invalid Image", "Only .jpg, .jpeg, and .png files are allowed.");
            return false;
        }
        try {
            coverImageService.validateImageFile(file);
            selectedImageFile = file;
            imageClearedInEditMode = false;
            updateImagePreview();
            return true;
        } catch (IOException e) {
            showError("Invalid Image", e.getMessage());
            return false;
        }
    }

    private void clearSelectedImage() {
        selectedImageFile = null;
        imageClearedInEditMode = editMode;
        updateImagePreview();
    }

    private void updateImagePreview() {
        if (selectedImageFile != null) {
            imageFileLabel.setText(selectedImageFile.getName());
            Image image = new Image(selectedImageFile.toURI().toString(), 64, 64, true, true, false);
            imagePreview.setImage(image);
            return;
        }

        if (editMode && editingVinyl != null && editingVinyl.getImagePath() != null
                && !editingVinyl.getImagePath().isBlank() && !imageClearedInEditMode) {
            Path imagePath = coverImageService.getAbsolutePath(editingVinyl.getImagePath());
            if (imagePath.toFile().exists()) {
                imageFileLabel.setText(imagePath.getFileName().toString());
                imagePreview.setImage(new Image(imagePath.toUri().toString(), 64, 64, true, true, false));
                return;
            }
        }

        imageFileLabel.setText("No image selected");
        imagePreview.setImage(null);
    }

    private boolean validateTitle() {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            setFieldError(titleField, true);
            return false;
        }
        setFieldError(titleField, false);
        return true;
    }

    private boolean validateArtist() {
        if (artistField.getText() == null || artistField.getText().trim().isEmpty()) {
            setFieldError(artistField, true);
            return false;
        }
        setFieldError(artistField, false);
        return true;
    }

    private boolean validateYear() {
        try {
            String yearText = yearField.getText();
            if (yearText == null || yearText.trim().isEmpty()) {
                setFieldError(yearField, true);
                return false;
            }
            int year = Integer.parseInt(yearText);
            if (year < 1900 || year > 2100) {
                setFieldError(yearField, true);
                return false;
            }
            setFieldError(yearField, false);
            return true;
        } catch (NumberFormatException e) {
            setFieldError(yearField, true);
            return false;
        }
    }

    private boolean validatePrice() {
        String priceText = priceField.getText();
        if (priceText == null || priceText.trim().isEmpty()) {
            setFieldError(priceField, false);
            return true; // Price is optional
        }
        try {
            BigDecimal price = new BigDecimal(priceText);
            if (price.signum() < 0) {
                setFieldError(priceField, true);
                return false;
            }
            setFieldError(priceField, false);
            return true;
        } catch (NumberFormatException e) {
            setFieldError(priceField, true);
            return false;
        }
    }

    private void setFieldError(TextField field, boolean hasError) {
        if (hasError) {
            field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            field.setStyle("");
        }
    }

    private void handleSave() {
        // Validate all fields
        boolean titleValid = validateTitle();
        boolean artistValid = validateArtist();
        boolean yearValid = validateYear();
        boolean priceValid = validatePrice();
        
        if (!titleValid || !artistValid || !yearValid || !priceValid) {
            showError("Validation Error", "Please fix the highlighted fields");
            return;
        }
        
        try {
            Vinyl vinyl = editMode ? editingVinyl : new Vinyl();
            vinyl.setTitle(titleField.getText().trim());
            vinyl.setArtist(artistField.getText().trim());
            vinyl.setYear(Integer.parseInt(yearField.getText().trim()));
            
            String genre = genreComboBox.getValue();
            if (genre != null && !genre.trim().isEmpty()) {
                vinyl.setGenre(genre.trim());
                // Save new genre if it doesn't exist
                genreService.addGenre(genre.trim());
            } else {
                vinyl.setGenre(null);
            }
            
            String priceText = priceField.getText();
            if (priceText != null && !priceText.trim().isEmpty()) {
                vinyl.setPrice(new BigDecimal(priceText.trim()));
            } else {
                vinyl.setPrice(null);
            }

            Vinyl savedVinyl;
            if (editMode) {
                savedVinyl = vinylService.updateVinyl(vinyl);
            } else {
                savedVinyl = vinylService.addVinyl(vinyl);
            }

            try {
                applyImageChanges(savedVinyl);
            } catch (IOException e) {
                if (!editMode) {
                    vinylService.deleteVinyl(savedVinyl.getId());
                }
                throw new RuntimeException("Failed to process selected image: " + e.getMessage(), e);
            }
            
            if (mainViewController != null) {
                mainViewController.closeAddVinylForm();
            }
        } catch (ValidationException e) {
            showError("Validation Error", e.getMessage());
        } catch (DuplicateVinylException e) {
            showError("Duplicate Vinyl", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to save vinyl: " + e.getMessage());
        }
    }

    private void handleCancel() {
        if (mainViewController != null) {
            mainViewController.closeAddVinylForm();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void setEditVinyl(Vinyl vinyl) {
        if (vinyl == null) {
            return;
        }

        this.editMode = true;
        this.editingVinyl = vinyl;
        this.imageClearedInEditMode = false;
        this.selectedImageFile = null;

        formTitleLabel.setText("Edit Vinyl");
        saveButton.setText("Update");

        titleField.setText(vinyl.getTitle());
        artistField.setText(vinyl.getArtist());
        genreComboBox.setValue(vinyl.getGenre());
        yearField.setText(vinyl.getYear() == null ? "" : vinyl.getYear().toString());
        priceField.setText(vinyl.getPrice() == null ? "" : vinyl.getPrice().toString());
        updateImagePreview();
    }

    private void applyImageChanges(Vinyl vinyl) throws IOException, ValidationException, DuplicateVinylException {
        if (selectedImageFile != null) {
            String relativeCoverPath = coverImageService.saveCover(selectedImageFile, vinyl.getId());
            vinyl.setImagePath(relativeCoverPath);
            vinylService.updateVinyl(vinyl);
            return;
        }

        if (editMode && imageClearedInEditMode && vinyl.getImagePath() != null && !vinyl.getImagePath().isBlank()) {
            String oldImagePath = vinyl.getImagePath();
            coverImageService.deleteCover(oldImagePath);
            vinyl.setImagePath(null);
            vinylService.updateVinyl(vinyl);
        }
    }
}
