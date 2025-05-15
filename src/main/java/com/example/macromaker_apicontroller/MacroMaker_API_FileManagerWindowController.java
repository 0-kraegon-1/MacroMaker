package com.example.macromaker_apicontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MacroMaker_API_FileManagerWindowController implements Initializable {
    @FXML protected HBox hBox = new HBox();
    @FXML protected TextField textField = new TextField();
    @FXML protected ToggleButton loadToggle = new ToggleButton();
    @FXML protected ToggleButton saveToggle = new ToggleButton();
    @FXML protected ToggleButton deleteToggle = new ToggleButton();
    @FXML protected ImageView icon;
    @FXML protected Label title;
    @FXML protected ListView<String> saveFileListView = new ListView<>();
    private final FileHandler fileHandler = new FileHandler();
    private Stage primaryStage = null;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<File> saveFiles;
        switch (AppData.activeApp.name()) {
            case "KEYBOARD_MACRO_EDITOR" -> {
                saveFiles = fileHandler.listOfAllMacroFiles("Key Macro Save Files");
                for (File file : saveFiles) {
                    String fileName = file.getName();
                    if (!fileName.contains(".txt"))
                        continue;
                    saveFileListView.getItems().add(fileName.substring(0, fileName.length()-4));
                }
            }
            case "MOUSE_MACRO_EDITOR" -> {
                saveFiles = fileHandler.listOfAllMacroFiles("Mouse Macro Save Files");
                for (File file : saveFiles) {
                    String fileName = file.getName();
                    if (!fileName.contains(".txt"))
                        continue;
                    saveFileListView.getItems().add(fileName.substring(0, fileName.length()-4));
                }
            }
        }
    }


    @FXML
    public void togglePressed(ActionEvent actionEvent) {
        String tButtonText = ((ToggleButton)actionEvent.getTarget()).getText();
        switch (tButtonText) {
            case "Load" -> {
                loadToggle.setSelected(true);
                saveToggle.setSelected(false);
                deleteToggle.setSelected(false);
            }
            case "Save" -> {
                loadToggle.setSelected(false);
                saveToggle.setSelected(true);
                deleteToggle.setSelected(false);
            }
            case "Delete" -> {
                loadToggle.setSelected(false);
                saveToggle.setSelected(false);
                deleteToggle.setSelected(true);
            }
        }
    }

    @FXML
    public void editStart(ListView.EditEvent<String> stringEditEvent) {
        enterPressed(new ActionEvent().copyFor(textField, textField));
    }

    @FXML
    public void enterPressed(ActionEvent actionEvent) {
        if (textField.getText().length() == 0) return;
        String textFieldData = textField.getText();
        Stage fileManagerStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Stage parentStage = (Stage)(fileManagerStage).getOwner();
        TextArea macroTextBox = (TextArea) parentStage.getScene().getRoot().lookup("#macroTextBox");

        if (loadToggle.isSelected()) {
            System.out.println("\n-load action begun-");            // debug-print
            String fileData = fileHandler.getFileData(textFieldData);
            if (AppData.activeApp.name().equals("MOUSE_MACRO_EDITOR"))
                MacroMaker_API_MouseMacroController.parseLoadedMacroData(fileData);
            macroTextBox.setText(fileData);
            fileManagerStage.hide();
            System.out.println("-load action finished-\n");         // debug-print
        }
        else if (deleteToggle.isSelected()) {
            System.out.println("\n-delete action begun-");          // debug-print
            String fileName = textField.getText();
            fileHandler.deleteFile(fileName);
            fileManagerStage.hide();
            System.out.println("-delete action finished-\n");       // debug-print
        }
        else if (saveToggle.isSelected()) {
            System.out.println("\n-save action begun-");            // debug-print
            String fileName = textField.getText();
            if (!fileHandler.writeToFile(fileName, macroTextBox.getText())) {
                System.out.println("file write error!");
            }
            fileManagerStage.hide();
            System.out.println("-save action finished-\n");         // debug-print
        }
    }

    @FXML
    public void mousePressDetected(MouseEvent mouseEvent) {
        String targetString = mouseEvent.getTarget().toString();
        for (String filenameString : saveFileListView.getItems()) {
            if (targetString.contains(filenameString)) {
                textField.setText(filenameString);
                break;
            }
            else textField.clear();
        }
    }

    @FXML
    public void keyPressed(KeyEvent keyEvent) {
        String pressedKey = keyEvent.getCode().name();
        if (pressedKey.equals("UP") || pressedKey.equals("DOWN")) {
            int selectedIndex = saveFileListView.selectionModelProperty().get().getSelectedIndices().get(0);
            textField.setText(saveFileListView.getItems().get(selectedIndex));
        }
    }


    //              TITLE-BAR-METHODS
    @FXML
    public void minimizeButtonPressed(ActionEvent actionEvent) {
        WindowManager.minimizeWindow(actionEvent);
    }

    @FXML
    public void maximizeButtonPressed(ActionEvent actionEvent) {
        WindowManager.maximizeWindow(actionEvent);
    }

    @FXML
    public void closeButtonPressed(ActionEvent actionEvent) {
        WindowManager.closeWindow(actionEvent);
    }

    @FXML
    public void windowDragEntered(MouseEvent mouseEvent) {
        if (primaryStage == null)
            primaryStage = ((Stage)((Node)mouseEvent.getTarget()).getScene().getWindow());
        WindowManager.windowDragEntered(mouseEvent.getSceneX(), mouseEvent.getSceneY());
    }

    @FXML
    public void windowDragging(MouseEvent mouseEvent) {
        WindowManager.windowDragging(mouseEvent, primaryStage);
    }


}
