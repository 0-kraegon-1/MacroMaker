package com.example.macromaker_apicontroller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MacroMaker_API_MouseMacroController {
    @FXML protected  Slider randomIntensitySlider;
    @FXML protected ImageView icon;
    @FXML protected Label title;
    @FXML private MenuBar menuBar = new MenuBar();
    @FXML protected ButtonBar buttonBar = new ButtonBar();
    @FXML private Button recordButton = new Button();
    @FXML protected Button executeButton = new Button();
    @FXML protected Button clearButton = new Button();
    @FXML private Slider waitTimeSlider = new Slider();
    @FXML private TextField loopCountBox = new TextField();
    @FXML private CheckBox loopIndefinitelyCheckBox = new CheckBox();
    @FXML private SplitPane splitPane = new SplitPane();
    @FXML private TextArea macroTextBox = new TextArea();
    @FXML private TextField screenSizeBox = new TextField();
    @FXML private TextField mousePositionBox = new TextField();
    @FXML private CheckBox alwaysOnTopCheckBox = new CheckBox();
    @FXML private CheckBox randomizeLeftClickCheckBox = new CheckBox();
    @FXML private HBox macroIndicatorField = new HBox();
    @FXML private Label macroIndicatorText = new Label();
    private Stage primaryStage = null;
    private boolean recording = false;
    private final AppData appData = new AppData();
    private final double frameRate = AppData.GetMacroFrameRate();
    private final Macro macro = new Macro();
    private final Timeline mouseScanner = new Timeline( new KeyFrame(Duration.millis((double)1000/30), e -> mousePositionBox.setText(macro.getMousePosition())) );
    private final Timeline macroListBuilder = new Timeline( new KeyFrame(Duration.millis(frameRate), e -> storeInput() ));
    private static final List<RTMCommand> macroList = new ArrayList<>();
    protected static final InputListener inputListener = new InputListener();
    protected static boolean QUIT = false;


    public void initialize() {
        screenSizeBox.setText(macro.getScreenDimensions());
        macroListBuilder.setCycleCount(Animation.INDEFINITE);
        mouseScanner.setCycleCount(Animation.INDEFINITE);
        mouseScanner.play();
    }

    //          ***** COMMAND-STRING-FORMAT *****
    //          <x> <y> <mb1> <mb2> <mb3> <mw> <k>
    //             10 20 1 0 0 0 [W, A, Space]
    private synchronized void storeInput() {
        if (!MacroMaker_API_MouseMacroController.QUIT)
            macroList.add( new RTMCommand( inputListener.getMouseX()!=0? inputListener.getMouseX() : macro.getMousePosX(),
                                           inputListener.getMouseY()!=0? inputListener.getMouseY() : macro.getMousePosY(),
                                           inputListener.isMouseB1Pressed(),
                                           inputListener.isMouseB2Pressed(),
                                           inputListener.isMouseB3Pressed(),
                                           inputListener.mouseWheelState(),
                                           new ArrayList<>(inputListener.getPressedKeys())
            ));
        else {
            recordPressed(new ActionEvent());
            MacroMaker_API_MouseMacroController.QUIT = false;
        }
    }

    @FXML
    public void recordPressed(ActionEvent actionEvent) {
        recording = !recording;
        if (recording) {
            mouseScanner.stop();
            inputListener.startInputListener();
            recordButton.setText("STOP");
            macroList.clear();
            ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(true);
            ((Label)((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorText")).setText(" RECORDING ");
            lockControls(true);
            macroListBuilder.play();
        } else {
            macroListBuilder.stop();
            mouseScanner.play();
            stopListeners();
            recordButton.getScene().lookup("#macroIndicatorField").setVisible(false);
            ((Label)(recordButton).getScene().lookup("#macroIndicatorText")).setText(" WAITING... ");
            lockControls(false);
            recordButton.setText("Record");
            /* ===========================================================================
            |  - Deletes all commands containing a left-click from the end of the macro.  |
            | --------------------------------------------------------------------------- |
            |  - Prevents the macro from clicking the "Record" button, which              |
            |    could cause an infinite macro-loop.                                      |
             =========================================================================== */
            int endIndex = macroList.size() - 1;
            while (!macroList.isEmpty() && macroList.get(endIndex).mb1() == 1) {
                macroList.remove(endIndex);
                endIndex--;
                //System.out.println("left-click removed");  // debug-print
            }
            displayMacroList();
        }
    }

    public static void parseLoadedMacroData(String fileData) {
        macroList.clear();
        String[] macroStrings = fileData.split("\n");
        String[] activeKeys = null;
        for (String macro : macroStrings) {
            String[] commands = macro.split(" \\| ");

            if (!Objects.equals(commands[6], "[]"))
                activeKeys = (commands[6].substring(1,commands[6].length()-1)).split(", ");
            else
                activeKeys = new String[0];

            macroList.add( new RTMCommand (
                    Integer.parseInt(commands[0]),
                    Integer.parseInt(commands[1]),
                    Integer.parseInt(commands[2]),
                    Integer.parseInt(commands[3]),
                    Integer.parseInt(commands[4]),
                    Integer.parseInt(commands[5]),
                    List.of(activeKeys)
            ));
        }
    }

    private void displayMacroList() {
        if (!macroList.isEmpty()) {
            StringBuilder fullMacroString = new StringBuilder();
            for (RTMCommand command : macroList)
                fullMacroString.append(command.toString()).append("\n");
            macroTextBox.setText(fullMacroString.toString());
        }
    }

    public static void stopListeners() {
        inputListener.stopInputListener();
    }

    @FXML
    public void switchToKeyboardMacroEditor(ActionEvent actionEvent) {
        mouseScanner.stop();
        appData.setActiveApp(AppData.ActiveApp.KEYBOARD_MACRO_EDITOR);
        WindowManager.openWindow(actionEvent, "macro-view.fxml", appData.getAppMainTitle(), "/wireless-keyboard.png", false);
        ((MenuItem)actionEvent.getSource()).getParentPopup().getOwnerWindow().hide();
    }

    @FXML
    public void executePressed(ActionEvent actionEvent) {
        if (!macroList.isEmpty()) {
            ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorText").setAccessibleText(" WAITING... ");
            ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(true);
            macro.executeRealTimeMouseAndKeyMacro(actionEvent, macroList);
        }
    }

    @FXML
    public void clearTextPressed(ActionEvent actionEvent) {
        macroTextBox.clear();
        macroList.clear();
    }

    @FXML
    public void waitTimeSliderDragDropped(MouseEvent mouseEvent) {
        macro.setInitialWaitTime_ms(((int)waitTimeSlider.getValue()) * 1000);
    }

    @FXML
    public void waitTimeSliderScroll(ScrollEvent scrollEvent) {
        final double direction = scrollEvent.getDeltaY();
        if (direction > 0) {
            waitTimeSlider.setValue((int) waitTimeSlider.getValue() + 1);               // Update slider position
            macro.setInitialWaitTime_ms(macro.getInitialWaitTime_ms() + 1000);          // Update macro wait-time
        }
        else {
            waitTimeSlider.setValue((int) waitTimeSlider.getValue() - 1);               // Update slider position
            macro.setInitialWaitTime_ms(macro.getInitialWaitTime_ms() - 1000);          // Update macro wait-time
        }
    }


    @FXML
    public void loopCountAltered(KeyEvent keyEvent) {
        try {
            final int loopCt = Integer.parseInt(loopCountBox.getText());
            macro.setLoopCount(loopCt);
            if (loopCt < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            macro.setLoopCount(1);
            loopCountBox.setText("1");
        }
    }

    @FXML
    public void displayMouseMacroSaveFileLocation(ActionEvent actionEvent) {
        final FileHandler fileHandler = new FileHandler();
        macroTextBox.setText(fileHandler.getMouseMacroSaveFilePath() + "\n");
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
    public void alwaysOnTopCheckBoxClicked(ActionEvent actionEvent) {
        ((Stage)((ContextMenu)((CheckBox)actionEvent.getTarget()).getParent().getScene().getWindow()).getOwnerWindow()).setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
    }

    @FXML
    public void loopIndefinitelyCheckBoxClicked(ActionEvent actionEvent) {
        macro.setLoopIndefinitely(loopIndefinitelyCheckBox.isSelected());
        loopCountBox.setDisable(loopIndefinitelyCheckBox.isSelected());
    }

    @FXML
    public void randomizeLeftClickCheckBoxClicked(ActionEvent actionEvent) {
        Macro.BOT_MODE = randomizeLeftClickCheckBox.isSelected();
        randomIntensitySlider.setDisable(!randomizeLeftClickCheckBox.isSelected());
    }

    private void lockControls(boolean controlState) {
        menuBar.setDisable(controlState);
        executeButton.setDisable(controlState);
        clearButton.setDisable(controlState);
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
