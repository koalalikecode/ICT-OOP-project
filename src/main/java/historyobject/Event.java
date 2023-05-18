package historyobject;

import org.json.JSONObject;
import java.util.List;

public class Event {
    private String name;
    private String url;
    private JSONObject info;
    private String description;
    private List<JSONObject> connection;
    private String connect;
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
    public String getConnect() {
        return connect;
    }
    public void setConnect(String connect) {
        this.connect = connect;
    }  
    

}
