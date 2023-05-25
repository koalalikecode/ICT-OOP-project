module oop_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.json;
    requires org.jsoup;

    opens appgui to javafx.fxml;
    exports appgui;
}