package apprunner.executedata;

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

public class EventExecData extends ExecuteData{

    public EventExecData(List<HistoryObject> events) {
        this.historyObjects = events;
    }

    public ObservableList<HistoryObject> getObservableEventList(List<HistoryObject> events) {
        return FXCollections.observableArrayList(events);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    public HistoryObject searchByName(String name) {
        for (HistoryObject event : historyObjects) {
            if (event.getName().equalsIgnoreCase(name)) {
                return event;
            }
        }
        return null;
    }

    public JSONObject getInfoBoxByName(List<HistoryObject> events, String name) {
        JSONObject info = null;
        for (HistoryObject event : events) {
            if (event.getName().equalsIgnoreCase(name)) {
                info = event.getInfo();
                return info;
            }
        }
        return info;
    }

    public List<JSONObject> getConnectionBoxByName(List<HistoryObject> events, String name) {
        List<JSONObject> connections = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (HistoryObject event : events) {
            if (event.getName().equalsIgnoreCase(name)) {
                result.append("Connections:\n");
                connections = event.getConnection();
            }
        }
        return connections;
    }

    public List<String> getHyperTextLinksBy(int index) {
        List<String> hyperTextLinks = new ArrayList<>();
        if (index >= 0 && index < historyObjects.size()) {
            HistoryObject event = historyObjects.get(index);
            JSONObject info = event.getInfo();
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
    public void printEvents() {
        for (int i = 0; i < historyObjects.size(); i++) {
            HistoryObject event = historyObjects.get(i);
            System.out.println("====================================================");
            System.out.println("Event " + (i + 1));
            System.out.println("Event Name: " + event.getName());

            System.out.println("Info:");
            JSONObject info = event.getInfo();
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

    //    Read the final.json to scan event
    public static List<HistoryObject> loadEvents(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = jsonData.getJSONArray("Event");

        List<HistoryObject> events = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonEvent = jsonArray.getJSONObject(i);

            String id = jsonEvent.getString("id");
            String name = jsonEvent.getString("name");
            String description = jsonEvent.has("description") ? jsonEvent.getString("description") : "";
            String url = jsonEvent.has("url") ? jsonEvent.getString("url") : null;
            String imageURL = jsonEvent.has("imageUrl") ? jsonEvent.getString("imageUrl") : null;
            JSONObject info = jsonEvent.getJSONObject("info");

            JSONArray jsonConnections = null;
            List<JSONObject> connections = new ArrayList<>();
            if (jsonEvent.has("connection")){
                jsonConnections = jsonEvent.getJSONArray("connection");
                for (int j = 0; j < jsonConnections.length(); j++) {
                    JSONObject jsonConnection = jsonConnections.getJSONObject(j);
                    connections.add(jsonConnection);
                }
            }

            HistoryObject event = new Event(name, description, url, info, connections, imageURL);
            events.add(event);
        }
        return events;
    }

    public String listDataByName(String name) {
        StringBuilder result = new StringBuilder();
        HistoryObject event = searchByName(name);
        result.append("Name: ").append(event.getName()).append("\n");
        result.append("Description: ").append(event.getDescription()).append("\n");
        result.append("URL: ").append(event.getUrl()).append("\n");

        return result.toString();
    }
}

