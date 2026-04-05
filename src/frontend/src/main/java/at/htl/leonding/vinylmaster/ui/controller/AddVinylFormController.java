package at.htl.leonding.vinylmaster.ui.controller;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.service.DuplicateVinylException;
import at.htl.leonding.vinylmaster.service.GenreService;
import at.htl.leonding.vinylmaster.service.ValidationException;
import at.htl.leonding.vinylmaster.service.VinylServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class AddVinylFormController {
    @FXML
    private VBox formContainer;
    
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
    
    private final VinylServiceImpl vinylService;
    private final GenreService genreService;
    private MainViewController mainViewController;

    public AddVinylFormController() {
        this.vinylService = new VinylServiceImpl();
        this.genreService = new GenreService();
    }

    @FXML
    public void initialize() {
        loadGenres();
        setupValidation();
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
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
            Vinyl vinyl = new Vinyl();
            vinyl.setTitle(titleField.getText().trim());
            vinyl.setArtist(artistField.getText().trim());
            vinyl.setYear(Integer.parseInt(yearField.getText().trim()));
            
            String genre = genreComboBox.getValue();
            if (genre != null && !genre.trim().isEmpty()) {
                vinyl.setGenre(genre.trim());
                // Save new genre if it doesn't exist
                genreService.addGenre(genre.trim());
            }
            
            String priceText = priceField.getText();
            if (priceText != null && !priceText.trim().isEmpty()) {
                vinyl.setPrice(new BigDecimal(priceText.trim()));
            }
            
            vinylService.addVinyl(vinyl);
            
            if (mainViewController != null) {
                mainViewController.closeAddVinylForm();
            }
        } catch (ValidationException e) {
            showError("Validation Error", e.getMessage());
        } catch (DuplicateVinylException e) {
            showError("Duplicate Vinyl", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to save vinyl: " + e.getMessage());
            e.printStackTrace();
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
}
