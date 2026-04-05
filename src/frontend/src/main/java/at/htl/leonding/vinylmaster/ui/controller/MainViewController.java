package at.htl.leonding.vinylmaster.ui.controller;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.service.VinylServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class MainViewController {
    @FXML
    private ListView<Vinyl> vinylListView;
    
    @FXML
    private Button addVinylButton;
    
    @FXML
    private StackPane rootPane;
    
    @FXML
    private VBox mainContent;
    
    private final VinylServiceImpl vinylService;
    private final ObservableList<Vinyl> vinylList;
    private Pane addVinylFormPane;

    public MainViewController() {
        this.vinylService = new VinylServiceImpl();
        this.vinylList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        setupListView();
        loadVinyls();
        addVinylButton.setOnAction(event -> showAddVinylForm());
    }

    private void setupListView() {
        vinylListView.setItems(vinylList);
        vinylListView.setCellFactory(listView -> new VinylListCell());
    }

    private void loadVinyls() {
        vinylList.clear();
        List<Vinyl> vinyls = vinylService.getAllVinyls();
        vinylList.addAll(vinyls);
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
            e.printStackTrace();
        }
    }

    public void closeAddVinylForm() {
        if (addVinylFormPane != null) {
            rootPane.getChildren().remove(addVinylFormPane);
            addVinylFormPane = null;
        }
        loadVinyls(); // Refresh the list
    }

    private static class VinylListCell extends javafx.scene.control.ListCell<Vinyl> {
        private final HBox content;
        private final ImageView imageView;
        private final Label titleLabel;
        private final Label artistLabel;
        private final Label priceLabel;

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
            
            priceLabel = new Label();
            priceLabel.setStyle("-fx-font-size: 12px;");
            
            VBox textBox = new VBox(5, titleLabel, artistLabel);
            textBox.setPadding(new Insets(0, 10, 0, 10));
            HBox.setHgrow(textBox, Priority.ALWAYS);
            
            content = new HBox(10, imageView, textBox, priceLabel);
            content.setPadding(new Insets(10));
            content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        }

        @Override
        protected void updateItem(Vinyl vinyl, boolean empty) {
            super.updateItem(vinyl, empty);
            if (empty || vinyl == null) {
                setGraphic(null);
            } else {
                try {
                    Image image = new Image(getClass().getResourceAsStream("/images/vinyl-placeholder.png"));
                    imageView.setImage(image);
                } catch (Exception e) {
                    imageView.setImage(null);
                }
                
                titleLabel.setText(vinyl.getTitle());
                artistLabel.setText(vinyl.getArtist());
                priceLabel.setText(vinyl.getPrice() != null ? vinyl.getPrice() + " €" : "");
                
                setGraphic(content);
            }
        }
    }
}
