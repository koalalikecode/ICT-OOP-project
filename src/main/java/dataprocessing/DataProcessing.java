package dataprocessing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataProcessing {
    private JSONArray output; // Kết quả sau khi processing data
    private String firstUrl; // Trang json thứ nhất để process
    private String secondUrl; // Trang json thứ 2 để process
    private String outputFile; // File lưu kết quả
    private String id_prefix; // Tiền tố cho id từng object

    private int increment = 0;

    public DataProcessing(String firstUrl, String outputFile, String id_prefix) {
        this.firstUrl = firstUrl;
        this.outputFile = outputFile;
        this.id_prefix = id_prefix;
    }

    public DataProcessing(String firstUrl, String secondUrl, String outputFile, String id_prefix) {
        this.firstUrl = firstUrl;
        this.secondUrl = secondUrl;
        this.outputFile = outputFile;
        this.id_prefix = id_prefix;
    }

    public void setOutput(JSONArray output) {
        this.output = output;
    }

    public JSONArray getOutput() {
        return output;
    }

    // Hàm để tìm kiếm một Object trong file Json có giá trị name được cung cấp không
    // Giá trị trả về là JSONObject nếu tìm thấy, null nếu không
    public JSONObject searchObjectByName(String url, String name) {
        File file = new File(url);
        try
        {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            //Read JSON file
            JSONArray objectList = new JSONArray(content);
            for(int i = 0; i < objectList.length(); i++) {
                JSONObject item = objectList.optJSONObject(i);
                if (item.get("name").equals(name)) return item;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm để xử lý dữ liệu 1 file trong trường hợp không cần hợp nhất 2 file
    public void dataIterate() {
        JSONArray objectList;
        List<JSONObject> result = new ArrayList<>();

        try
        {
            // Đọc file và lưu vào biến kiểu String
            String content1 = new String(Files.readAllBytes(Paths.get(new File(firstUrl).toURI())));

            objectList = new JSONArray(content1);

            // Hợp nhất info của từng object
            for(int i = 0; i < objectList.length(); i++) {
                JSONObject item = objectList.getJSONObject(i);
                increment++;
                item.put("id", id_prefix + increment);
                result.add(item);
            }
            setOutput(new JSONArray(result));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // Hàm để hợp nhất 2 file
    public void dataUnion() {
        JSONArray objectList1;
        JSONArray objectList2;
        List<JSONObject> result = new ArrayList<>();

        try
        {
            // Đọc file và lưu vào biến kiểu String
            String content1 = new String(Files.readAllBytes(Paths.get(new File(firstUrl).toURI())));
            String content2 = new String(Files.readAllBytes(Paths.get(new File(secondUrl).toURI())));

            // Lấy mảng object từ 2 file
            objectList1 = new JSONArray(content1);
            objectList2 = new JSONArray(content2);

            // Hợp nhất info của từng object
            for(int i = 0; i < objectList1.length(); i++) {
                JSONObject item = objectList1.getJSONObject(i);
                JSONObject searchObject = searchObjectByName(secondUrl, (String) item.get("name"));

                if (searchObject != null) {
                    JSONObject searchObjectInfo = searchObject.getJSONObject("info");
                    JSONObject itemObjectInfo = item.getJSONObject("info");
                    for(Object key : searchObjectInfo.keySet()) {
                        String keyStr = (String) key;
                        if (itemObjectInfo.optJSONObject(keyStr) == null) {
                            itemObjectInfo.put(keyStr, searchObjectInfo.get(keyStr));
                        }
                    }
                }
                increment++;
                item.put("id", id_prefix + increment);
                result.add(item);
            }

            // Hợp nhất những object từ file 2 mà file 1 chưa có
            for(int i = 0; i < objectList2.length(); i++) {
                JSONObject item = (JSONObject) objectList2.get(i);
                JSONObject searchObject = searchObjectByName(firstUrl, (String) item.get("name"));

                if (searchObject == null) {
                    increment++;
                    item.put("id", id_prefix + increment);
                    result.add(item);
                }
            }
            setOutput(new JSONArray(result));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void processAndSave() {
        if (secondUrl != null) dataUnion();
        else dataIterate();

        FileWriter file = null;
        try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            file = new FileWriter(outputFile);
            file.write(output.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        DataProcessing b = new DataProcessing("data/characterNKS.json", "processed_data/character.json", "c");
        b.processAndSave();
    }
}
