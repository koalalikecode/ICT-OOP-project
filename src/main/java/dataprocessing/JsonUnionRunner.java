package dataprocessing;

public class JsonUnionRunner {
    public static void main(String[] args) {
        JsonFileUnion runner = new JsonFileUnion("processed_data/final.json");
        runner.jsonUnion();
    }

}
