package at.htl.leonding.vinylmaster.ui.controller;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.model.VinylSortCriteria;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
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
    private static final String ALL_FILTER_OPTION = "All";

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
    private Button closeDetailsButton;

    @FXML
    private VBox detailsPanel;

    @FXML
    private SplitPane collectionSplitPane;

    @FXML
    private ComboBox<VinylSortCriteria> sortComboBox;

    @FXML
    private ComboBox<String> artistFilterComboBox;

    @FXML
    private ComboBox<String> genreFilterComboBox;

    @FXML
    private Button favoritesFilterButton;

    @FXML
    private Button favoriteDetailButton;

    private final VinylServiceImpl vinylService;
    private final CoverImageService coverImageService;
    private final ObservableList<Vinyl> vinylList;
    private final Image listPlaceholderImage;
    private final Image detailPlaceholderImage;
    private final Map<String, Image> imageCache;
    private boolean showFavoritesOnly = false;
    private Pane addVinylFormPane;
    private javafx.collections.transformation.FilteredList<Vinyl> filteredVinyls;
    private javafx.collections.transformation.SortedList<Vinyl> sortedVinyls;
    private Vinyl selectedVinyl;
    private ParallelTransition detailsPanelAnimation;
    private double detailsPanelDividerPosition = 0.62;

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
        setupFilters();
        setupSort();
        loadVinyls();
        addVinylButton.setOnAction(event -> showVinylForm(null));
    }

    private void setupListView() {
        filteredVinyls = new javafx.collections.transformation.FilteredList<>(vinylList, p -> true);
        sortedVinyls = new javafx.collections.transformation.SortedList<>(filteredVinyls);
        vinylListView.setItems(sortedVinyls);
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
        collectionSplitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
            if (detailsPanel.isVisible() && newVal.doubleValue() < 0.99) {
                detailsPanelDividerPosition = newVal.doubleValue();
            }
        });
        storageLocationField.setDisable(true);
        notesArea.setDisable(true);
        editVinylButton.setDisable(true);
        deleteVinylButton.setDisable(true);
        favoriteDetailButton.setDisable(true);
        closeDetailsButton.setDisable(true);
        storageLocationField.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (!focused) persistSelectedDetails();
        });
        notesArea.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (!focused) persistSelectedDetails();
        });
        editVinylButton.setOnAction(event -> handleEditVinyl());
        deleteVinylButton.setOnAction(event -> handleDeleteVinyl());
        favoriteDetailButton.setOnAction(event -> handleToggleFavorite());
        closeDetailsButton.setOnAction(event -> handleCloseDetailsPanel());
        showVinylDetails(null);
    }

    private void setupSort() {
        styleComboBoxText(sortComboBox);
        sortComboBox.getItems().setAll(VinylSortCriteria.values());
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                sortedVinyls.setComparator(newVal == null ? null : newVal.toComparator()));
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupFilters() {
        styleComboBoxText(artistFilterComboBox);
        artistFilterComboBox.getItems().setAll(ALL_FILTER_OPTION);
        artistFilterComboBox.setValue(ALL_FILTER_OPTION);
        artistFilterComboBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> applyFilters());

        styleComboBoxText(genreFilterComboBox);
        genreFilterComboBox.getItems().setAll(ALL_FILTER_OPTION);
        genreFilterComboBox.setValue(ALL_FILTER_OPTION);
        genreFilterComboBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> applyFilters());

        favoritesFilterButton.setOnAction(event -> {
            showFavoritesOnly = !showFavoritesOnly;
            applyFavoritesButtonStyle();
            applyFilters();
        });
    }

    private void applyFavoritesButtonStyle() {
        if (showFavoritesOnly) {
            favoritesFilterButton.setText("★ Favorites Only");
            favoritesFilterButton.setStyle("-fx-background-color: #8c525c; -fx-text-fill: #e3e6db; -fx-border-color: #8c525c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4 12; -fx-font-size: 13px;");
        } else {
            favoritesFilterButton.setText("☆ Favorites Only");
            favoritesFilterButton.setStyle("-fx-background-color: #e3e6db; -fx-text-fill: #8c525c; -fx-border-color: #8c525c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4 12; -fx-font-size: 13px;");
        }
    }

    private <T> void styleComboBoxText(ComboBox<T> comboBox) {
        comboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setStyle("-fx-text-fill: #8c525c; -fx-background-color: transparent;");
            }
        });

        comboBox.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #e3e6db;");
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: #8c525c; -fx-background-color: #e3e6db;");
                }
            }
        });
    }

    private void loadVinyls() {
        Long previouslySelectedId = selectedVinyl != null ? selectedVinyl.getId() : null;
        vinylList.clear();
        List<Vinyl> vinyls = vinylService.getAllVinyls();
        vinylList.addAll(vinyls);
        refreshFilterOptions();
        applyFilters();

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

    private void refreshFilterOptions() {
        String selectedArtist = artistFilterComboBox.getValue();
        String selectedGenre = genreFilterComboBox.getValue();

        List<String> artists = vinylList.stream()
                .map(Vinyl::getArtist)
                .filter(this::hasText)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<String> genres = vinylList.stream()
                .map(Vinyl::getGenre)
                .filter(this::hasText)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        artistFilterComboBox.getItems().setAll(ALL_FILTER_OPTION);
        artistFilterComboBox.getItems().addAll(artists);
        if (selectedArtist != null && artistFilterComboBox.getItems().contains(selectedArtist)) {
            artistFilterComboBox.setValue(selectedArtist);
        } else {
            artistFilterComboBox.setValue(ALL_FILTER_OPTION);
        }

        genreFilterComboBox.getItems().setAll(ALL_FILTER_OPTION);
        genreFilterComboBox.getItems().addAll(genres);
        if (selectedGenre != null && genreFilterComboBox.getItems().contains(selectedGenre)) {
            genreFilterComboBox.setValue(selectedGenre);
        } else {
            genreFilterComboBox.setValue(ALL_FILTER_OPTION);
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedArtist = artistFilterComboBox.getValue();
        String selectedGenre = genreFilterComboBox.getValue();

        filteredVinyls.setPredicate(vinyl -> {
            if (vinyl == null) {
                return false;
            }
            if (showFavoritesOnly && !vinyl.isFavorite()) {
                return false;
            }
            if (!matchesSelectedFilter(vinyl.getArtist(), selectedArtist)) {
                return false;
            }
            if (!matchesSelectedFilter(vinyl.getGenre(), selectedGenre)) {
                return false;
            }

            if (searchText.isEmpty()) {
                return true;
            }
            return containsIgnoreCase(vinyl.getTitle(), searchText)
                    || containsIgnoreCase(vinyl.getArtist(), searchText)
                    || containsIgnoreCase(vinyl.getGenre(), searchText);
        });
    }

    private boolean matchesSelectedFilter(String value, String selectedFilter) {
        if (selectedFilter == null || ALL_FILTER_OPTION.equals(selectedFilter)) {
            return true;
        }
        return hasText(value) && value.equalsIgnoreCase(selectedFilter);
    }

    private boolean containsIgnoreCase(String value, String searchText) {
        return hasText(value) && value.toLowerCase().contains(searchText);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private Alert createThemedAlert(Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #e3e6db; -fx-border-color: #8c525c; -fx-border-width: 1;");

        for (javafx.scene.Node node : dialogPane.lookupAll(".label")) {
            if (node instanceof Label label) {
                label.setStyle("-fx-text-fill: #8c525c;");
            }
        }

        for (javafx.scene.Node node : dialogPane.lookupAll(".button")) {
            if (node instanceof Button button) {
                button.setStyle("-fx-background-color: #8c525c; -fx-text-fill: #e3e6db; -fx-background-radius: 6;");
            }
        }

        return alert;
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
            Alert err = createThemedAlert(Alert.AlertType.ERROR);
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
            Alert err = createThemedAlert(Alert.AlertType.ERROR);
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
            favoriteDetailButton.setDisable(true);
            favoriteDetailButton.setText("☆");
            favoriteDetailButton.setStyle("-fx-background-color: #e3e6db; -fx-text-fill: #8c525c; -fx-border-color: #8c525c; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 16px; -fx-padding: 4 10;");
            closeDetailsButton.setDisable(true);
            return;
        }

        boolean detailsWereVisible = detailsPanel.isVisible();
        detailsPanel.setVisible(true);
        detailsPanel.setManaged(true);
        if (!detailsWereVisible) {
            collectionSplitPane.setDividerPositions(detailsPanelDividerPosition);
        }
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
        favoriteDetailButton.setDisable(false);
        updateFavoriteDetailButton(vinyl.isFavorite());
        closeDetailsButton.setDisable(false);
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

        Alert alert = createThemedAlert(Alert.AlertType.CONFIRMATION);
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
                Alert err = createThemedAlert(Alert.AlertType.ERROR);
                err.setTitle("Deletion failed");
                err.setHeaderText("Could not delete vinyl");
                err.setContentText(ex.getMessage());
                err.showAndWait();
            }
        }
    }

    private void handleToggleFavorite() {
        if (selectedVinyl == null) {
            return;
        }
        Vinyl updated = vinylService.toggleFavorite(selectedVinyl.getId());
        selectedVinyl.setFavorite(updated.isFavorite());
        updateFavoriteDetailButton(selectedVinyl.isFavorite());
        vinylListView.refresh();
        if (showFavoritesOnly) {
            applyFilters();
        }
    }

    private void updateFavoriteDetailButton(boolean isFavorite) {
        if (isFavorite) {
            favoriteDetailButton.setText("★");
            favoriteDetailButton.setStyle("-fx-background-color: #FFB800; -fx-text-fill: white; -fx-border-color: #FFB800; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 16px; -fx-padding: 4 10;");
        } else {
            favoriteDetailButton.setText("☆");
            favoriteDetailButton.setStyle("-fx-background-color: #e3e6db; -fx-text-fill: #8c525c; -fx-border-color: #8c525c; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 16px; -fx-padding: 4 10;");
        }
    }

    private void handleCloseDetailsPanel() {
        vinylListView.getSelectionModel().clearSelection();
        showVinylDetails(null);
    }

    private class VinylListCell extends javafx.scene.control.ListCell<Vinyl> {
        private final HBox content;
        private final ImageView imageView;
        private final Label titleLabel;
        private final Label artistLabel;
        private final Label genreLabel;
        private final Label priceLabel;
        private final Button cellFavoriteButton;

        public VinylListCell() {
            super();
            imageView = new ImageView();
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            setStyle("-fx-background-color: transparent; -fx-padding: 2 0 2 0;");

            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #8c525c;");

            artistLabel = new Label();
            artistLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8c525c;");

            genreLabel = new Label();
            genreLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8c525c;");

            priceLabel = new Label();
            priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8c525c;");

            cellFavoriteButton = new Button("☆");
            cellFavoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 18px; -fx-padding: 0 4; -fx-text-fill: #8c525c; -fx-cursor: hand;");
            cellFavoriteButton.setOnAction(e -> {
                Vinyl vinyl = getItem();
                if (vinyl != null) {
                    Vinyl updated = vinylService.toggleFavorite(vinyl.getId());
                    vinyl.setFavorite(updated.isFavorite());
                    if (vinyl.equals(selectedVinyl)) {
                        updateFavoriteDetailButton(vinyl.isFavorite());
                    }
                    vinylListView.refresh();
                    if (showFavoritesOnly) {
                        applyFilters();
                    }
                }
            });

            VBox textBox = new VBox(5, titleLabel, artistLabel, genreLabel);
            textBox.setPadding(new Insets(0, 10, 0, 10));
            HBox.setHgrow(textBox, Priority.ALWAYS);

            VBox rightCol = new VBox(2, cellFavoriteButton, priceLabel);
            rightCol.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            content = new HBox(10, imageView, textBox, rightCol);
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

                if (vinyl.isFavorite()) {
                    cellFavoriteButton.setText("★");
                    cellFavoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 18px; -fx-padding: 0 4; -fx-text-fill: #FFB800; -fx-cursor: hand;");
                } else {
                    cellFavoriteButton.setText("☆");
                    cellFavoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 18px; -fx-padding: 0 4; -fx-text-fill: #8c525c; -fx-cursor: hand;");
                }

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
                content.setStyle("-fx-background-color: #e3e6db; -fx-background-radius: 8; "
                        + "-fx-border-color: #8c525c; -fx-border-width: 2; -fx-border-radius: 8;");
            } else {
                content.setStyle("-fx-background-color: #e3e6db; -fx-background-radius: 8; "
                        + "-fx-border-color: #8c525c; -fx-border-width: 1; -fx-border-radius: 8;");
            }
        }
    }
}
