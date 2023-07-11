package historyobject;

import org.json.JSONObject;
import java.util.List;

public class Event extends HistoryObject{

    public Event () {}

    public Event(String name, String description, String url, JSONObject info,  List<JSONObject> connection, String imageUrl) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
        this.imageUrl = imageUrl;
    }
}
