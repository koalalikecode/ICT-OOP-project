package historyobject;

import org.json.JSONObject;

import java.util.List;

public class Character extends HistoryObject{

    public Character() {}
    public Character(String name,String description, String url, JSONObject info, List<JSONObject> connection) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
    }
}