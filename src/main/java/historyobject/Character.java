package historyobject;

import org.json.JSONObject;

import java.util.List;

public class Character {
    private String id;
    private String name;
    private String url;
    private JSONObject info;
    private String description;
    private List<JSONObject> connection;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Character() {}
    public Character(String name,String description, String url, JSONObject info, List<JSONObject> connection, String imageUrl) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
        this.imageUrl = imageUrl;
    }

    public Character(String name, String description, String url, JSONObject info,  List<JSONObject> connection) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
    }

    public JSONObject getInfo() {
        return info;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public List<JSONObject> getConnection() {
        return connection;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConnection(List<JSONObject> connection) {
        this.connection = connection;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}