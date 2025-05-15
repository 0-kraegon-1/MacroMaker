package com.example.macromaker_apicontroller;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class MacroMaker_APIApplication extends Application {
    AppData appData = new AppData();


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MacroMaker_APIApplication.class.getResource("macro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        try {
            scene.getStylesheets().add(String.valueOf(getClass().getResource(ThemeManager.ActiveStyleSheet)));
        } catch (NullPointerException e) {
            System.out.println("no stylesheet available");
        }
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setOnShown(event -> {
            ObservableList<Node> titleBarNodes = ((AnchorPane)stage.getScene().getRoot().getChildrenUnmodifiable().get(1)).getChildrenUnmodifiable();
            ((ImageView)titleBarNodes.get(1)).setImage(new Image(Objects.requireNonNull(MacroMaker_APIApplication.class.getResourceAsStream("/mouse.png"))));
            ((Label)titleBarNodes.get(2)).setText("MacroMaker " + appData.getAppVersion());
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        System.out.println("*** APPLICATION-SHUTTING-DOWN ***");
        MacroMaker_API_MouseMacroController.stopListeners();
        System.exit(0);
    }

}
