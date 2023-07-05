package historyobject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceNKS extends HistoryObject {
    public PlaceNKS() {}
    public PlaceNKS(String name,String description, String url, JSONObject info, List<JSONObject> connection) {
        this.name = name;
        this.url = url;
        this.info = info;
        this.description = description;
        this.connection = connection;
    }
    public static List<String> linkList = new ArrayList<>();
    public static List<String> getLinkList() {
        linkList.add("/di-tich-lich-su?types[0]=1");
        linkList.add("/di-tich-lich-su?types[0]=1&start=10");
        linkList.add("/di-tich-lich-su?types[0]=1&start=20");
        return linkList;
    }
}
