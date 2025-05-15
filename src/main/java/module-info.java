module com.example.macromaker_apicontroller {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.datatransfer;
    requires transitive java.desktop;
    requires transitive java.logging;
    requires com.github.kwhat.jnativehook;
    requires org.jsoup;
    requires org.apache.commons.lang3;

    opens com.example.macromaker_apicontroller to javafx.fxml;
    exports com.example.macromaker_apicontroller;
}


/*
module com.github.kwhat.jnativehook {
	exports com.github.kwhat.jnativehook;
	exports com.github.kwhat.jnativehook.dispatcher;
	exports com.github.kwhat.jnativehook.keyboard;
	exports com.github.kwhat.jnativehook.mouse;

}
*/