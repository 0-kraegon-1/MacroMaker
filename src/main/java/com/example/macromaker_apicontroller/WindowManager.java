package com.example.macromaker_apicontroller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class WindowManager {
    private static double sceneX;
    private static double sceneY;


    @FXML
    protected static void closeWindow(ActionEvent actionEvent) {
        ((Button)actionEvent.getTarget()).getParent().getScene().getWindow().hide();
    }

    @FXML
    protected static void maximizeWindow(ActionEvent actionEvent) {
        System.out.println("maximize pressed");
    }

    @FXML
    protected static void minimizeWindow(ActionEvent actionEvent) {
        ((Stage)((Button)actionEvent.getTarget()).getParent().getScene().getWindow()).setIconified(true);
    }


    @FXML
    protected static void windowDragEntered(double x, double y) {
        sceneX = x;
        sceneY = y;
    }

    @FXML
    protected static void windowDragging(MouseEvent mouseEvent, Stage stage) {
        stage.setX(mouseEvent.getScreenX() - sceneX);
        stage.setY(mouseEvent.getScreenY() - sceneY);
    }


    protected static void openWindow(ActionEvent actionEvent, String fxmlFile, String windowTitle, String windowIcon, boolean isPopup) {
        try {
            Window parentWindow = ((MenuItem) actionEvent.getTarget()).getParentPopup().getOwnerWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(WindowManager.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(String.valueOf(WindowManager.class.getResource(ThemeManager.ActiveStyleSheet)));
            } catch (NullPointerException e) {
                System.out.println("no stylesheet available");
            }
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            if (isPopup) {
                stage.initOwner(parentWindow);
                stage.setResizable(false);
                String menuItemText = ((Label)((MenuItem)actionEvent.getTarget()).getGraphic()).getText();
                stage.setOnShown(event -> {
                    ObservableList<Node> titleBarNodes = ((AnchorPane)stage.getScene().getRoot().getChildrenUnmodifiable().get(1)).getChildrenUnmodifiable();
                    ((ImageView)titleBarNodes.get(1)).setImage(new Image(Objects.requireNonNull(MacroMaker_APIApplication.class.getResourceAsStream(windowIcon))));
                    ((Label)titleBarNodes.get(2)).setText(windowTitle);
                    if (windowTitle.equals("File Manager")) {
                        ObservableList<Node> buttonsOnBar = ((ButtonBar)((VBox) stage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getChildrenUnmodifiable().get(1)).getButtons();
                        for (Node node : buttonsOnBar) {
                            if (node.getId() != null && ((ToggleButton) node).getText().equals(menuItemText)) {
                                System.out.println("toggle btn text = " + ((ToggleButton) node).getText());
                                ((ToggleButton) node).setSelected(true);
                            }
                        }
                    }
                });
                stage.setOnHidden(event -> {
                    ObservableList<Node> rootChildren = ((MenuItem)actionEvent.getSource()).getParentPopup().getOwnerWindow().getScene().getRoot().getChildrenUnmodifiable();
                    for (Node node : rootChildren)
                        if (!node.toString().contains("AnchorPane"))
                            node.setDisable(false);
                });
            }
            else {

                stage.setOnShown(event -> {
                    ObservableList<Node> titleBarNodes = ((AnchorPane) stage.getScene().getRoot().getChildrenUnmodifiable().get(1)).getChildrenUnmodifiable();
                    ((ImageView) titleBarNodes.get(1)).setImage(new Image(Objects.requireNonNull(MacroMaker_APIApplication.class.getResourceAsStream(windowIcon))));
                    ((Label) titleBarNodes.get(2)).setText(windowTitle);
                });
            }

            stage.show();
        } catch (Exception e) {
            System.out.println("***FAILED-TO-LOAD-NEW-WINDOW***");
            //String contents = macroTextBox.getText();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            e.printStackTrace();
            /*macroTextBox.setText("*** can't load new window ***\n" + pw + '\n' +
                    "\n\n           *** POSSIBLE SOLUTION ***" +
                    "\n * Try closing the program and running it again by right-clicking" +
                    "\n   the shortcut and selecting \"Run as Administrator\"\n\n" + contents);*/
        }
    }

}
