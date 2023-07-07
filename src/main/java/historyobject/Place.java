package historyobject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Place {
    private String name;
    private String url;
    private JSONObject info;
    private String description;
    private List<JSONObject> connection;
    public static List<String> linkList = new ArrayList<>();
    public static List<String> getLinkList() {
        linkList.add("/di-tich-lich-su?types[0]=1");
        linkList.add("/di-tich-lich-su?types[0]=1&start=10");
        linkList.add("/di-tich-lich-su?types[0]=1&start=20");
        return linkList;
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

    public Place (){

    }
    public Place(String name, String description, String url, JSONObject info,  List<JSONObject> connection) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
    }
}
