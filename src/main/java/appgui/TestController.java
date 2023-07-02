package appgui;

import historyobject.Character;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TestController implements Initializable {
    private String dataJson = "data/final.json";
    private JSONObject characterInfoBox;

    @FXML
    private ScrollPane infoScrollPane;
    @FXML
    private Pane infoPane;
    @FXML
    private Pane hyperlinkPane;
    @FXML
    private Label labelName;
    @FXML
    private AnchorPane infoAnchorPane;

    // TableView for Character in All Character Tab
    @FXML
    private TableView<Character> tbvCharacters;
    @FXML
    private TableColumn<Character, String> tbcName;

    private ObservableList<Character> dataCharacter = FXCollections.observableArrayList();
    private List<Character> characterList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            characterList = printData.loadCharacters(dataJson);
            printData execDataCharacter = new printData(characterList);
            tbcName.setCellValueFactory(new PropertyValueFactory<Character, String>("name"));
            dataCharacter = FXCollections.observableArrayList(characterList);
            tbvCharacters.setItems(
                    dataCharacter
            );

            for (Character character : characterList) {
                dataCharacter.add(character);
            }

            tbvCharacters.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    labelName.setText("" + newSelection.getName());
                    infoAnchorPane.getChildren().clear();

                    TextFlow textFlow = new TextFlow();
                    String characterDescription = newSelection.getDescription();

                    textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
                    textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());
                    Text text = new Text(characterDescription);
                    textFlow.getChildren().add(text);
                    textFlow.getChildren().add(new Text("\n"));

                    Text infoStart = new Text("\nThông tin chi tiết của " + newSelection.getName());
                    textFlow.getChildren().add(infoStart);
                    infoAnchorPane.getChildren().add(textFlow);

                    characterInfoBox = execDataCharacter.getInfoBoxByName(characterList, newSelection.getName());

                    VBox contentContainer = new VBox(10);
                    contentContainer.setPadding(new Insets(10));
                    contentContainer.getChildren().add(textFlow);

                    for (String key : characterInfoBox.keySet()) {
                        HBox infoItem = new HBox();
                        infoItem.setPrefHeight(0);
                        infoItem.setPrefHeight(0);
                        infoItem.setAlignment(Pos.CENTER_LEFT);
                        JSONObject value = characterInfoBox.getJSONObject(key);
                        Label infoKey = new Label(key + ": ");
                        infoItem.getChildren().add(infoKey);
                        if (value.has("name") && value.has("url")) {
                            Hyperlink link = new Hyperlink(value.getString("name"));
                            link.setWrapText(true);
                            link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                            infoItem.getChildren().add(link);
                        } else if(value.has("name")) {
                            Label link = new Label(value.getString("name"));
                            link.setWrapText(true);
                            link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                            infoItem.getChildren().add(link);
                        }
                        contentContainer.getChildren().add(infoItem);
                    }

                    infoAnchorPane.getChildren().add(contentContainer);

                    infoScrollPane.setContent(infoAnchorPane);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public TextFlow linebreak(String data, TextFlow textFlow) {
        String[] lines = data.split("\\n");
        for (String line : lines) {
            Text text = new Text(line);
            textFlow.getChildren().add(text);
            textFlow.getChildren().add(new Text("\n"));
        }
        return textFlow;
    }
}
