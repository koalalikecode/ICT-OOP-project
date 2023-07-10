package historyobject;

import org.json.JSONObject;

import java.util.List;

public class HistoryObject {
    protected String name;
    protected String url;
    protected JSONObject info;
    protected String description;
    protected List<JSONObject> connection;
    protected String imageUrl;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject getInfo() {
        return info;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JSONObject> getConnection() {
        return connection;
    }

    public void setConnection(List<JSONObject> connection) {
        this.connection = connection;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
