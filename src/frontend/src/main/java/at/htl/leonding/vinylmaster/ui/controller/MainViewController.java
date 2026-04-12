package at.htl.leonding.vinylmaster.ui.controller;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.service.VinylServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final VinylServiceImpl vinylService;
    private final ObservableList<Vinyl> vinylList;
    private final Map<Long, String> storageLocationByVinylId;
    private final Map<Long, String> notesByVinylId;
    private Pane addVinylFormPane;
    private javafx.collections.transformation.FilteredList<Vinyl> filteredVinyls;
    private Vinyl selectedVinyl;

    public MainViewController() {
        this.vinylService = new VinylServiceImpl();
        this.vinylList = FXCollections.observableArrayList();
        this.storageLocationByVinylId = new HashMap<>();
        this.notesByVinylId = new HashMap<>();
    }

    @FXML
    public void initialize() {
        setupListView();
        setupDetailsPane();
        setupSearch();
        loadVinyls();
        addVinylButton.setOnAction(event -> showAddVinylForm());
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
        storageLocationField.setDisable(true);
        notesArea.setDisable(true);
        storageLocationField.textProperty().addListener((obs, oldVal, newVal) -> persistSelectedDetails());
        notesArea.textProperty().addListener((obs, oldVal, newVal) -> persistSelectedDetails());
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

    private void showAddVinylForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-vinyl-form.fxml"));
            addVinylFormPane = loader.load();
            AddVinylFormController controller = loader.getController();
            controller.setMainViewController(this);
            
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
        loadVinyls(); // Refresh the list
    }

    private void persistSelectedDetails() {
        if (selectedVinyl == null || selectedVinyl.getId() == null) {
            return;
        }
        storageLocationByVinylId.put(selectedVinyl.getId(), storageLocationField.getText());
        notesByVinylId.put(selectedVinyl.getId(), notesArea.getText());
    }

    private void showVinylDetails(Vinyl vinyl) {
        selectedVinyl = vinyl;
        detailImageView.setImage(loadPlaceholderImage());

        if (vinyl == null) {
            detailTitleValue.setText("-");
            detailArtistValue.setText("-");
            detailGenreValue.setText("-");
            detailYearValue.setText("-");
            detailPriceValue.setText("-");
            storageLocationField.clear();
            notesArea.clear();
            storageLocationField.setDisable(true);
            notesArea.setDisable(true);
            return;
        }

        detailTitleValue.setText(safeText(vinyl.getTitle()));
        detailArtistValue.setText(safeText(vinyl.getArtist()));
        detailGenreValue.setText(safeText(vinyl.getGenre()));
        detailYearValue.setText(vinyl.getYear() == null ? "-" : vinyl.getYear().toString());
        detailPriceValue.setText(formatPrice(vinyl.getPrice()));

        storageLocationField.setDisable(false);
        notesArea.setDisable(false);

        Long vinylId = vinyl.getId();
        storageLocationField.setText(vinylId == null ? "" : storageLocationByVinylId.getOrDefault(vinylId, ""));
        notesArea.setText(vinylId == null ? "" : notesByVinylId.getOrDefault(vinylId, ""));
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String formatPrice(BigDecimal price) {
        return price == null ? "-" : price + " €";
    }

    private Image loadPlaceholderImage() {
        try (InputStream is = getClass().getResourceAsStream("/images/vinyl-placeholder.png")) {
            return is == null ? null : new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    private class VinylListCell extends javafx.scene.control.ListCell<Vinyl> {
        private final HBox content;
        private final ImageView imageView;
        private final Label titleLabel;
        private final Label artistLabel;
        private final Label genreLabel;
        private final Label priceLabel;
        private final Button deleteButton;

        public VinylListCell() {
            super();
            imageView = new ImageView();
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            
            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            artistLabel = new Label();
            artistLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

            genreLabel = new Label();
            genreLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            
            priceLabel = new Label();
            priceLabel.setStyle("-fx-font-size: 12px;");
            
            deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px;");
            deleteButton.setMinWidth(70);

            VBox textBox = new VBox(5, titleLabel, artistLabel, genreLabel);
            textBox.setPadding(new Insets(0, 10, 0, 10));
            HBox.setHgrow(textBox, Priority.ALWAYS);
            
            content = new HBox(10, imageView, textBox, priceLabel, deleteButton);
            content.setPadding(new Insets(10));
            content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        }

        @Override
        protected void updateItem(Vinyl vinyl, boolean empty) {
            super.updateItem(vinyl, empty);
            if (empty || vinyl == null) {
                setGraphic(null);
            } else {
                imageView.setImage(loadPlaceholderImage());
                
                titleLabel.setText(vinyl.getTitle());
                artistLabel.setText(vinyl.getArtist());
                genreLabel.setText("Genre: " + safeText(vinyl.getGenre()));
                priceLabel.setText(vinyl.getPrice() != null ? vinyl.getPrice() + " €" : "");
                
                // configure delete action for this cell's vinyl
                deleteButton.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Vinyl");
                    alert.setHeaderText("Confirm deletion");
                    alert.setContentText(String.format("Are you sure you want to delete '%s' by %s?",
                            vinyl.getTitle(), vinyl.getArtist()));

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            // call service to delete and refresh list
                            // Using outer class's vinylService and loadVinyls()
                            MainViewController.this.vinylService.deleteVinyl(vinyl.getId());
                            MainViewController.this.loadVinyls();
                        } catch (Exception ex) {
                            Alert err = new Alert(Alert.AlertType.ERROR);
                            err.setTitle("Deletion failed");
                            err.setHeaderText("Could not delete vinyl");
                            err.setContentText(ex.getMessage());
                            err.showAndWait();
                        }
                    }
                });

                setGraphic(content);
            }
        }
    }
}
