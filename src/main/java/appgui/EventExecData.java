package appgui;

import historyobject.Character;

import historyobject.CharacterTest;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

public class EventExecData {
    private String dataJson = "data/final.json";
    private List<Character> characters;
    private List<String> hyperlinkTexts;

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public void setHyperlinkTexts(List<String> hyperlinkTexts) {
        this.hyperlinkTexts = hyperlinkTexts;
    }


    public EventExecData(List<Character> characters) {
        this.characters = characters;
        this.hyperlinkTexts = getHyperlinkTexts(characters);
    }

    public ObservableList<Character> getObservableCharacterList(List<Character> characters) {
        return FXCollections.observableArrayList(characters);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    public Character searchByName(String name) {
        for (Character character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        return null;
    }

    private List<String> getHyperlinkTexts(List<Character> characters) {
        List<String> texts = new ArrayList<>();
        for (Character character : characters) {
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
    public JSONObject getInfoBoxByName(List<Character> characters, String name) {
        JSONObject info = null;
        for (Character character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                info = character.getInfo();
                return info;
            }
        }
        return info;
    }

    public List<JSONObject> getConnectionBoxByName(List<Character> characters, String name) {
        List<JSONObject> connections = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (Character character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                result.append("Connections:\n");
                connections = character.getConnection();
            }
        }
        return connections;
    }

    public List<String> getHyperTextLinksBy(int index) {
        List<String> hyperTextLinks = new ArrayList<>();
        if (index >= 0 && index < characters.size()) {
            Character character = characters.get(index);
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
        for (int i = 0; i < characters.size(); i++) {
            Character character = characters.get(i);
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
    //    Read the final.json to scan character
    public static List<Character> loadCharacters(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = jsonData.getJSONArray("Character");

        List<Character> characters = new ArrayList<>();

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

            Character character = new Character(name, description, url, info, connections);
            characters.add(character);
        }
        return characters;
    }

    public String listDataByName(String name) {
        StringBuilder result = new StringBuilder();
        Character character = searchByName(name);
        result.append("Name: ").append(character.getName()).append("\n");
        result.append("Description: ").append(character.getDescription()).append("\n");
        result.append("URL: ").append(character.getUrl()).append("\n");

        return result.toString();
    }
}


