package historyobject;

import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

public class FestivalWiki {
    private String name;
    private String url;
    private JSONObject info;
    private String description;
    private List<JSONObject> connection;
    private String imageUrl;
    public FestivalWiki() {
        info = new JSONObject();
    }

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
    public Collection<?> toJSONObject() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}