package at.htl.leonding.vinylmaster.ui.controller;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.service.VinylServiceImpl;
import at.htl.leonding.vinylmaster.ui.image.CoverImageService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Duration;

public class MainViewController {
    @FXML
    private ListView<Vinyl> vinylListView;
    
    @FXML
    private Button addVinylButton;
    
    @FXML
    private StackPane rootPane;
    
    @FXML
    @SuppressWarnings("unused") // referenced from FXML
    private VBox mainContent;
    
    @FXML
    private javafx.scene.control.TextField searchField;

    @FXML
    private ImageView detailImageView;

    @FXML
    private Label detailTitleValue;

    @FXML
    private Label detailArtistValue;

    @FXML
    private Label detailGenreValue;

    @FXML
    private Label detailYearValue;

    @FXML
    private Label detailPriceValue;

    @FXML
    private TextField storageLocationField;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button editVinylButton;

    @FXML
    private Button deleteVinylButton;

    @FXML
    private VBox detailsPanel;

    @FXML
    private SplitPane collectionSplitPane;

    private final VinylServiceImpl vinylService;
    private final CoverImageService coverImageService;
    private final ObservableList<Vinyl> vinylList;
    private final Image listPlaceholderImage;
    private final Image detailPlaceholderImage;
    private final Map<String, Image> imageCache;
    private Pane addVinylFormPane;
    private javafx.collections.transformation.FilteredList<Vinyl> filteredVinyls;
    private Vinyl selectedVinyl;
    private ParallelTransition detailsPanelAnimation;

    public MainViewController() {
        this.vinylService = new VinylServiceImpl();
        this.coverImageService = new CoverImageService();
        this.vinylList = FXCollections.observableArrayList();
        this.listPlaceholderImage = loadPlaceholderImage(50, 50);
        this.detailPlaceholderImage = loadPlaceholderImage(64, 64);
        this.imageCache = new HashMap<>();
    }

    @FXML
    public void initialize() {
        setupListView();
        setupDetailsPane();
        setupSearch();
        loadVinyls();
        addVinylButton.setOnAction(event -> showVinylForm(null));
    }

