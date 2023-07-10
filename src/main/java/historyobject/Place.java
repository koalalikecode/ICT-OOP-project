package historyobject;

import org.json.JSONObject;

import java.util.List;

public class Place extends HistoryObject{

    public Place () {}

    public Place(String name, String description, String url, JSONObject info,  List<JSONObject> connection, String imageUrl) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
        this.imageUrl = imageUrl;
    }
}
