/* Việc cần làm khi sửa code :
 * 1. Tối ưu exception handling trong file
 * 2.
 */


package appgui.Controller;

import apprunner.ExecuteData.FestivalExecData;
import historyobject.Festival;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FestivalController  extends Controller{

    //    TableView for Festival in All Festival Tab
    @FXML
    private TableView<Festival> tbvFestivals;
    @FXML
    private TableColumn<Festival, String> tbcName;
    private ObservableList<Festival> dataFestival = FXCollections.observableArrayList();
    private List<Festival> festivalList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            festivalList = FestivalExecData.loadFestivals(dataJson);
            FestivalExecData execDataFestival = new FestivalExecData(festivalList);
            tbcName.setCellValueFactory(new PropertyValueFactory<Festival, String>("name"));
            dataFestival = FXCollections.observableArrayList(festivalList);
            tbvFestivals.setItems(dataFestival);

            search.setOnKeyReleased(event -> searchFestival());

            LinkController.selectedFestival = execDataFestival.searchByName(LinkController.selectedFestivalName);

//            Initialize selected object every Controller
            if (LinkController.selectedFestivalName == null){
                LinkController.selectedFestival = festivalList.get(0);
                displaySelectionInfo(LinkController.selectedFestival, execDataFestival);
                selectCellByValue(LinkController.selectedFestival.getName());
            } else if (LinkController.selectedFestivalName != null) {
                displaySelectionInfo(LinkController.selectedFestival, execDataFestival);
                selectCellByValue(LinkController.selectedFestival.getName());
            }

            tbvFestivals.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataFestival);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSelectionInfo(Festival festivalSelection, FestivalExecData execDataFestival) {
        if (festivalSelection != null) {
            displaySelectionInfo(festivalSelection, execDataFestival);
        }
    }
    private void displaySelectionInfo(Festival festivalSelection, FestivalExecData execDataFestival) {
        labelName.setText("" + festivalSelection.getName());
        infoAnchorPane.getChildren().clear();

        TextFlow textFlow = new TextFlow();

        if (festivalSelection.getImageUrl() != null){
            ImageView imageView = new ImageView();
            Image image = new Image(festivalSelection.getImageUrl(), true);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(infoAnchorPane.getPrefWidth() - 20 );
            imageView.setImage(image);

            textFlow.setTextAlignment(TextAlignment.CENTER);
            textFlow.getChildren().add(imageView);
        }

        String festivalDescription = festivalSelection.getDescription();

        textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
        textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());
        Text text = new Text("\n"+festivalDescription);
        textFlow.getChildren().add(text);
        textFlow.getChildren().add(new Text("\n"));

        Text infoStart = new Text("\nThông tin chi tiết của " + festivalSelection.getName() + ":");
        infoStart.setFont(new Font(16));
        textFlow.getChildren().add(infoStart);
        infoAnchorPane.getChildren().add(textFlow);

        objectInfoBox = execDataFestival.getInfoBoxByName(festivalList, festivalSelection.getName());

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);

        for (String key : objectInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);

            Object valueObject = objectInfoBox.get(key);

            Label infoKey = new Label("\u2023 " + key + ": ");
            infoItem.getChildren().add(infoKey);
            if (valueObject instanceof JSONArray){
                JSONArray valueArray = (JSONArray) valueObject;
                for (int i = 0; i < valueArray.length(); i++) {
                    JSONObject valueObjectFromArray = valueArray.getJSONObject(i);
                    String fieldName = execDataFestival.dataSearchField(valueObjectFromArray.getString("name"));
                    String sceneName = sceneFromField(fieldName);
                    Hyperlink link = new Hyperlink(valueObjectFromArray.getString("name"));
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
                }
            } else {
                String value = objectInfoBox.getString(key);
                if (value != null) {
                    String fieldName = execDataFestival.dataSearchField(value);
                    String sceneName = sceneFromField(fieldName);
                    Hyperlink link = new Hyperlink(value);
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
                }
            }
            contentContainer.getChildren().add(infoItem);
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
            sceneName = "fxml/festivalPane.fxml";
        }
        return sceneName;
    }

    //    Make tableview show selected row by hyperlink
    public void selectCellByValue(String targetValue) {
        for (int row = 0; row < tbvFestivals.getItems().size(); row++) {
            String cellValue = tbcName.getCellData(row);
            if (cellValue.equals(targetValue)) {
                tbvFestivals.getSelectionModel().select(row, tbcName);
                tbvFestivals.scrollTo(row);
                break;
            }
        }
    }

    @FXML
    private void addSceneSwitchingHandler(ActionEvent event) {
        Stage stage = (Stage) btnEvent.getScene().getWindow();
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

    private void searchFestival() {
        String searchQuery = search.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            tbvFestivals.setItems(dataFestival);
        } else {
            List<Festival> searchResults = festivalList.stream()
                    .filter(festival -> festival.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            tbvFestivals.setItems(FXCollections.observableArrayList(searchResults));
        }
    }

}