    private void setupListView() {
        // wrap the observable list with a FilteredList so we can filter by search
        filteredVinyls = new javafx.collections.transformation.FilteredList<>(vinylList, p -> true);
        vinylListView.setItems(filteredVinyls);
        vinylListView.setCellFactory(listView -> new VinylListCell());
        vinylListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null && oldVal != newVal) {
                persistSelectedDetails();
            }
            showVinylDetails(newVal);
        });
    }

    private void setupDetailsPane() {
        detailsPanel.setVisible(false);
        detailsPanel.setManaged(false);
        storageLocationField.setDisable(true);
        notesArea.setDisable(true);
        editVinylButton.setDisable(true);
        deleteVinylButton.setDisable(true);
        storageLocationField.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (!focused) persistSelectedDetails();
        });
        notesArea.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (!focused) persistSelectedDetails();
        });
        editVinylButton.setOnAction(event -> handleEditVinyl());
        deleteVinylButton.setOnAction(event -> handleDeleteVinyl());
        showVinylDetails(null);
    }

    private void setupSearch() {
        // case-insensitive substring match on title, artist, or genre
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.trim().toLowerCase();
            if (filter.isEmpty()) {
                filteredVinyls.setPredicate(v -> true);
            } else {
                filteredVinyls.setPredicate(v -> {
                    if (v == null) return false;
                    if (v.getTitle() != null && v.getTitle().toLowerCase().contains(filter)) return true;
                    if (v.getArtist() != null && v.getArtist().toLowerCase().contains(filter)) return true;
                    if (v.getGenre() != null && v.getGenre().toLowerCase().contains(filter)) return true;
                    return false;
                });
            }
        });
    }

    private void loadVinyls() {
        Long previouslySelectedId = selectedVinyl != null ? selectedVinyl.getId() : null;
        vinylList.clear();
        List<Vinyl> vinyls = vinylService.getAllVinyls();
        vinylList.addAll(vinyls);

        if (previouslySelectedId == null) {
            showVinylDetails(null);
            return;
        }

        Vinyl matchingVinyl = vinylList.stream()
                .filter(v -> previouslySelectedId.equals(v.getId()))
                .findFirst()
                .orElse(null);

        if (matchingVinyl != null) {
            vinylListView.getSelectionModel().select(matchingVinyl);
        } else {
            showVinylDetails(null);
        }
    }

    private void showVinylForm(Vinyl vinylToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-vinyl-form.fxml"));
            addVinylFormPane = loader.load();
            AddVinylFormController controller = loader.getController();
            controller.setMainViewController(this);
            if (vinylToEdit != null) {
                controller.setEditVinyl(vinylToEdit);
            }
            
            // Add semi-transparent overlay
            addVinylFormPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            rootPane.getChildren().add(addVinylFormPane);
        } catch (IOException e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Error");
            err.setHeaderText("Could not open Add Vinyl form");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }

    public void closeAddVinylForm() {
        if (addVinylFormPane != null) {
            rootPane.getChildren().remove(addVinylFormPane);
            addVinylFormPane = null;
        }
        imageCache.clear();
        loadVinyls(); // Refresh the list
    }

    private void persistSelectedDetails() {
        if (selectedVinyl == null || selectedVinyl.getId() == null) {
            return;
        }
        String newStorageLocation = storageLocationField.getText();
        String newNotes = notesArea.getText();

        boolean storageChanged = !safeNullableEquals(selectedVinyl.getStorageLocation(), newStorageLocation);
        boolean notesChanged = !safeNullableEquals(selectedVinyl.getNotes(), newNotes);
        if (!storageChanged && !notesChanged) {
            return;
        }

        selectedVinyl.setStorageLocation(newStorageLocation);
        selectedVinyl.setNotes(newNotes);
        try {
            vinylService.updateVinyl(selectedVinyl);
        } catch (Exception ex) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Save failed");
            err.setHeaderText("Could not persist detail fields");
            err.setContentText(ex.getMessage());
            err.showAndWait();
        }
    }

    private void showVinylDetails(Vinyl vinyl) {
        selectedVinyl = vinyl;

        if (vinyl == null) {
            if (detailsPanelAnimation != null) {
                detailsPanelAnimation.stop();
            }
            detailsPanel.setVisible(false);
            detailsPanel.setManaged(false);
            collectionSplitPane.setDividerPositions(1.0);
            detailImageView.setImage(detailPlaceholderImage);
            detailTitleValue.setText("-");
            detailArtistValue.setText("-");
            detailGenreValue.setText("-");
            detailYearValue.setText("-");
            detailPriceValue.setText("-");
            storageLocationField.clear();
            notesArea.clear();
            storageLocationField.setDisable(true);
            notesArea.setDisable(true);
            editVinylButton.setDisable(true);
            deleteVinylButton.setDisable(true);
            return;
        }

        detailsPanel.setVisible(true);
        detailsPanel.setManaged(true);
        collectionSplitPane.setDividerPositions(0.62);
        detailImageView.setImage(loadCoverImage(vinyl, 64, 64));
        detailTitleValue.setText(safeText(vinyl.getTitle()));
        detailArtistValue.setText(safeText(vinyl.getArtist()));
        detailGenreValue.setText(safeText(vinyl.getGenre()));
        detailYearValue.setText(vinyl.getYear() == null ? "-" : vinyl.getYear().toString());
        detailPriceValue.setText(formatPrice(vinyl.getPrice()));

        storageLocationField.setDisable(false);
        notesArea.setDisable(false);

        storageLocationField.setText(vinyl.getStorageLocation() == null ? "" : vinyl.getStorageLocation());
        notesArea.setText(vinyl.getNotes() == null ? "" : vinyl.getNotes());
        editVinylButton.setDisable(false);
        deleteVinylButton.setDisable(false);
        playDetailsPanelSlideInAnimation();
    }

    private void playDetailsPanelSlideInAnimation() {
        if (detailsPanel == null) {
            return;
        }

        if (detailsPanelAnimation != null) {
            detailsPanelAnimation.stop();
        }

        detailsPanel.setTranslateX(28);
        detailsPanel.setOpacity(0.55);

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), detailsPanel);
        slide.setFromX(28);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.millis(220), detailsPanel);
        fade.setFromValue(0.55);
        fade.setToValue(1.0);

        detailsPanelAnimation = new ParallelTransition(slide, fade);
        detailsPanelAnimation.play();
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String formatPrice(BigDecimal price) {
        return price == null ? "-" : price + " €";
    }

    private boolean safeNullableEquals(String left, String right) {
        String normalizedLeft = left == null ? "" : left;
        String normalizedRight = right == null ? "" : right;
        return normalizedLeft.equals(normalizedRight);
    }

    private Image loadPlaceholderImage(double width, double height) {
        URL resourceUrl = getClass().getResource("/images/vinyl-placeholder.jpg");
        if (resourceUrl == null) {
            return null;
        }
        return new Image(resourceUrl.toExternalForm(), width, height, true, true, false);
    }

    private Image loadCoverImage(Vinyl vinyl, double width, double height) {
        if (vinyl == null || vinyl.getImagePath() == null || vinyl.getImagePath().isBlank()) {
            return width <= 50 ? listPlaceholderImage : detailPlaceholderImage;
        }

        String cacheKey = vinyl.getImagePath() + "|" + (int) width + "x" + (int) height;
        Image cached = imageCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Path absolutePath = coverImageService.getAbsolutePath(vinyl.getImagePath());
        if (!Files.exists(absolutePath)) {
            return width <= 50 ? listPlaceholderImage : detailPlaceholderImage;
        }

        Image image = new Image(absolutePath.toUri().toString(), width, height, true, true, false);
        if (!image.isError()) {
            imageCache.put(cacheKey, image);
            return image;
        }
        return width <= 50 ? listPlaceholderImage : detailPlaceholderImage;
    }

    private void clearCacheForPath(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        imageCache.keySet().removeIf(key -> key.startsWith(relativePath + "|"));
    }

    private void handleEditVinyl() {
        if (selectedVinyl == null) {
            return;
        }
        showVinylForm(selectedVinyl);
    }

    private void handleDeleteVinyl() {
        if (selectedVinyl == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Vinyl");
        alert.setHeaderText("Confirm deletion");
        alert.setContentText(String.format("Are you sure you want to delete '%s' by %s?",
                selectedVinyl.getTitle(), selectedVinyl.getArtist()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (selectedVinyl.getImagePath() != null && !selectedVinyl.getImagePath().isBlank()) {
                    coverImageService.deleteCover(selectedVinyl.getImagePath());
                    clearCacheForPath(selectedVinyl.getImagePath());
                }
                vinylService.deleteVinyl(selectedVinyl.getId());
                loadVinyls();
            } catch (Exception ex) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Deletion failed");
                err.setHeaderText("Could not delete vinyl");
                err.setContentText(ex.getMessage());
                err.showAndWait();
            }
        }
    }

    private class VinylListCell extends javafx.scene.control.ListCell<Vinyl> {
        private final HBox content;
        private final ImageView imageView;
        private final Label titleLabel;
        private final Label artistLabel;
        private final Label genreLabel;
        private final Label priceLabel;

        public VinylListCell() {
            super();
            imageView = new ImageView();
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            setStyle("-fx-background-color: transparent; -fx-padding: 2 0 2 0;");
            
            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1f2937;");
            
            artistLabel = new Label();
            artistLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

            genreLabel = new Label();
            genreLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            
            priceLabel = new Label();
            priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1f2937;");
            
            VBox textBox = new VBox(5, titleLabel, artistLabel, genreLabel);
            textBox.setPadding(new Insets(0, 10, 0, 10));
            HBox.setHgrow(textBox, Priority.ALWAYS);
            
            content = new HBox(10, imageView, textBox, priceLabel);
            content.setPadding(new Insets(10));
            content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            applySelectionStyle(false);
        }

        @Override
        protected void updateItem(Vinyl vinyl, boolean empty) {
            super.updateItem(vinyl, empty);
            if (empty || vinyl == null) {
                setGraphic(null);
            } else {
                imageView.setImage(loadCoverImage(vinyl, 50, 50));
                
                titleLabel.setText(vinyl.getTitle());
                artistLabel.setText(vinyl.getArtist());
                genreLabel.setText("Genre: " + safeText(vinyl.getGenre()));
                priceLabel.setText(vinyl.getPrice() != null ? vinyl.getPrice() + " €" : "");
                applySelectionStyle(isSelected());

                setGraphic(content);
            }
        }

        @Override
        public void updateSelected(boolean selected) {
            super.updateSelected(selected);
            applySelectionStyle(selected);
        }

        private void applySelectionStyle(boolean selected) {
            if (selected) {
                content.setStyle("-fx-background-color: #f6f9ff; -fx-background-radius: 8; "
                        + "-fx-border-color: #5b8def; -fx-border-width: 1.2; -fx-border-radius: 8;");
            } else {
                content.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; "
                        + "-fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8;");
            }
        }
    }
}
