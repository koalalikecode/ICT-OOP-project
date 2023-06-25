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
public class JsonFileUnion {
    private String outputPath;

    public JsonFileUnion(String outputPath){
        this.outputPath = outputPath;
    }

    public void setOutputPath(String outputPath){ this.outputPath = outputPath; }
    public String getOutputPath(){ return outputPath; }
    protected JSONArray getJson(String path){
        JSONArray output = null;
        try{
            String JsonFile = new String(Files.readAllBytes(Paths.get(new File(path).toURI())));
            output = new JSONArray(JsonFile);
        }catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.println("Can't get Json file!");
        }
        return output;
    }

    public void jsonUnion(){
        JSONObject unionfile = new JSONObject();

        unionfile.put("Character", getJson("processed_data/character.json"));
        unionfile.put("Dynasty", getJson("processed_data/dynasty.json"));
        unionfile.put("Event", getJson("processed_data/event.json"));
        unionfile.put("Festival", getJson("processed_data/festival.json"));
        unionfile.put("Place", getJson("processed_data/place.json"));

        FileWriter file = null;
        try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            file = new FileWriter(outputPath);
            file.write(unionfile.toString(1));
            System.out.println("Successfully union!");
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
}
