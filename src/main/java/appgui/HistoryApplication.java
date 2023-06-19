package appgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class HistoryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Parent root sẽ là trang đầu tiên hiện ra mỗi khi ta chạy chương trình
        // Scene scene = new Scene(root);
        FXMLLoader fxmlLoader = new FXMLLoader(HistoryApplication.class.getResource("test.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setTitle("History!");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }
}
