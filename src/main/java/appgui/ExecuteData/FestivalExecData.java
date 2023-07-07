package appgui.ExecuteData;

import historyobject.Festival;

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

public class FestivalExecData {
    private String dataJson = "data/final.json";
    private List<Festival> festivals;
    private List<String> hyperlinkTexts;

    public List<Festival> getFestivals() {
        return festivals;
    }

    public void setFestivals(List<Festival> festivals) {
        this.festivals = festivals;
    }

    public void setHyperlinkTexts(List<String> hyperlinkTexts) {
        this.hyperlinkTexts = hyperlinkTexts;
    }


    public FestivalExecData(List<Festival> festivals) {
        this.festivals = festivals;
//        this.hyperlinkTexts = getHyperlinkTexts(festivals);
    }

    public ObservableList<Festival> getObservableFestivalList(List<Festival> festivals) {
        return FXCollections.observableArrayList(festivals);
    }

    public List<String> getHyperlinkTexts() {
        return hyperlinkTexts;
    }

    public Festival searchByName(String name) {
        for (Festival festival : festivals) {
            if (festival.getName().equalsIgnoreCase(name)) {
                return festival;
            }
        }
        return null;
    }
    private List<String> getHyperlinkTexts(List<Festival> festivals) {
        List<String> texts = new ArrayList<>();
        for (Festival festival : festivals) {
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
    public JSONObject getInfoBoxByName(List<Festival> festivals, String name) {
        JSONObject info = null;
        for (Festival festival : festivals) {
            if (festival.getName().equalsIgnoreCase(name)) {
                info = festival.getInfo();
                return info;
            }
        }
        return info;
    }


    public int indexByName(String name){
        for(int i = 0; i < festivals.size(); i++){
            if(festivals.get(i).getName().equalsIgnoreCase(name)){
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
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
    //    Read the final.json to scan festival
    public static List<Festival> loadFestivals(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonArray = jsonData.getJSONArray("Festival");

        List<Festival> festivals = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonFestival = jsonArray.getJSONObject(i);

            String id = jsonFestival.getString("id");
            String name = jsonFestival.getString("name");
            String description = jsonFestival.getString("description");
            JSONObject info = jsonFestival.getJSONObject("info");
            Festival festival = new Festival(name, info, description);
            festivals.add(festival);


        }
        return festivals;
    }

    public String listDataByName(String name) {
        StringBuilder result = new StringBuilder();
        Festival festival = searchByName(name);
        result.append("Name: ").append(festival.getName()).append("\n");
        result.append("Description: ").append(festival.getDescription()).append("\n");
        result.append("URL: ").append(festival.getUrl()).append("\n");

        return result.toString();
    }
}


