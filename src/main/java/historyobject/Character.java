package historyobject;

import org.json.JSONObject;

import java.util.List;

public class Character {
    private String name;
    private String url;
    private JSONObject info;
    private String description;
    private List<JSONObject> connection;

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