package apprunner.executedata;

import historyobject.Festival;

import historyobject.HistoryObject;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.Set;

public class FestivalExecData extends ExecuteData{


        public FestivalExecData(List<HistoryObject> festivals) {
                this.historyObjects = festivals;
        }

        public ObservableList<HistoryObject> getObservableFestivalList(List<HistoryObject> festivals) {
                return FXCollections.observableArrayList(festivals);
        }

        public List<String> getHyperlinkTexts() {
                return hyperlinkTexts;
        }

        public HistoryObject searchByName(String name) {
                for (HistoryObject festival : historyObjects) {
                        if (festival.getName().equalsIgnoreCase(name)) {
                                return festival;
                        }
                }
                return null;
        }
        private List<String> getHyperlinkTexts(List<HistoryObject> festivals) {
                List<String> texts = new ArrayList<>();
                for (HistoryObject festival : festivals) {
                        JSONObject info = festival.getInfo();
                        if (info != null) {
                                for (String key : info.keySet()) {
                                        JSONObject value = info.getJSONObject(key);
                                        if (value.has("name")) {
                                                texts.add(value.getString("name"));
                                        }
                                }
                        }
                }
                return texts;
        }
        public JSONObject getInfoBoxByName(List<HistoryObject> festivals, String name) {
                JSONObject info = null;
                for (HistoryObject festival : festivals) {
                        if (festival.getName().equalsIgnoreCase(name)) {
                                info = festival.getInfo();
                                return info;
                        }
                }
                return info;
        }


        public int indexByName(String name){
                for(int i = 0; i < historyObjects.size(); i++){
                        if(historyObjects.get(i).getName().equalsIgnoreCase(name)){
                                return i;
                        }
                }
                return -1;
        }

        public String dataSearchField(String name) {
                StringBuilder result = new StringBuilder();
                try {
                        String jsonContent = new String(Files.readAllBytes(Paths.get(dataJson)));
                        JSONObject jsonData = new JSONObject(jsonContent);
                        Set<String> jsonArrayNames = jsonData.keySet();
                        for (String jsonArrayName : jsonArrayNames) {
                                JSONArray jsonArray = jsonData.getJSONArray(jsonArrayName);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String objectName = jsonObject.getString("name");
                                        if (objectName.equalsIgnoreCase(name)) {
                                                result.append(jsonArrayName);
                                                break;
                                        }
                                }
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }

                return result.toString();
        }
        //    Read the final.json to scan festival
        public static List<HistoryObject> loadFestivals(String filePath) throws IOException {
                String json = new String(Files.readAllBytes(Paths.get(filePath)));
                JSONObject jsonData = new JSONObject(json);
                JSONArray jsonArray = jsonData.getJSONArray("Festival");

                List<HistoryObject> festivals = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonFestival = jsonArray.getJSONObject(i);

                        String id = jsonFestival.getString("id");
                        String name = jsonFestival.getString("name");
                        String description = jsonFestival.getString("description");
                        JSONObject info = jsonFestival.getJSONObject("info");
                        String imageURL = jsonFestival.has("imageUrl") ? jsonFestival.getString("imageUrl") : null;
                        String imageUrl = null;
                        if (imageURL != null ) {
                                imageUrl = "https:" + imageURL;
                        }
                        HistoryObject festival = new Festival(name, info, description, imageUrl);
                        festivals.add(festival);

                }
                return festivals;
        }

        public String listDataByName(String name) {
                StringBuilder result = new StringBuilder();
                HistoryObject festival = searchByName(name);
                result.append("Name: ").append(festival.getName()).append("\n");
                result.append("Description: ").append(festival.getDescription()).append("\n");
                result.append("URL: ").append(festival.getUrl()).append("\n");

                return result.toString();
        }
}


