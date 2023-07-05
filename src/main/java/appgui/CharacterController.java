package appgui;

import historyobject.HistoryObject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CharacterController extends Controller {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            objectList = ExecData.loadHistoryObject(dataJson,TypeHistoryObject.Character);
            execDataCharacter = new CharacterExecData(objectList);
            super.initialize(url,resourceBundle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}