package appgui;

import historyobject.Character;

import historyobject.Dynasty;
import historyobject.Event;
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

public class ExecData {
    protected String dataJson;
    protected List<HistoryObject> historyObjectList;
    protected List<String> hyperlinkTexts;
    public static List<HistoryObject> loadHistoryObject(String filePath, TypeHistoryObject typeHistoryObject) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = new JSONArray();
        if (typeHistoryObject == TypeHistoryObject.Character) { jsonArray = jsonData.getJSONArray("Character");}
        else if (typeHistoryObject == TypeHistoryObject.Event) {jsonArray = jsonData.getJSONArray("Event");}
        else if (typeHistoryObject == TypeHistoryObject.Dynasty) {jsonArray = jsonData.getJSONArray("Dynasty");}
        List<HistoryObject> historyObjects = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonCharacter = jsonArray.getJSONObject(i);

            String id = jsonCharacter.getString("id");
            String name = jsonCharacter.getString("name");
            String description = jsonCharacter.getString("description");
            String url = jsonCharacter.getString("url");
            JSONObject info = jsonCharacter.getJSONObject("info");
            JSONArray jsonConnections = jsonCharacter.getJSONArray("connection");

            List<JSONObject> connections = new ArrayList<>();
            for (int j = 0; j < jsonConnections.length(); j++) {
                JSONObject jsonConnection = jsonConnections.getJSONObject(j);
                connections.add(jsonConnection);
            }
            if (typeHistoryObject == TypeHistoryObject.Character) {
                HistoryObject character = new Character(name, description, url, info, connections);
                historyObjects.add(character);
            } else if (typeHistoryObject == TypeHistoryObject.Event) {

                HistoryObject character = new Event(name, description, url, info, connections);
                historyObjects.add(character);
            }
            else if (typeHistoryObject == TypeHistoryObject.Dynasty) {
                HistoryObject character = new Dynasty(name, description, url, info, connections);
                historyObjects.add(character);
            }
        }
        return historyObjects;
    }

    public List<HistoryObject> getHistoryObjectList() {
        return historyObjectList;
    }

    public void setHistoryObjectList(List<HistoryObject> historyObjectList) {
        this.historyObjectList = historyObjectList;
    }

    public void setHyperlinkTexts(List<String> hyperlinkTexts) {
        this.hyperlinkTexts = hyperlinkTexts;
    }




    public ObservableList<HistoryObject> getObservableCharacterList(List<HistoryObject> characters) {
        return FXCollections.observableArrayList(characters);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    public HistoryObject searchByName(String name) {
        for (HistoryObject character : historyObjectList) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        return null;
    }
    protected List<String> getHyperlinkTexts(List<HistoryObject> historyObjects) {
        List<String> texts = new ArrayList<>();
        for (HistoryObject character : historyObjects) {
            JSONObject info = character.getInfo();
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
    public JSONObject getInfoBoxByName(List<HistoryObject> historyObjects, String name) {
        JSONObject info = null;
        for (HistoryObject character : historyObjects) {
            if (character.getName().equalsIgnoreCase(name)) {
                info = character.getInfo();
               return info;
            }
        }
        return info;
    }

    public List<JSONObject> getConnectionBoxByName(List<HistoryObject> historyObjects, String name) {
        List<JSONObject> connections = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (HistoryObject character : historyObjects) {
            if (character.getName().equalsIgnoreCase(name)) {
                result.append("Connections:\n");
                connections = character.getConnection();
            }
        }
        return connections;
    }

    public List<String> getHyperTextLinksBy(int index) {
        List<String> hyperTextLinks = new ArrayList<>();
        if (index >= 0 && index < historyObjectList.size()) {
            HistoryObject character = historyObjectList.get(index);
            JSONObject info = character.getInfo();
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
    public void printCharacters() {
        for (int i = 0; i < historyObjectList.size(); i++) {
            HistoryObject character = historyObjectList.get(i);
            System.out.println("====================================================");
            System.out.println("Character " + (i + 1));
            System.out.println("Character Name: " + character.getName());

            System.out.println("Info:");
            JSONObject info = character.getInfo();
            if (info != null) {
                for (String key : info.keySet()) {
                    JSONObject value = info.getJSONObject(key);
                    if (value.has("name")) {
                        System.out.println(key + ": " + value.getString("name"));
                    }
                }
            }

            System.out.println();
        }
    }

    public int indexByName(String name){
        for(int i = 0; i < historyObjectList.size(); i++){
            if(historyObjectList.get(i).getName().equalsIgnoreCase(name)){
                return i;
            }
        }
        return -1;
    }

    public String dataSearchField(String name, String fileName) {
        dataJson = fileName;
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
                        return result.toString();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
//    Read the final.json to scan character


    public String listDataByName(String name) {
        StringBuilder result = new StringBuilder();
        HistoryObject character = searchByName(name);
        result.append("Name: ").append(character.getName()).append("\n");
        result.append("Description: ").append(character.getDescription()).append("\n");
        result.append("URL: ").append(character.getUrl()).append("\n");

        return result.toString();
    }

}
enum TypeHistoryObject {
    Character,
    Event,
    Dynasty
}


