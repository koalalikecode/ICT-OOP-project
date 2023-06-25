package historyobject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

import java.util.List;

public class CharacterTest {
    private String id;
    private StringProperty Name;
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
//    public StringProperty nameProperty() {
//        return new SimpleStringProperty(name);
//    }

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

    public CharacterTest(String name,String description, String url, JSONObject info, List<JSONObject> connection) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
    }

    public CharacterTest(String name) {
        this.name = name;
    }
}