package appgui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Test2 extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        Hyperlink reloadLink = new Hyperlink("Reload");

        reloadLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Reload the pane
                // Your reload logic here
                pane.getChildren().clear(); // Clear existing content
                // Add new content to the pane
                String reloaded = "Reloaded" + reloadLink;
                reloadLink.setText(reloaded);
                pane.getChildren().add(reloadLink);
                // ...
            }
        });

        pane.getChildren().add(reloadLink);

        Scene scene = new Scene(pane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
