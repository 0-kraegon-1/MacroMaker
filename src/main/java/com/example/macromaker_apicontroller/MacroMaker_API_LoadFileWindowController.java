package com.example.macromaker_apicontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MacroMaker_API_LoadFileWindowController {
    @FXML
    private TextField loadInputField = new TextField();
    @FXML
    private TextArea loadFileDisplayArea = new TextArea();
    @FXML
    private TextField loadNotificationField = new TextField();
    private final AppVersionData appVersionData = new AppVersionData();
    private final FileHandler fileHandler = new FileHandler(appVersionData.getSaveFileTitle());

    public void initialize() {
        String savefiles = fileHandler.listOfAllStorageFiles();
        loadFileDisplayArea.setText(savefiles);
    }

    @FXML
    void loadTextBoxAction(ActionEvent event) throws IOException {
        String filename = null;
        // try to access the text field
        try {
            filename = loadInputField.getText();
        } catch (NullPointerException e) {
            loadFileDisplayArea.setText("SYSTEM ERROR: text field is null");
            System.out.println("SYSTEM ERROR: text field is null");
            return;
        }
        // try to access a file
        if (!fileHandler.fileExists(filename)) {
            loadNotificationField.setText("Cannot locate a file with that name.");
            System.out.println("*** Cannot locate file with that name ***");
        }
        // if the file is found, get window owner
        else  {
            Window loadWindow = ((Node)event.getSource()).getScene().getWindow();
            Window loadWindowOwner = null ;
            if (loadWindow instanceof Stage) {
                loadWindowOwner = ((Stage)loadWindow).getOwner();
            }
            assert loadWindowOwner != null;

            // set save file window owner's text area to found file's data and hide save file window
            Parent parentRoot = loadWindowOwner.getScene().getRoot();
            TextArea macroTextBox = (TextArea) parentRoot.lookup("#macroTextBox");
            macroTextBox.setText(String.valueOf(fileHandler.getFileData(filename)));
            ((Stage)loadWindow).hide();
        }

    }


    @FXML
    public void mouseClicked(MouseEvent mouseEvent) {
        String highlightedText = loadFileDisplayArea.getSelectedText();
        if (highlightedText.equals(""))
            return;
        loadInputField.setText(highlightedText);
        String filename = null;
        // try to access the text field
        try {
            filename = loadInputField.getText();
        } catch (NullPointerException e) {
            loadFileDisplayArea.setText("SYSTEM ERROR: text field is null");
            System.out.println("SYSTEM ERROR: text field is null");
            return;
        }
        // try to access a file
        if (!fileHandler.fileExists(filename)) {
            System.out.println("*** Cannot locate file with that name ***");
        }
        // if the file is found, get window owner
        else  {
            Window loadWindow = ((Node)mouseEvent.getSource()).getScene().getWindow();
            Window loadWindowOwner = null ;
            if (loadWindow instanceof Stage) {
                loadWindowOwner = ((Stage)loadWindow).getOwner();
            }
            assert loadWindowOwner != null;

            // set save file window owner's text area to found file's data and hide save file window
            Parent parentRoot = loadWindowOwner.getScene().getRoot();
            TextArea macroTextBox = (TextArea) parentRoot.lookup("#macroTextBox");
            macroTextBox.setText(String.valueOf(fileHandler.getFileData(filename)));
            ((Stage)loadWindow).hide();
        }
    }
}
