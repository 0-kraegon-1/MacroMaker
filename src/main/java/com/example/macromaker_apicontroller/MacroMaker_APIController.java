package com.example.macromaker_apicontroller;

import com.github.kwhat.jnativehook.GlobalScreen;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MacroMaker_APIController {
    @FXML protected static ImageView icon;
    @FXML protected static Label title;
    @FXML protected ButtonBar buttonBar = new ButtonBar();
    @FXML private MenuBar menuBar = new MenuBar();
    @FXML protected Label macroIndicatorText = new Label(" WAITING... ");
    @FXML protected AnchorPane titleBar = new AnchorPane();
    @FXML private TextArea macroTextBox = new TextArea();
    @FXML private TextField loopCountBox = new TextField();
    @FXML private CheckBox loopIndefinitelyCheckBox = new CheckBox();
    @FXML private Slider keystrokeSlider = new Slider();
    @FXML private Slider waitTimeSlider = new Slider();
    @FXML private CheckBox alwaysOnTopCheckBox = new CheckBox();
    private final AppData appData = new AppData();
    private Stage primaryStage = null;
    private final Macro macro = new Macro();
    InputListener inputListener = new InputListener();


    @FXML
    public void initialize() {
        if (!GlobalScreen.isNativeHookRegistered()) {
            try {
                GlobalScreen.registerNativeHook();
            } catch (Exception e) {
                System.out.println("***FAILED-TO-INITIALIZE-LISTENERS***");     // error-print
                System.exit(-1);
            }
        }
    }


    @FXML
    void executePressed(ActionEvent actionEvent) {
        ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(true);
        macroIndicatorText.setText(" WAITING... ");
        macro.executeKeyMacro(actionEvent, macroTextBox.getText());
    }


    @FXML
    void fileMenuOptionPressed(ActionEvent actionEvent) {
        ObservableList<Node> rootChildren = ((MenuItem)actionEvent.getSource()).getParentPopup().getOwnerWindow().getScene().getRoot().getChildrenUnmodifiable();
        for (Node node : rootChildren)
            if (!node.toString().contains("AnchorPane"))
                node.setDisable(true);
        WindowManager.openWindow(actionEvent, "files-view.fxml", "File Manager", "/hard-drive.png", true);
    }


    @FXML
    public void keystrokeSliderDragDropped(MouseEvent mouseEvent) {
        macro.setKeystrokeDelay((int) keystrokeSlider.getValue());
    }

    @FXML
    public void keystrokeSliderScroll(ScrollEvent scrollEvent) {
        final double direction = scrollEvent.getDeltaY();
        final int scrollDistance = 1;
        if (direction > 0) {
            macro.setKeystrokeDelay(macro.getKeystrokeDelay() + scrollDistance);
            keystrokeSlider.setValue((int) keystrokeSlider.getValue() + scrollDistance);
        }
        else {
            macro.setKeystrokeDelay(macro.getKeystrokeDelay() - scrollDistance);
            keystrokeSlider.setValue((int) keystrokeSlider.getValue() - scrollDistance);
        }
    }


    @FXML
    public void waitTimeSliderDragDropped(MouseEvent mouseEvent) {
        macro.setInitialWaitTime_ms(((int)waitTimeSlider.getValue()) * 1000);
    }

    @FXML
    public void waitTimeSliderScroll(ScrollEvent scrollEvent) {
        final double direction = scrollEvent.getDeltaY();
        final int scrollDistance = 1;
        if (direction > 0) {
            macro.setInitialWaitTime_ms(macro.getInitialWaitTime_ms() + (scrollDistance * 1000));    // Update macro wait-time
            waitTimeSlider.setValue((int) waitTimeSlider.getValue() + scrollDistance);               // Update slider position
        }
        else {
            macro.setInitialWaitTime_ms(macro.getInitialWaitTime_ms() - (scrollDistance * 1000));    // Update macro wait-time
            waitTimeSlider.setValue((int) waitTimeSlider.getValue() - scrollDistance);               // Update slider position
        }
    }


    @FXML
    public void loopCountAltered(KeyEvent actionEvent) {
        try {
            final int loopCt = Integer.parseInt(loopCountBox.getText());    // Discern validity of user-input
            macro.setLoopCount(loopCt);
            if (loopCt < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            macro.setLoopCount(1);                       // Default to one loop if any errors are encountered
            loopCountBox.setText("1");
        }
    }


    @FXML
    public void killMacroPressed(ActionEvent actionEvent) {
        macro.killKeyboardAnimation();
    }


    @FXML
    public void clearTextPressed(ActionEvent actionEvent) {
        macroTextBox.clear();
    }


    @FXML
    public void switchToMouseMacroEditor(ActionEvent actionEvent) {
        appData.setActiveApp(AppData.ActiveApp.MOUSE_MACRO_EDITOR);
        WindowManager.openWindow(actionEvent, "mouse-view.fxml", "Mouse & Keyboard Macro Editor", "/wireless-keyboard.png", false);
        ((MenuItem)actionEvent.getSource()).getParentPopup().getOwnerWindow().hide();
    }


    @FXML
    public void displayKeyMacroSaveFileLocation() {
        final FileHandler fileHandler = new FileHandler();
        String contents = macroTextBox.getText();
        macroTextBox.setText(fileHandler.getKeyMacroSaveFilePath() + "\n\n" + contents);
    }

    @FXML
    public void alwaysOnTopCheckBoxClicked(ActionEvent actionEvent) {
        ((Stage)((ContextMenu)((CheckBox)actionEvent.getTarget()).getParent().getScene().getWindow()).getOwnerWindow()).setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
    }

    @FXML
    public void loopIndefinitelyCheckBoxClicked(ActionEvent actionEvent) {
        macro.setLoopIndefinitely(loopIndefinitelyCheckBox.isSelected());
        loopCountBox.setDisable(loopIndefinitelyCheckBox.isSelected());
    }


    
    //          TITLE-BAR-METHODS
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
