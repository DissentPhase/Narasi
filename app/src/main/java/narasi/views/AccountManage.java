package narasi.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import narasi.models.*;

import java.sql.Timestamp;
import java.util.Optional;

public class AccountManage {

    private final Stage stage;
    private MainView mainView;
    private final ObservableList<Work> works;
    private Work selectedWork;
    private final User currentUser;
    private TextArea contentArea;
    private TextField titleField;
    private Stage primaryStage;
    private final ObservableList<String> selectedTags = FXCollections.observableArrayList();

    public AccountManage(Stage stage, User currentUser, MainView mainView) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.works = FXCollections.observableArrayList(DBManager.getWorksByCurrentUser(currentUser.getId()));
        this.mainView = mainView; 
    }

    public void showManage() {
        stage.setTitle("Manage dan Publish Content");

        BorderPane root = new BorderPane();

        ListView<String> workListView = new ListView<>();
        ObservableList<String> workTitles = FXCollections.observableArrayList(DBManager.getWorkTitlesByCurrentUser(currentUser.getId()));
        workListView.setItems(workTitles);
        workListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = workListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < works.size()) {
                selectedWork = works.get(selectedIndex);
                showWorkDetails(selectedWork);
                System.out.println("Selected work: " + (selectedWork != null ? selectedWork.getTitle() : "None"));
            }
        });

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.getChildren().add(workListView);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> stage.close());

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout? Any unsaved changes will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                mainView.setLoggedIn(false, currentUser);
                stage.close();
                System.out.println("User logged out.");
            }
        });

        leftPane.getChildren().addAll(closeButton, logoutButton);

        GridPane rightPane = new GridPane();
        rightPane.setPadding(new Insets(10));
        rightPane.setVgap(10);
        rightPane.setHgap(10);

        titleField = new TextField();
        titleField.setPromptText("Title");

        contentArea = new TextArea();
        contentArea.setPromptText("Write your content here...");
        contentArea.setPrefRowCount(10);
        contentArea.setPrefColumnCount(60); 

        Button saveDraftButton = new Button("Save Draft");
        saveDraftButton.setOnAction(event -> {
            if (selectedWork != null) {
                selectedWork.setTitle(titleField.getText());
                selectedWork.setContent(contentArea.getText());
                selectedWork.setTags(String.join(", ", selectedTags));
                selectedWork.setDraft(true);
                saveDraft(selectedWork);
                updateWorkListView(workListView, workTitles);
            } else {
                if (!titleField.getText().isEmpty() && !contentArea.getText().isEmpty()) {
                    Work newWork = new Work();
                    newWork.setTitle(titleField.getText());
                    newWork.setContent(contentArea.getText());
                    newWork.setDraft(true);
                    newWork.setUserId(currentUser.getId()); // Set user ID
                    boolean success = DBManager.addWork(newWork);
                    if (success) {
                        System.out.println("New work created and draft saved successfully.");
                        works.add(newWork);
                        workListView.getItems().add(newWork.getTitle() + " (Draft)");
                    } else {
                        System.out.println("Gagal membuat karya baru dan menyimpan draft.");
                    }
                } else {
                    System.out.println("Title and content cannot be empty.");
                }
            }
        });

        Button publishButton = new Button("Publish");
        publishButton.setOnAction(event -> {
            if (selectedWork != null) {
                selectedWork.setTitle(titleField.getText());
                selectedWork.setContent(contentArea.getText());
                selectedWork.setTags(String.join(", ", selectedTags));
                if (selectedTags.isEmpty()) {
                    System.out.println("Tags are mandatory before publishing.");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Publish Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Tambahkan setidaknya satu tag sebelum merilis karya.");
                    alert.showAndWait();
                    return;
                }
                selectedWork.setDraft(false);
                publishWork(selectedWork);
                updateWorkListView(workListView, workTitles);
            } else {
                if (!titleField.getText().isEmpty() && !contentArea.getText().isEmpty() && !selectedTags.isEmpty()) {
                    Work newWork = new Work();
                    newWork.setTitle(titleField.getText());
                    newWork.setContent(contentArea.getText());
                    newWork.setTags(String.join(", ", selectedTags));
                    newWork.setDraft(false);
                    newWork.setUserId(currentUser.getId()); 
                    boolean success = DBManager.publishWork(newWork);
                    if (success) {
                        System.out.println("New work created and published successfully.");
                        works.add(newWork);
                        workListView.getItems().add(newWork.getTitle());
                    } else {
                        System.out.println("Gagal dalam membuat karya dan melakukan perilisan.");
                    }
                } else {
                    System.out.println("Title, content, and tags cannot be empty.");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Publish Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Please ensure title, content, and tags are filled in before publishing.");
                    alert.showAndWait();
                }
            }
        });

        Button addChapterButton = new Button("Add Chapter");
        addChapterButton.setOnAction(event -> {
            if (selectedWork != null) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Chapter");
                dialog.setHeaderText("Create a new chapter");
                dialog.setContentText("Please enter the chapter title:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String chapterTitle = result.get();
                    Chapter newChapter = new Chapter(0, selectedWork.getId(), selectedWork.getChapters().size() + 1, chapterTitle, "Chapter content here...", new Timestamp(System.currentTimeMillis()));
                    if (DBManager.addChapter(newChapter.getWorkId(), newChapter.getChapterNumber(), newChapter.getTitle(), newChapter.getContent())) {
                        selectedWork.getChapters().add(newChapter);
                        System.out.println("Chapter added successfully.");
                    } else {
                        System.out.println("Failed to add chapter.");
                    }
                }
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            if (selectedWork != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to delete this work?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean success = DBManager.deleteWork(selectedWork.getTitle());
                    if (success) {
                        System.out.println("Work deleted successfully.");
                        works.remove(selectedWork);
                        workListView.getItems().remove(selectedWork.getTitle());
                        clearWorkDetails();
                    } else {
                        System.out.println("Failed to delete work.");
                    }
                }
            }
        });

        rightPane.add(new Label("Title:"), 0, 0);
        rightPane.add(titleField, 1, 0);
        Label contentLabel = new Label("Content:");
        contentLabel.setMinHeight(25);
        rightPane.add(contentLabel, 0, 1);
        rightPane.add(contentArea, 1, 1, 2, 1); 
        rightPane.add(saveDraftButton, 0, 2);
        rightPane.add(publishButton, 1, 2);
        rightPane.add(addChapterButton, 0, 3);
        rightPane.add(deleteButton, 1, 3);

        VBox taggingBox = createTaggingButtons();
        rightPane.add(taggingBox, 3, 1);

        root.setLeft(leftPane);
        root.setCenter(rightPane);

        Scene scene = new Scene(root);
        stage.setFullScreen(true);
        scene.getStylesheets().add(getClass().getResource("/AccountStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

private void showWorkDetails(Work work) {
    if (work != null) {
        titleField.setText(work.getTitle());
        contentArea.setText(work.getContent());
        selectedTags.setAll(work.getTags().split(", "));
    }
}

private VBox createTaggingButtons() {
    VBox taggingBox = new VBox(10);

    // Jenis Karya
    Button jenisKaryaButton = new Button("Jenis Karya");
    jenisKaryaButton.setMinWidth(120);
    VBox jenisKaryaSubButtons = new VBox();
    jenisKaryaSubButtons.setPadding(new Insets(7));
    jenisKaryaSubButtons.setSpacing(5);
    Button novelButton = createTagButton("Novel");
    Button cerpenButton = createTagButton("Cerpen");
    Button puisiButton = createTagButton("Puisi");
    jenisKaryaButton.setOnAction(event -> toggleSubButtons(jenisKaryaSubButtons, novelButton, cerpenButton, puisiButton));

    // Genre
    Button genreButton = new Button("Genre");
    genreButton.setMinWidth(120);
    VBox genreSubButtons = new VBox();
    genreSubButtons.setPadding(new Insets(5));
    genreSubButtons.setSpacing(5);
    Button fantasiButton = createTagButton("Fantasi");
    Button romantisButton = createTagButton("Romantis");
    Button misteriButton = createTagButton("Misteri");
    Button thrillerButton = createTagButton("Thriller");
    genreButton.setOnAction(event -> toggleSubButtons(genreSubButtons, fantasiButton, romantisButton, misteriButton, thrillerButton));

    taggingBox.getChildren().addAll(jenisKaryaButton, jenisKaryaSubButtons, genreButton, genreSubButtons);

    return taggingBox;
}

private Button createTagButton(String tag) {
    Button button = new Button(tag);
    button.setMinWidth(120);
    button.setOnAction(event -> {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag);
            button.setStyle("");
        } else {
            selectedTags.add(tag);
            button.setStyle("-fx-background-color: #87CEEB;"); 
        }
        System.out.println("Selected tags: " + selectedTags);
    });
    return button;
}

private void toggleSubButtons(VBox subButtons, Button... buttons) {
    if (subButtons.getChildren().isEmpty()) {
        subButtons.getChildren().addAll(buttons);
    } else {
        subButtons.getChildren().clear();
    }
}

private void updateWorkListView(ListView<String> workListView, ObservableList<String> workTitles) {
    int selectedIndex = workListView.getSelectionModel().getSelectedIndex();
    workTitles.set(selectedIndex, selectedWork.getTitle() + (selectedWork.isDraft() ? " (Draft)" : ""));
}

private void saveDraft(Work work) {
    work.setUserId(currentUser.getId());
    if (DBManager.updateWork(work)) {
        System.out.println("Draft saved successfully.");
    } else {
        System.out.println("Failed to save draft.");
    }
}

private void publishWork(Work work) {
    work.setUserId(currentUser.getId());
    if (DBManager.updateWork(work)) {
        System.out.println("Work published successfully.");
    } else {
        System.out.println("Failed to publish work.");
    }
}
    private void clearWorkDetails() {
        titleField.clear();
        contentArea.clear();
        selectedTags.clear();
    }
}