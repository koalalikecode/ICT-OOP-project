package historyobject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Dynasty {
    private String name;
    private String start;
    private String end;
    private List<String> kings;
    private String capital;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<String> getKings() {
        return kings;
    }

    public void setKings(List<String> kings) {
        this.kings = kings;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public void loadField(JSONObject jsonObject) {
        JSONParser parser = new JSONParser();
        String strKings = new JSONArray(kings).toString();
        JSONArray jsonKings = new JSONArray();
        try {
            jsonKings = new JSONArray(strKings);
        } catch (Exception e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }
        jsonObject.put("tên", getName());
        jsonObject.put("bắt đầu", start);
        jsonObject.put("kết thúc", end);
        jsonObject.put("vua", jsonKings);
        jsonObject.put("thủ đô", capital);
    }

    public Dynasty(JSONObject jsonObj) {
        if (jsonObj.get("tên") != null)
            setName((String) jsonObj.get("tên"));
        else setName(null);

        setName((String) jsonObj.get("tên"));

        if (jsonObj.get("bắt đầu") != null)
            start = (String) jsonObj.get("bắt đầu");
        else start = null;
        if (jsonObj.get("kết thúc") != null)
            end = (String) jsonObj.get("kết thúc");
        else end = null;
        if (jsonObj.get("thủ đô") != null)
            capital = (String) jsonObj.get("thủ đô");
        else capital = null;
        if (jsonObj.get("vua") != null) {
            JSONArray arr = (JSONArray) jsonObj.get("vua");
            if (arr != null) {
                kings = new ArrayList<>();
                for (int i = 0; i < arr.length(); ++i) {
                    kings.add((String) arr.get(i));
                }
            }
        } else {
            kings = null;
        }
    }

    public Dynasty() {
        setName(null);
        this.start = null;
        this.end = null;
        this.kings = null;
        this.capital = null;
    }
}
