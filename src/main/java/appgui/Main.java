package appgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class Main extends Application {
    double x,y = 0;
    @Override
    public void start(Stage stage) throws Exception {



        Parent root = FXMLLoader.load(getClass().getResource("fxml/startPane.fxml"));
//        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root, 1067, 600);

        stage.getIcons().add(new Image(getClass().getResource("icons/vietnam-logo.png").toExternalForm()));
        stage.setTitle("Sử Việt Toàn Thư");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}