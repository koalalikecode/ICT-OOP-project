package appgui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Test2 extends Application {

    private TableView<Character> tableView;
    private ObservableList<Character> data;
    private TableColumn<Character, String> tbcName;

    @Override
    public void start(Stage primaryStage) {
        // Create TableView and data
        tableView = new TableView<>();
        data = FXCollections.observableArrayList(
                new Character("Cuong1"),
                new Character("Cuong2"),
                new Character("Cuong3"),
                new Character("Cuong4")
        );

        // Create column
        tbcName = new TableColumn<>("Name");
        tbcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().add(tbcName);

        // Set data to TableView
        tableView.setItems(data);

        // Select cell by value
        selectCellByValue("Cuong2");

        VBox root = new VBox(tableView);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.setTitle("TableView Selection Example");
        primaryStage.show();
    }

    public void selectCellByValue(String targetValue) {
        for (int row = 0; row < tableView.getItems().size(); row++) {
            String cellValue = tbcName.getCellData(row);
            if (cellValue.equals(targetValue)) {
                tableView.getSelectionModel().select(row, tbcName);
                tableView.scrollTo(row);
                break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Character {
        private String name;

        public Character(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
