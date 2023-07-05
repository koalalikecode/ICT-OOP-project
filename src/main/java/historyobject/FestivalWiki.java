package historyobject;

import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

public class FestivalWiki extends HistoryObject{

    private String connect;
    public FestivalWiki() {
        info = new JSONObject();
    }
    public FestivalWiki(String name,String description, String url, JSONObject info, List<JSONObject> connection) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
    }
    public String getConnect() {
        return connect;
    }
    public void setConnect(String connect) {
        this.connect = connect;
    }
    public Collection<?> toJSONObject() {
        // TODO Auto-generated method stub
        return null;
    }

}