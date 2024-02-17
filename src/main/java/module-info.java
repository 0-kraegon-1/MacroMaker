module com.example.macromaker_apicontroller {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.datatransfer;
    requires java.desktop;

    opens com.example.macromaker_apicontroller to javafx.fxml;
    exports com.example.macromaker_apicontroller;
}
