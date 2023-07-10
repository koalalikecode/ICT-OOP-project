package apprunner.executedata;

import historyobject.HistoryObject;
import historyobject.Place;

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

public class PlaceExecData extends ExecuteData{


    public PlaceExecData(List<HistoryObject> places) {
        this.historyObjects = places;
    }

    public ObservableList<HistoryObject> getObservablePlaceList(List<HistoryObject> places) {
        return FXCollections.observableArrayList(places);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    public HistoryObject searchByName(String name) {
        for (HistoryObject place : historyObjects) {
            if (place.getName().equalsIgnoreCase(name)) {
                return place;
            }
        }
        return null;
    }
    private List<String> getHyperlinkTexts(List<Place> places) {
        List<String> texts = new ArrayList<>();
        for (Place place : places) {
            JSONObject info = place.getInfo();
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
    public JSONObject getInfoBoxByName(List<HistoryObject> places, String name) {
        JSONObject info = null;
        for ( HistoryObject place : places) {
            if (place.getName().equalsIgnoreCase(name)) {
                info = place.getInfo();
                return info;
            }
        }
        return info;
    }

    public List<JSONObject> getConnectionBoxByName(List<HistoryObject> places, String name) {
        List<JSONObject> connections = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (HistoryObject place : places) {
            if (place.getName().equalsIgnoreCase(name)) {
                result.append("Connections:\n");
                connections = place.getConnection();
            }
        }
        return connections;
    }

    public List<String> getHyperTextLinksBy(int index) {
        List<String> hyperTextLinks = new ArrayList<>();
        if (index >= 0 && index < historyObjects.size()) {
            HistoryObject place = historyObjects.get(index);
            JSONObject info = place.getInfo();
            if (info != null) {
                for (String key : info.keySet()) {
                    JSONObject value = info.getJSONObject(key);
                    if (value.has("name")) {
                        String hyperlinkText = key + ": " + value.getString("name");
                        hyperTextLinks.add(hyperlinkText);
                    }
                }
            }
        }
        return hyperTextLinks;
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
    //    Read the final.json to scan place
    public static List<HistoryObject> loadPlaces(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = jsonData.getJSONArray("Place");

        List<HistoryObject> places = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonPlace = jsonArray.getJSONObject(i);

            String id = jsonPlace.getString("id");
            String name = jsonPlace.getString("name");
            String description = jsonPlace.has("description") ? jsonPlace.getString("description") : "";
            String url = jsonPlace.has("url") ? jsonPlace.getString("url") : null;
            String imageURL = jsonPlace.has("imageUrl") ? jsonPlace.getString("imageUrl") : null;
            String imageUrl = null;
            if (imageURL != null){
                imageUrl = imageURL.replace("\\","/");
            }
            JSONObject info = jsonPlace.getJSONObject("info");

            JSONArray jsonConnections = null;
            List<JSONObject> connections = new ArrayList<>();
            if (jsonPlace.has("connection")){
                jsonConnections = jsonPlace.getJSONArray("connection");
                for (int j = 0; j < jsonConnections.length(); j++) {
                    JSONObject jsonConnection = jsonConnections.getJSONObject(j);
                    connections.add(jsonConnection);
                }
            }

            HistoryObject place = new Place(name, description, url, info, connections, imageUrl);
            places.add(place);
        }
        return places;
    }

    public String listDataByName(String name) {
        StringBuilder result = new StringBuilder();
        HistoryObject place = searchByName(name);
        result.append("Name: ").append(place.getName()).append("\n");
        result.append("Description: ").append(place.getDescription()).append("\n");
        result.append("URL: ").append(place.getUrl()).append("\n");

        return result.toString();
    }
}


