package appgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class Main extends Application {
    double x,y = 0;
    @Override
    public void start(Stage stage) throws Exception {

//        Parent root = FXMLLoader.load(getClass().getResource("fxml/characterPane.fxml"));
//        Scene sc = new Scene(root);
////        stage.initStyle(StageStyle.UNDECORATED);
//
//        String css = this.getClass().getResource("css/style.css").toExternalForm();
//        sc.getStylesheets().add(css);
//        root.setOnMousePressed(event -> {
//            x = event.getSceneX();
//            y = event.getSceneY();
//        });
//        root.setOnMouseDragged(event -> {
//            stage.setX(event.getScreenX() - x);
//            stage.setY(event.getScreenY() - y);
//        });
//
//        stage.setScene(sc);
//        for(Screen sn : Screen.getScreens()) {
//            Rectangle2D bounds = sn.getBounds();
//            double x = bounds.getMinX() + (bounds.getWidth() - sc.getWidth()) * 0.2;
//            double y = bounds.getMinY() + (bounds.getHeight() - sc.getHeight()) * 0.2;
//            stage.setX(x);
//            stage.setY(y);
//        }
//
//        stage.show();

        Parent root = FXMLLoader.load(getClass().getResource("fxml/startPane.fxml"));
//        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root, 1260, 720);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}