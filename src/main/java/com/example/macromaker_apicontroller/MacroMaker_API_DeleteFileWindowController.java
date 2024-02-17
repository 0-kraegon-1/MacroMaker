package com.example.macromaker_apicontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MacroMaker_API_DeleteFileWindowController {
    @FXML
    private TextField deleteInputField = new TextField();
    @FXML
    private TextArea deleteFileDisplayArea = new TextArea();
    @FXML
    private TextField deleteNotificationField = new TextField();
    private final AppVersionData appVersionData = new AppVersionData();
    private final FileHandler fileHandler = new FileHandler(appVersionData.getSaveFileTitle());


    public void initialize() {
        String savefiles = fileHandler.listOfAllStorageFiles();
        deleteFileDisplayArea.setText(savefiles);
    }

    @FXML
    void deleteTextBoxAction(ActionEvent event) throws IOException {
        String filename = null;
        // try to access the text field
        try {
            filename = deleteInputField.getText();
        } catch (NullPointerException e) {
            deleteFileDisplayArea.setText("SYSTEM ERROR: text field is null");
            System.out.println("SYSTEM ERROR: text field is null");
            return;
        }
        // try to access a file
        if (!fileHandler.fileExists(filename)) {
            deleteNotificationField.setText("Cannot locate a file with that name.");
            System.out.println("*** Cannot locate file with that name ***");
        }
        // if the file is found, delete it
        else  {
            Window deleteWindow = ((Node)event.getSource()).getScene().getWindow();
            ((Stage)deleteWindow).hide();
            fileHandler.deleteFile(filename);
        }
    }


    @FXML
    public void mouseClicked(MouseEvent mouseEvent) {
        String highlightedText = deleteFileDisplayArea.getSelectedText();
        if (highlightedText.equals(""))
            return;
        deleteInputField.setText(highlightedText);
        String filename = null;
        // try to access the text field
        try {
            filename = deleteInputField.getText();
        } catch (NullPointerException e) {
            deleteFileDisplayArea.setText("SYSTEM ERROR: text field is null");
            System.out.println("SYSTEM ERROR: text field is null");
            return;
        }
        // try to access a file
        if (!fileHandler.fileExists(filename)) {
            deleteNotificationField.setText("Cannot locate a file with that name.");
            System.out.println("*** Cannot locate file with that name ***");
        }
        // if the file is found, delete it
        else  {
            Window deleteWindow = ((Node)mouseEvent.getSource()).getScene().getWindow();
            ((Stage)deleteWindow).hide();
            fileHandler.deleteFile(filename);
        }
    }
}
