package historyobject;

import org.json.JSONObject;

import java.util.List;

public class Dynasty extends HistoryObject {
    public Dynasty() {
    }
    public Dynasty(String name,  String description, String url, JSONObject info, List<JSONObject> connection) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
    }
}
