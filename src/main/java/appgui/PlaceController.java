/* Việc cần làm khi sửa code :
 * 1. Tối ưu exception handling trong file
 * 2.
 */


package appgui;

import apprunner.executeData.PlaceExecData;
import historyobject.Place;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PlaceController extends Controller {

    //    TableView for Place in All Place Tab
    @FXML
    private TableView<Place> tbvPlaces;
    @FXML
    private TableColumn<Place, String> tbcName;
    private ObservableList<Place> dataPlace = FXCollections.observableArrayList();
    private List<Place> placeList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            placeList = PlaceExecData.loadPlaces(dataJson);
            PlaceExecData execDataPlace = new PlaceExecData(placeList);
            tbcName.setCellValueFactory(new PropertyValueFactory<Place, String>("name"));
            dataPlace = FXCollections.observableArrayList(placeList);
            tbvPlaces.setItems(dataPlace);

            search.setOnKeyReleased(event -> searchPlace());

            LinkController.selectedPlace = execDataPlace.searchByName(LinkController.selectedPlaceName);

//            Initialize selected object every Controller
            if (LinkController.selectedPlaceName == null){
                LinkController.selectedPlace = placeList.get(0);
                displaySelectionInfo(LinkController.selectedPlace, execDataPlace);
                selectCellByValue(LinkController.selectedPlace.getName());
            } else if (LinkController.selectedPlaceName != null) {
                displaySelectionInfo(LinkController.selectedPlace, execDataPlace);
                selectCellByValue(LinkController.selectedPlace.getName());
            }

            tbvPlaces.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataPlace);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSelectionInfo(Place placeSelection, PlaceExecData execDataPlace) {
        if (placeSelection != null) {
            displaySelectionInfo(placeSelection, execDataPlace);
        }
    }
    private void displaySelectionInfo(Place placeSelection, PlaceExecData execDataPlace) {
        labelName.setText("" + placeSelection.getName());
        infoAnchorPane.getChildren().clear();

        TextFlow textFlow = new TextFlow();

//        Add image view
        if (placeSelection.getImageUrl() != null){
            ImageView imageView = new ImageView();
            Image image = new Image(placeSelection.getImageUrl(), true);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(infoAnchorPane.getPrefWidth() - 20 );
            imageView.setImage(image);

            textFlow.setTextAlignment(TextAlignment.CENTER);
            textFlow.getChildren().add(imageView);
        }


        String placeDescription = placeSelection.getDescription();

        textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
        textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());
        Text text = new Text("\n" + placeDescription);
        textFlow.getChildren().add(text);
        textFlow.getChildren().add(new Text("\n"));

        Text infoStart = new Text("\nThông tin chi tiết của " + placeSelection.getName() + ":");
        infoStart.setFont(new Font(16));
        textFlow.getChildren().add(infoStart);
        infoAnchorPane.getChildren().add(textFlow);

        objectInfoBox = execDataPlace.getInfoBoxByName(placeList, placeSelection.getName());
        connectionBox = execDataPlace.getConnectionBoxByName(placeList, placeSelection.getName());

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);


        for (String key : objectInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);
            Object Value = objectInfoBox.get(key);

            Label infoKey = new Label("\u2023 " + key + ": ");
            infoItem.getChildren().add(infoKey);
            if (Value instanceof JSONObject){
                JSONObject value = (JSONObject) Value;
                if (value.has("name") && value.has("url")) {
                    String fieldName = execDataPlace.dataSearchField(value.getString("name"));
                    String sceneName = sceneFromField(fieldName);
                    Hyperlink link = new Hyperlink(value.getString("name"));
                    link.setWrapText(true);
                    link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                    link.setOnAction(event -> {
                        try {
                            LinkController.setSelectedObject(link.getText(), fieldName);
                            Stage stage = (Stage) link.getScene().getWindow();
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
            } else {
                Label infoValue = new Label(Value.toString());
                infoValue.setWrapText(true);
                infoValue.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                infoItem.getChildren().add(infoValue);
            }

            contentContainer.getChildren().add(infoItem);
        }
        Text connectionStart = new Text("Xem thêm");
        connectionStart.setFont(new Font(16));
        contentContainer.getChildren().add(connectionStart);

//      Add the connections of the Place
        if (connectionBox != null) {
            if (!connectionBox.isEmpty()) {
                for (JSONObject connection : connectionBox) {
                    HBox infoItem = new HBox();
                    infoItem.setPrefHeight(0);
                    infoItem.setAlignment(Pos.CENTER_LEFT);
                    String connectionName = connection.getString("name");
                    String connectionUrl = connection.getString("url");

                    Label infoKey = new Label("Tên : ");
                    infoItem.getChildren().add(infoKey);
                    if (connectionName != null && connectionUrl != null) {
                        Hyperlink link = new Hyperlink(connectionName);
                        String fieldName = execDataPlace.dataSearchField(connectionName);
                        String sceneName = sceneFromField(fieldName);
                        link.setOnAction(event -> {
                            try {
                                LinkController.setSelectedObject(link.getText(), fieldName);
                                Stage stage = (Stage) link.getScene().getWindow();
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
            sceneName = "fxml/characterPane.fxml";
        } else if (name.equals("Dynasty")){
            sceneName = "fxml/dynastyPane.fxml";
        } else if (name.equals("Event")){
            sceneName = "fxml/eventPane.fxml";
        } else if (name.equals("Festival")){
            sceneName = "fxml/festivalPane.fxml";
        } else if (name.equals("Place")){
            sceneName = "fxml/placePane.fxml";
        } else {
            sceneName = "fxml/placePane.fxml";
        }
        return sceneName;
    }

    //    Make tableview show selected row by hyperlink
    public void selectCellByValue(String targetValue) {
        for (int row = 0; row < tbvPlaces.getItems().size(); row++) {
            String cellValue = tbcName.getCellData(row);
            if (cellValue.equals(targetValue)) {
                tbvPlaces.getSelectionModel().select(row, tbcName);
                tbvPlaces.scrollTo(row);
                break;
            }
        }
    }

    @FXML
    private void addSceneSwitchingHandler(ActionEvent event) {
        Stage stage = (Stage) btnPlace.getScene().getWindow();
        try {
            if (event.getSource() == btnEvent) {
                Parent newPane = FXMLLoader.load(getClass().getResource("fxml/eventPane.fxml"));
                Scene newScene = new Scene(newPane);
                stage.setScene(newScene);
            } else if (event.getSource() == btnCharacter) {
                Parent newPane2 = FXMLLoader.load(getClass().getResource("fxml/characterPane.fxml"));
                Scene newScene2 = new Scene(newPane2);
                stage.setScene(newScene2);
            } else if (event.getSource() == btnDynasty) {
                Parent newPane3 = FXMLLoader.load(getClass().getResource("fxml/dynastyPane.fxml"));
                Scene newScene3 = new Scene(newPane3);
                stage.setScene(newScene3);
            } else if (event.getSource() == btnFestival) {
                Parent newPane4 = FXMLLoader.load(getClass().getResource("fxml/festivalPane.fxml"));
                Scene newScene4 = new Scene(newPane4);
                stage.setScene(newScene4);
            } else if (event.getSource() == btnPlace) {
                Parent newPane5 = FXMLLoader.load(getClass().getResource("fxml/placePane.fxml"));
                Scene newScene5 = new Scene(newPane5);
                stage.setScene(newScene5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchPlace() {
        String searchQuery = search.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            tbvPlaces.setItems(dataPlace);
        } else {
            List<Place> searchResults = placeList.stream()
                    .filter(place -> place.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            tbvPlaces.setItems(FXCollections.observableArrayList(searchResults));
        }
    }

}