package apprunner.executedata;

import historyobject.Character;

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

public class CharacterExecData extends ExecuteData{


    public CharacterExecData(List<HistoryObject> characters) {
        this.historyObjects = characters;
    }

    public ObservableList<HistoryObject> getObservableCharacterList(List<HistoryObject> characters) {
        return FXCollections.observableArrayList(characters);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    public HistoryObject searchByName(String name) {
        for (HistoryObject character : historyObjects) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        return null;
    }
    private List<String> getHyperlinkTexts(List<HistoryObject> characters) {
        List<String> texts = new ArrayList<>();
        for (HistoryObject character : characters) {
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
    public JSONObject getInfoBoxByName(List<HistoryObject> characters, String name) {
        JSONObject info = null;
        for (HistoryObject character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                info = character.getInfo();
                return info;
            }
        }
        return info;
    }

    public List<JSONObject> getConnectionBoxByName(List<HistoryObject> characters, String name) {
        List<JSONObject> connections = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (HistoryObject character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                result.append("Connections:\n");
                connections = character.getConnection();
            }
        }
        return connections;
    }

    public List<String> getHyperTextLinksBy(int index) {
        List<String> hyperTextLinks = new ArrayList<>();
        if (index >= 0 && index < historyObjects.size()) {
            HistoryObject character = historyObjects.get(index);
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
    //    Read the final.json to scan character
    public static List<HistoryObject> loadCharacters(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = jsonData.getJSONArray("Character");

        List<HistoryObject> characters = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonCharacter = jsonArray.getJSONObject(i);

            String id = jsonCharacter.getString("id");
            String name = jsonCharacter.getString("name");
            String description = jsonCharacter.has("description") ? jsonCharacter.getString("description") : "";
            String url = jsonCharacter.has("url") ? jsonCharacter.getString("url") : null;
            String imageURL = jsonCharacter.has("imageUrl") ? jsonCharacter.getString("imageUrl") : null;
            String imageUrl = null;
            if (imageURL != null && !imageURL.startsWith("https:")){
                imageUrl = "https:" + imageURL;
            } else if (imageURL != null) {
                imageUrl = imageURL;
            }
            else if (imageURL != null ) {
                imageUrl = imageURL;
            }
            JSONObject info = jsonCharacter.getJSONObject("info");

            JSONArray jsonConnections = null;
            List<JSONObject> connections = new ArrayList<>();
            if (jsonCharacter.has("connection")){
                jsonConnections = jsonCharacter.getJSONArray("connection");
                for (int j = 0; j < jsonConnections.length(); j++) {
                    JSONObject jsonConnection = jsonConnections.getJSONObject(j);
                    connections.add(jsonConnection);
                }
            }

            HistoryObject character = new Character(name, description, url, info, connections, imageUrl);
            characters.add(character);
        }
        return characters;
    }

}

