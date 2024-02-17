package com.example.macromaker_apicontroller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MacroMaker_APIApplication extends Application {
    AppVersionData appVersionData = new AppVersionData();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MacroMaker_APIApplication.class.getResource("macro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.getIcons().add(new Image(Objects.requireNonNull(MacroMaker_APIApplication.class.getResourceAsStream("/wireless-keyboard.png"))));
        stage.setTitle("MacroMaker " + appVersionData.getAppVersion());
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
