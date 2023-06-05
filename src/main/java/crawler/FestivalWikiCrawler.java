package crawler;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

public class FestivalWikiCrawler extends Crawlers {
    public FestivalWikiCrawler() {
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/FestivalWiki.json");
        setStartLink("https://vi.wikipedia.org/wiki/L%E1%BB%85_h%E1%BB%99i_Vi%E1%BB%87t_Nam");
    }
    @Override
    public void scrapePage(String pageToString) {
        Document doc;
        try {
            doc = Jsoup.connect(pageToString).userAgent("Jsoup client").timeout(20000).get();
            Elements table = doc.select("table.prettytable.wikitable > tbody > tr");
            for (int i = 1; i < table.size(); i++) {
                JSONObject obj = new JSONObject();
                Elements names = table.get(i).select("td:nth-of-type(3)");
                String name = "";
                for (Element element : names) name += element.text();
                String url = table.get(i).select("td:nth-of-type(3) > a").attr("href");
                url = super.getWebLink() + url;
                String connection="";
                System.out.println(name);
                System.out.println(url);
                obj.put("name", name);

                String description = "";

                // Scrape the organized day
                if (table.get(i).select("td:nth-of-type(1)").text() != null)
                    description += "Ngày bắt đầu (âm lịch): " + table.get(i).select("td:nth-of-type(1)").text() + "   ";
                // Scrape the position
                if (table.get(i).select("td:nth-of-type(2)") != null) {
                    String position = "";
                    for (Element element : table.get(i).select("td:nth-of-type(2)")) {
                        position += element.text();
                    }
                    description += "Vị trí: " + position + "   ";
                }
                // Scrape the first time
                String start = null;
                if (table.get(i).select("td:nth-of-type(4) > a") != null) {
                    start = table.get(i).select("td:nth-of-type(4) > a").text();
                } else {
                    if (table.get(i).select("td:nth-of-type(4)").text() != null) {
                        start = table.get(i).select("td:nth-of-type(4)").text();
                    }
                }
                if (start != null) description += "Lần đầu tổ chức: " + start + "   " ;
                else{description += "Lần đầu tổ chức: " + "Chưa rõ"+ "  ";}
                // Scrape the connected character
                String connect = null;
                if (table.get(i).select("td:nth-of-type(5)").text() != null) {
                    connect = table.get(i).select("td:nth-of-type(5)").text();
                }
                if (connect != null)
                    connection+=  "url:   "+url +"    ";

                    connection += "Nhân vật liên quan: " + connect + "   ";
                if (scrapeInformation(url, "div.mw-body-content.mw-content-ltr > div > p:first-of-type") != null)
                    description += scrapeInformation(url, "div.mw-body-content.mw-content-ltr > div > p:first-of-type");
                System.out.println(description);
                obj.put("description", description);
                obj.put("connection", connection);
                super.getOutput().add(obj);
            }
        } catch (UnknownHostException e){
            try {
                throw new UnknownHostException("");
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void saveData(String folder) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(getFolder());

            // Tạo một JSONArray mới để lưu trữ dữ liệu
            JSONArray jsonArray = new JSONArray(getOutput().toString());

            // Ghi dữ liệu từ JSONArray vào file
            fileWriter.write(jsonArray.toString(4));
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }
    public String scrapeInformation(String url, String css) throws IOException{

        String info = "";
        Document doc;
        try {
            doc = Jsoup.connect(url).userAgent("Jsoup client").timeout(20000).get();
            Elements text = doc.select(css);
            for (Element element:text){
                info += element.text() + '\n';
            }
        }  catch (IOException e){
            e.printStackTrace();
        }
        return info;
    }
    public String scrapeInformation(List<String> urls, String css) throws IOException{
        String info = "";
        Document doc;
        try {
            for (String url:urls){
                info += scrapeInformation(url,css);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return info;
    }
}

class Crawlers {
    private String webLink;
    private org.json.simple.JSONArray output = new org.json.simple.JSONArray();
    private String startLink;
    private String folder;
    private int pageLimit;
    public String getWebLink() {
        return webLink;
    }
    public void setWebLink(String root) {
        this.webLink = root;
    }
    public String getStartLink() {
        return startLink;
    }
    public void setStartLink(String start) {
        this.startLink = start;
    }
    public String getFolder() {
        return folder;
    }
    public void setFolder(String folder) {
        this.folder = folder;
    }
    public org.json.simple.JSONArray getOutput() {
        return output;
    }
    public void scrapePage(String start) throws  IOException{
        return;
    }
    public String scrapeInformation(String url, String css) throws IOException{

        String info = "";
        Document doc;
        try {
            doc = Jsoup.connect(url).userAgent("Jsoup client").timeout(20000).get();
            Elements text = doc.select(css);
            for (Element element:text){
                info += element.text() + '\n';
            }
        }  catch (IOException e){
            e.printStackTrace();
        }
        return info;
    }
    public String scrapeInformation(List<String> urls, String css) throws IOException{
        String info = "";
        Document doc;
        try {
            for (String url:urls){
                info += scrapeInformation(url,css);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return info;
    }

    public void saveData(String folder) throws IOException {
        try (FileWriter file = new FileWriter(folder)){
            file.write(output.toJSONString());
            file.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void crawlAndSave() throws IOException{
        this.scrapePage(startLink);
        this.saveData(folder);
        System.out.println("Crawled " + output.size() + " objects");
    }
}
