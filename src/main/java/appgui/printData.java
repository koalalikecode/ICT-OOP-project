package appgui;

import historyobject.CharacterTest;

import org.json.JSONArray;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

public class printData {
    public static String printdata() {
        String data = "An Dương Vương (chữ Hán: 安陽王), tên thật là Thục Phán (蜀泮), là một vị vua đã lập nên đất nước Âu Lạc và cũng là vị vua duy nhất cai trị nhà nước này. Âu Lạc là nhà nước thứ hai trong lịch sử Việt Nam sau nhà nước Văn Lang đầu tiên của các vua Hùng.\n\n\n\n\n\n\n\n\n\n\n\nLinebreaghghjghk";
        return data;
    }
    private String dataJson = "data/final.json";
    private List<CharacterTest> characters;
    private List<String> hyperlinkTexts;

    public List<CharacterTest> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterTest> characters) {
        this.characters = characters;
    }

    public void setHyperlinkTexts(List<String> hyperlinkTexts) {
        this.hyperlinkTexts = hyperlinkTexts;
    }

    public printData() {
    }
    public printData(List<CharacterTest> characters) {
        this.characters = characters;
        this.hyperlinkTexts = getHyperlinkTexts(characters);
    }

    public ObservableList<CharacterTest> getObservableCharacterList(List<CharacterTest> characters) {
        return FXCollections.observableArrayList(characters);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    private List<String> getHyperlinkTexts(List<CharacterTest> characters) {
        List<String> texts = new ArrayList<>();
        for (CharacterTest character : characters) {
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
    public List<String> getHyperTextLinksByName(List<CharacterTest> characters, String name) {
        List<String> hyperTextLinks = new ArrayList<>();
        for (CharacterTest character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
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
        }
        return hyperTextLinks;
    }
    public List<String> getHyperTextLinksBy(int index) {
        List<String> hyperTextLinks = new ArrayList<>();
        if (index >= 0 && index < characters.size()) {
            CharacterTest character = characters.get(index);
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
            CharacterTest character = characters.get(i);
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
    public static List<CharacterTest> loadCharacters(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = jsonData.getJSONArray("Character");

        List<CharacterTest> characters = new ArrayList<>();

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

            CharacterTest character = new CharacterTest(name, description, url, info, connections);
            characters.add(character);
        }
        return characters;
    }

//    public void searchByName(String name) {
//        for (CharacterTest character : characters) {
//            if (character.getName().equalsIgnoreCase(name)) {
//                System.out.println("Character Name: " + character.getName());
//                System.out.println("Description: " + character.getDescription());
//                System.out.println("Info:");
//                JSONObject info = character.getInfo();
//                if (info != null) {
//                    for (String key : info.keySet()) {
//                        JSONObject value = info.getJSONObject(key);
//                        if (value.has("name")) {
//                            System.out.println(key + ": " + value.getString("name"));
//                        }
//                    }
//                }
//                System.out.println("Connections:");
//                List<JSONObject> connections = character.getConnection();
//                if (connections != null) {
//                    for (JSONObject connection : connections) {
//                        System.out.println("Name: " + connection.getString("name"));
//                        System.out.println("URL: " + connection.getString("url"));
//                        System.out.println();
//                    }
//                }
//                return;
//            }
//        }
//        System.out.println("Character with name '" + name + "' not found.");
//    }
    public String searchByName(String name) {
        StringBuilder result = new StringBuilder();

        for (CharacterTest character : characters) {
            if (character.getName().equalsIgnoreCase(name)) {
                result.append("Name: ").append(character.getName()).append("\n");
                result.append("Description: ").append(character.getDescription()).append("\n");
                result.append("URL: ").append(character.getUrl()).append("\n");

                JSONObject info = character.getInfo();
                for (String key : info.keySet()) {
                    JSONObject infoData = info.getJSONObject(key);
                    result.append(key).append(": ").append(infoData.getString("name")).append("\n");
                }

                result.append("Connections:\n");
                List<JSONObject> connections = character.getConnection();
                for (JSONObject connection : connections) {
                    result.append("  Name: ").append(connection.getString("name")).append("\n");
                    result.append("  URL: ").append(connection.getString("url")).append("\n");
                }

                break;
            }
        }
        return result.toString();
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
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
    public String dataSearch(String name) {
        StringBuilder result = new StringBuilder();
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(dataJson)));
            JSONObject jsonData = new JSONObject(jsonContent);
            // Get all jsonArray names
            Set<String> jsonArrayNames = jsonData.keySet();
            // Search in all jsonArray objects
            for (String jsonArrayName : jsonArrayNames) {
                JSONArray jsonArray = jsonData.getJSONArray(jsonArrayName);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String objectName = jsonObject.getString("name");
                    if (objectName.equalsIgnoreCase(name)) {
                        result.append("Field: " + jsonArrayName).append("\n");
                        result.append("Name: ").append(objectName).append("\n");
                        result.append("Description: ").append(jsonObject.getString("description")).append("\n");
                        result.append("URL: ").append(jsonObject.getString("url")).append("\n");

                        if (jsonObject.has("info")) {
                            JSONObject infoObject = jsonObject.getJSONObject("info");
                            result.append("Info:\n");
                            for (String key : infoObject.keySet()) {
                                JSONObject infoData = infoObject.getJSONObject(key);
                                result.append(key).append(": ").append(infoData.getString("name")).append("\n");
                            }
                        }

                        if (jsonObject.has("connection")) {
                            JSONArray connectionArray = jsonObject.getJSONArray("connection");
                            result.append("Connections:\n");
                            for (int j = 0; j < connectionArray.length(); j++) {
                                JSONObject connectionObject = connectionArray.getJSONObject(j);
                                String connectionName = connectionObject.getString("name");
                                String connectionUrl = connectionObject.getString("url");
                                result.append(connectionName).append(": ").append(connectionUrl).append("\n");
                            }
                        }
                        result.append("\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public static void main (String[] args){
        try {
            List<CharacterTest> characterList = new ArrayList<>();
            characterList = loadCharacters("data/final.json");

//            Create an instance of PrintData
            printData class1 = new printData(characterList);

//            SearchByName for info of the character
            String searchName = "Âu Cơ";
//            String searchResult = class1.searchByName(searchName);
//            System.out.println("\nSearch Result for Name: " + searchName);
//            System.out.println(searchResult);
//            String searchResult = class1.dataSearch(searchName);
//            System.out.println("Search Result for \"" + searchName + "\":");
//            System.out.println(searchResult);

//            Print the characters and hyperlink texts
            class1.printCharacters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


