/* Việc cần làm khi sửa code :
 * 1. Tối ưu exception handling trong file
 * 2.
 */


package appgui.Controller;

import appgui.ExecuteData.DynastyExecData;
import historyobject.Dynasty;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DynastyController implements Initializable {
    private final String dataJson = "data/final.json";
    private JSONObject dynastyInfoBox;
    private List<JSONObject> dynastyConnectionBox;

    //    Menu Buttons
    @FXML
    private Button btnCharacter;
    @FXML
    private Button btnDynasty;
    @FXML
    private Button btnEvent;
    @FXML
    private Button btnFestival;
    @FXML
    private Button btnPlace;


    //    Scenes
    private Scene sceneCharacter;
    private Scene sceneDynasty;
    private Scene sceneEvent;
    private Scene sceneFestival;
    private Scene scenePlace;



    //    Search Dynasty
    @FXML
    private TextField searchDynasty;

    @FXML
    private ScrollPane infoScrollPane;
    @FXML
    private Label labelName;
    @FXML
    private AnchorPane infoAnchorPane;

    //    TableView for Dynasty in All Dynasty Tab
    @FXML
    private TableView<Dynasty> tbvDynastys;
    @FXML
    private TableColumn<Dynasty, String> tbcName;
    private ObservableList<Dynasty> dataDynasty = FXCollections.observableArrayList();
    private List<Dynasty> dynastyList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            dynastyList = DynastyExecData.loadDynastys(dataJson);
            DynastyExecData execDataDynasty = new DynastyExecData(dynastyList);
            tbcName.setCellValueFactory(new PropertyValueFactory<Dynasty, String>("name"));
            dataDynasty = FXCollections.observableArrayList(dynastyList);
            tbvDynastys.setItems(dataDynasty);

            searchDynasty.setOnKeyReleased(event -> searchDynasty());

            LinkController.selectedDynasty = execDataDynasty.searchByName(LinkController.selectedDynastyName);

//            Initialize selected object every Controller
            if (LinkController.selectedDynastyName == null){
                LinkController.selectedDynasty = dynastyList.get(0);
                displaySelectionInfo(LinkController.selectedDynasty, execDataDynasty);
                selectCellByValue(LinkController.selectedDynasty.getName());
            } else if (LinkController.selectedDynastyName != null) {
                displaySelectionInfo(LinkController.selectedDynasty, execDataDynasty);
                selectCellByValue(LinkController.selectedDynasty.getName());
            }

            tbvDynastys.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataDynasty);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSelectionInfo(Dynasty dynastySelection, DynastyExecData execDataDynasty) {
        if (dynastySelection != null) {
            displaySelectionInfo(dynastySelection, execDataDynasty);
        }
    }
    private void displaySelectionInfo(Dynasty dynastySelection, DynastyExecData execDataDynasty) {
        labelName.setText("" + dynastySelection.getName());
        infoAnchorPane.getChildren().clear();

        TextFlow textFlow = new TextFlow();
        String dynastyDescription = dynastySelection.getDescription();

        textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
        textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());
        Text text = new Text(dynastyDescription);
        textFlow.getChildren().add(text);
        textFlow.getChildren().add(new Text("\n"));

        Text infoStart = new Text("\nThông tin chi tiết của " + dynastySelection.getName() + ":");
        infoStart.setFont(new Font(16));
        textFlow.getChildren().add(infoStart);
        infoAnchorPane.getChildren().add(textFlow);

        dynastyInfoBox = execDataDynasty.getInfoBoxByName(dynastyList, dynastySelection.getName());
        dynastyConnectionBox = execDataDynasty.getConnectionBoxByName(dynastyList, dynastySelection.getName());

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);


        for (String key : dynastyInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);
            JSONObject value = dynastyInfoBox.getJSONObject("Lịch sử");

            Label infoKey = new Label(key + ": ");
            infoItem.getChildren().add(infoKey);
            if (value.has("name") && value.has("url")) {
                String fieldName = execDataDynasty.dataSearchField(value.getString("name"));
                String sceneName = sceneFromField(fieldName);
                Hyperlink link = new Hyperlink(value.getString("name"));
                link.setWrapText(true);
                link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                link.setOnAction(event -> {
                    try {
                        LinkController.setSelectedObject(link.getText(), fieldName);
                        Stage stage = (Stage) link.getScene().getWindow();
                        System.out.println(sceneName);
                        Parent root = FXMLLoader.load(getClass().getResource(sceneName));
                        Scene newScene = new Scene(root);
                        stage.setScene(newScene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                infoItem.getChildren().add(link);
            } else if(value.has("name")) {
                Label link = new Label(value.getString("name"));
                link.setWrapText(true);
                link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                infoItem.getChildren().add(link);
            }
            contentContainer.getChildren().add(infoItem);
        }
        Text connectionStart = new Text("Xem thêm");
        connectionStart.setFont(new Font(16));
        contentContainer.getChildren().add(connectionStart);

//      Add the connections of the Dynasty
        if (dynastyConnectionBox != null) {
            if (!dynastyConnectionBox.isEmpty()) {
                for (JSONObject connection : dynastyConnectionBox) {
                    HBox infoItem = new HBox();
                    infoItem.setPrefHeight(0);
                    infoItem.setAlignment(Pos.CENTER_LEFT);
                    String connectionName = connection.getString("name");
                    String connectionUrl = connection.getString("url");

                    Label infoKey = new Label("Tên : ");
                    infoItem.getChildren().add(infoKey);
                    if (connectionName != null && connectionUrl != null) {
                        Hyperlink link = new Hyperlink(connectionName);
                        String fieldName = execDataDynasty.dataSearchField(connectionName);
                        String sceneName = sceneFromField(fieldName);
                        link.setOnAction(event -> {
                            try {
                                LinkController.setSelectedObject(link.getText(), fieldName);
                                Stage stage = (Stage) link.getScene().getWindow();
                                System.out.println(sceneName);
                                Parent root = FXMLLoader.load(getClass().getResource(sceneName));
                                Scene newScene = new Scene(root);
                                stage.setScene(newScene);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        infoItem.getChildren().add(link);
                    } else if (connectionName != null) {
                        Label link = new Label(connectionName);
                        infoItem.getChildren().add(link);
                    }
                    contentContainer.getChildren().add(infoItem);
                }
            }
        }
        infoAnchorPane.getChildren().add(contentContainer);
        infoScrollPane.setContent(infoAnchorPane);
    }

    private String sceneFromField(String name){
        String sceneName;
        if (name.equals("Character")){
            sceneName = "characterPane.fxml";
        } else if (name.equals("Dynasty")){
            sceneName = "dynastyPane.fxml";
        } else if (name.equals("Event")){
            sceneName = "eventPane.fxml";
        } else if (name.equals("Festival")){
            sceneName = "festivalPane.fxml";
        } else if (name.equals("Place")){
            sceneName = "placePane.fxml";
        } else {
            sceneName = "dynastyPane.fxml";
        }
        return sceneName;
    }

    //    Make tableview show selected row by hyperlink
    public void selectCellByValue(String targetValue) {
        for (int row = 0; row < tbvDynastys.getItems().size(); row++) {
            String cellValue = tbcName.getCellData(row);
            if (cellValue.equals(targetValue)) {
                tbvDynastys.getSelectionModel().select(row, tbcName);
                tbvDynastys.scrollTo(row);
                break;
            }
        }
    }

    @FXML
    private void addSceneSwitchingHandler(ActionEvent event) {
        Stage stage = (Stage) btnDynasty.getScene().getWindow();
        try {
            if (event.getSource() == btnEvent) {
                Parent newPane = FXMLLoader.load(getClass().getResource("eventPane.fxml"));
                Scene newScene = new Scene(newPane);
                stage.setScene(newScene);
            } else if (event.getSource() == btnCharacter) {
                Parent newPane2 = FXMLLoader.load(getClass().getResource("characterPane.fxml"));
                Scene newScene2 = new Scene(newPane2);
                stage.setScene(newScene2);
            } else if (event.getSource() == btnDynasty) {
                Parent newPane3 = FXMLLoader.load(getClass().getResource("dynastyPane.fxml"));
                Scene newScene3 = new Scene(newPane3);
                stage.setScene(newScene3);
            } else if (event.getSource() == btnFestival) {
                Parent newPane4 = FXMLLoader.load(getClass().getResource("festivalPane.fxml"));
                Scene newScene4 = new Scene(newPane4);
                stage.setScene(newScene4);
            } else if (event.getSource() == btnPlace) {
                Parent newPane5 = FXMLLoader.load(getClass().getResource("placePane.fxml"));
                Scene newScene5 = new Scene(newPane5);
                stage.setScene(newScene5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchDynasty() {
        String searchQuery = searchDynasty.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            tbvDynastys.setItems(dataDynasty);
        } else {
            List<Dynasty> searchResults = dynastyList.stream()
                    .filter(dynasty -> dynasty.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            tbvDynastys.setItems(FXCollections.observableArrayList(searchResults));
        }
    }

}