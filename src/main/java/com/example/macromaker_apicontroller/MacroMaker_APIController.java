package com.example.macromaker_apicontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class MacroMaker_APIController {

    @FXML
    private TextArea macroTextBox = new TextArea();
    @FXML
    private TextField loopCountBox = new TextField();
    @FXML
    private Slider keystrokeSlider = new Slider();
    @FXML
    private Slider waitTimeSlider = new Slider();
    private final Macro macro;

    public MacroMaker_APIController() {
        try {
            macro = new Macro();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void displaySaveFileLocation(ActionEvent actionEvent) {
        final AppVersionData appVersionData = new AppVersionData();
        final FileHandler fileHandler = new FileHandler(appVersionData.getSaveFileTitle());
        String contents = macroTextBox.getText();
        macroTextBox.setText(fileHandler.getRootPath() + "\n\n" + contents);
    }

    @FXML
    void executePressed(ActionEvent event) throws AWTException {
        macro.executeKeyMacro(macroTextBox.getText());
    }

    @FXML
    void loadPressed(ActionEvent event) {
        openWindow(event, "loadfile-view.fxml");
    }

    @FXML
    void savePressed(ActionEvent event) {
        openWindow(event, "savefile-view.fxml");
    }


    @FXML
    void deletePressed(ActionEvent event) {
        openWindow(event, "deletefile-view.fxml");
    }

    private void openWindow(ActionEvent event, String fxmlFile) {
        try {
            Window parentWindow = ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = (Parent) fxmlLoader.load();
            Stage savefileStage = new Stage();
            savefileStage.getIcons().add(new Image(Objects.requireNonNull(MacroMaker_APIApplication.class.getResourceAsStream("/hard-drive.png"))));
            savefileStage.setTitle("Save Files");
            savefileStage.setScene(new Scene(root));
            savefileStage.setResizable(false);
            savefileStage.initOwner(parentWindow);
            savefileStage.show();
        } catch (Exception e) {
            System.out.println("cant load new window");
            String contents = macroTextBox.getText();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            macroTextBox.setText("*** can't load new window ***\n" + pw + '\n' +
                    "\n\n           *** POSSIBLE SOLUTION ***" +
                    "\n * Try closing the program and running it again by right-clicking" +
                    "\n   the shortcut and selecting \"Run as Administrator\"\n\n" + contents);

        }
    }


    public void keystrokeSliderScroll(ScrollEvent scrollEvent) {
        final double direction = scrollEvent.getDeltaY();
        final int scrollDistance = 1;
        if (direction > 0) {
            macro.setBuffer(macro.getBuffer() + scrollDistance);
            keystrokeSlider.setValue((int) keystrokeSlider.getValue() + scrollDistance);
        }
        else {
            macro.setBuffer(macro.getBuffer() - scrollDistance);
            keystrokeSlider.setValue((int) keystrokeSlider.getValue() - scrollDistance);
        }
    }


    public void waitTimeSliderScroll(ScrollEvent scrollEvent) {
        final double direction = scrollEvent.getDeltaY();
        final int scrollDistance = 1;
        if (direction > 0) {
            macro.setInitialWaitTime_ms((macro.getInitialWaitTime_ms()/1000) + scrollDistance);
            waitTimeSlider.setValue((int) waitTimeSlider.getValue() + scrollDistance);
        }
        else {
            macro.setInitialWaitTime_ms((macro.getInitialWaitTime_ms()/1000) - scrollDistance);
            waitTimeSlider.setValue((int) waitTimeSlider.getValue() - scrollDistance);
        }
    }


    public void keystrokeSliderDragDropped(MouseEvent mouseEvent) {
        macro.setBuffer((int) keystrokeSlider.getValue());
    }


    public void waitTimeSliderDragDropped(MouseEvent mouseEvent) {
        macro.setInitialWaitTime_ms((int) waitTimeSlider.getValue());
    }


    public void loopCountAltered(KeyEvent actionEvent) {
        try {
            final int loopCt = Integer.parseInt(loopCountBox.getText());
            macro.setLoopCount(loopCt);
            if (loopCt < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            macro.setLoopCount(1);
            loopCountBox.setText("1");
        }
    }


    public void killMacro(ActionEvent actionEvent) {
        macro.killAnimation();
    }
}
