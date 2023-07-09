package apprunner.ExecuteData;

import historyobject.Dynasty;

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

public class DynastyExecData {
    private String dataJson = "processed_data/final.json";
    private List<Dynasty> dynasties;
    private List<String> hyperlinkTexts;

    public List<Dynasty> getDynasties() {
        return dynasties;
    }

    public void setDynasties(List<Dynasty> dynasties) {
        this.dynasties = dynasties;
    }

    public void setHyperlinkTexts(List<String> hyperlinkTexts) {
        this.hyperlinkTexts = hyperlinkTexts;
    }


    public DynastyExecData(List<Dynasty> dynasties) {
        this.dynasties = dynasties;
//        this.hyperlinkTexts = getHyperlinkTexts(dynasties);
    }

    public ObservableList<Dynasty> getObservableDynastyList(List<Dynasty> dynasties) {
        return FXCollections.observableArrayList(dynasties);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    public Dynasty searchByName(String name) {
        for (Dynasty dynasty : dynasties) {
            if (dynasty.getName().equalsIgnoreCase(name)) {
                return dynasty;
            }
        }
        return null;
    }
    private List<String> getHyperlinkTexts(List<Dynasty> dynasties) {
        List<String> texts = new ArrayList<>();
        for (Dynasty dynasty : dynasties) {
            JSONObject info = dynasty.getInfo();
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
    public JSONObject getInfoBoxByName(List<Dynasty> dynasties, String name) {
        JSONObject info = null;
        for (Dynasty dynasty : dynasties) {
            if (dynasty.getName().equalsIgnoreCase(name)) {
                info = dynasty.getInfo();
                return info;
            }
        }
        return info;
    }

    public List<JSONObject> getConnectionBoxByName(List<Dynasty> dynasties, String name) {
        List<JSONObject> connections = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (Dynasty dynasty : dynasties) {
            if (dynasty.getName().equalsIgnoreCase(name)) {
                result.append("Connections:\n");
                connections = dynasty.getConnection();
            }
        }
        return connections;
    }

    public List<String> getHyperTextLinksBy(int index) {
        List<String> hyperTextLinks = new ArrayList<>();
        if (index >= 0 && index < dynasties.size()) {
            Dynasty dynasty = dynasties.get(index);
            JSONObject info = dynasty.getInfo();
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
        for(int i = 0; i < dynasties.size(); i++){
            if(dynasties.get(i).getName().equalsIgnoreCase(name)){
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
    //    Read the final.json to scan dynasty
    public static List<Dynasty> loadDynasties(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = jsonData.getJSONArray("Dynasty");

        List<Dynasty> dynasties = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonDynasty = jsonArray.getJSONObject(i);

            String id = jsonDynasty.getString("id");
            String name = jsonDynasty.getString("name");
            String description = jsonDynasty.has("description") ? jsonDynasty.getString("description") : "";
            String url = jsonDynasty.has("url") ? jsonDynasty.getString("url") : null;
            JSONObject info = jsonDynasty.getJSONObject("info");

            JSONArray jsonConnections = null;
            List<JSONObject> connections = new ArrayList<>();
            if (jsonDynasty.has("connection")){
                jsonConnections = jsonDynasty.getJSONArray("connection");
                for (int j = 0; j < jsonConnections.length(); j++) {
                    JSONObject jsonConnection = jsonConnections.getJSONObject(j);
                    connections.add(jsonConnection);
                }
            }

            Dynasty dynasty = new Dynasty(name, description, url, info, connections);
            dynasties.add(dynasty);
        }
        return dynasties;
    }

    public String listDataByName(String name) {
        StringBuilder result = new StringBuilder();
        Dynasty dynasty = searchByName(name);
        result.append("Name: ").append(dynasty.getName()).append("\n");
        result.append("Description: ").append(dynasty.getDescription()).append("\n");
        result.append("URL: ").append(dynasty.getUrl()).append("\n");

        return result.toString();
    }
}


