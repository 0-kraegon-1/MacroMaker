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

public class MacroMaker_API_SaveFileWindowController {
    @FXML
    private TextField saveInputField = new TextField();
    @FXML
    private TextArea saveFileDisplayArea = new TextArea();
    @FXML
    private TextField saveNotificationField = new TextField();
    private final AppVersionData appVersionData = new AppVersionData();
    private final FileHandler fileHandler = new FileHandler(appVersionData.getSaveFileTitle());


    public void initialize() {
        String savefiles = fileHandler.listOfAllStorageFiles();
        saveFileDisplayArea.setText(savefiles);
    }

    @FXML
    void saveTextBoxAction(ActionEvent event) throws IOException {
        String filename = null;
        // try to access the text field
        try {
            filename = saveInputField.getText();
        } catch (NullPointerException e) {
            saveFileDisplayArea.setText("ERROR: text field is null");
            System.out.println("ERROR: text field is null");
            return;
        }
        // get parent window text area contents
        Window saveWindow = ((Node)event.getSource()).getScene().getWindow();
        Window saveWindowOwner = null ;
        if (saveWindow instanceof Stage)
            saveWindowOwner = ((Stage)saveWindow).getOwner();
        assert saveWindowOwner != null;
        Parent parentRoot = saveWindowOwner.getScene().getRoot();
        TextArea macroTextBox = (TextArea) parentRoot.lookup("#macroTextBox");
        String saveData = macroTextBox.getText();

        // write parent window text area contents to user defined file and hide save window
        fileHandler.writeToFile(filename, saveData);
        ((Stage)saveWindow).hide();
    }


    @FXML
    public void mouseClicked(MouseEvent mouseEvent) {
        String highlightedText = saveFileDisplayArea.getSelectedText();
        if (highlightedText.equals(""))
            return;
        saveInputField.setText(highlightedText);
        String filename = null;
        // try to access the text field
        try {
            filename = saveInputField.getText();
        } catch (NullPointerException e) {
            saveFileDisplayArea.setText("ERROR: text field is null");
            System.out.println("ERROR: text field is null");
            return;
        }
        // get parent window text area contents
        Window saveWindow = ((Node)mouseEvent.getSource()).getScene().getWindow();
        Window saveWindowOwner = null ;
        if (saveWindow instanceof Stage)
            saveWindowOwner = ((Stage)saveWindow).getOwner();
        assert saveWindowOwner != null;
        Parent parentRoot = saveWindowOwner.getScene().getRoot();
        TextArea macroTextBox = (TextArea) parentRoot.lookup("#macroTextBox");
        String saveData = macroTextBox.getText();

        // write parent window text area contents to user defined file and hide save window
        fileHandler.writeToFile(filename, saveData);
        ((Stage)saveWindow).hide();
    }
}
