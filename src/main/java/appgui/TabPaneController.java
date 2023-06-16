package appgui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class TabPaneController {
    @FXML
    private AnchorPane content;

    @FXML
    protected void setText() {
        Label text = new Label();
        text.setText("hello World");
        content.getChildren().add(text);
    }
}
