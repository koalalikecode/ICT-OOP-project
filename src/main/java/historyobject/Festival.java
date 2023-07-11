package historyobject;

import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

public class Festival extends HistoryObject{
    private String connect;

    public Festival() {
        info = new JSONObject();
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

    public Festival(String name, JSONObject info, String description, String imageUrl) {
        this.name = name;
        this.info = info;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}