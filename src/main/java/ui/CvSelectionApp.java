package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Applicant;
import network.Client;
import service.ResumeParser;

import java.io.File;
import java.io.IOException;

public class CvSelectionApp extends Application {

    private final ResumeParser resumeParser = new ResumeParser();
    private final Client client = new Client();

    private Label selectedFileLabel;
    private TextField jobNumberField;
    private ComboBox<String> strategyComboBox;
    private Button chooseCvButton;
    private TextArea outputArea;

    @Override
    public void start(Stage stage) {
        selectedFileLabel = new Label("No CV selected.");
        jobNumberField = new TextField("1");
        strategyComboBox = new ComboBox<>();
        chooseCvButton = new Button("Choose CV");
        outputArea = new TextArea();

        strategyComboBox.getItems().addAll("basic", "advanced");
        strategyComboBox.setValue("basic");

        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        // Event Listener: Opens the FileChooser, parses the selected CV, and sends it to the server
        chooseCvButton.setOnAction(event -> chooseAndSendCv(stage));

        // layout container
        VBox root = new VBox(
                10,
                new Label("Job number:"),
                jobNumberField,
                new Label("Matching strategy:"),
                strategyComboBox,
                chooseCvButton,
                selectedFileLabel,
                outputArea
        );

        root.setPadding(new Insets(16));

        Scene scene = new Scene(root, 520, 480);
        stage.setTitle("Job Matching Client");
        stage.setScene(scene);
        stage.show();
    }

    private void chooseAndSendCv(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose CV TXT File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT files", "*.txt")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return;
        }

        selectedFileLabel.setText("Selected: " + selectedFile.getAbsolutePath());

        int jobNumber;
        try {
            jobNumber = Integer.parseInt(jobNumberField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Job number must be a valid number.");
            return;
        }

        sendSelectedCv(selectedFile, jobNumber, strategyComboBox.getValue());
    }

    private void sendSelectedCv(File selectedFile, int jobNumber, String strategy) {
        chooseCvButton.setDisable(true);
        showMessage("Reading CV and sending applicant data to server...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws IOException {
                Applicant applicant = resumeParser.parse(selectedFile.toPath());
                return "Applicant: " + applicant.getName() + System.lineSeparator()
                        + client.sendApplicant(applicant, jobNumber, strategy);
            }
        };

        task.setOnSucceeded(event -> {
            showMessage(task.getValue());
            chooseCvButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            Throwable error = task.getException();
            showMessage("Error: " + error.getMessage());
            chooseCvButton.setDisable(false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showMessage(String message) {
        Platform.runLater(() -> outputArea.setText(message));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
