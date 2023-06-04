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
public class FestivalWikiCrawler extends Crawler {
    public FestivalWikiCrawler() {
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/FestivalWiki.json");
        setStartLink("https://vi.wikipedia.org/wiki/L%E1%BB%85_h%E1%BB%99i_Vi%E1%BB%87t_Nam");
    }

    @Override
    public void scrapePage(String pageToString) throws IOException {
        Document doc;
        try {
            doc = Jsoup.connect(pageToString).userAgent("Jsoup client").timeout(20000).get();
            Elements table = doc.select("table.prettytable.wikitable > tbody > tr");
            for (int i = 1; i < table.size(); i++) {
                JSONObject obj = new JSONObject();
                Elements names = table.get(i).select("td:nth-of-type(3)");
                String name = "";
                for (Element element : names) name += element.text();
                String id = table.get(i).select("td:nth-of-type(3) > a").attr("href");
                id = super.getWebLink() + id;
                System.out.println(name);
                System.out.println(id);
                obj.put("name", name);
                obj.put("id", id);
                String description = "";
                // Scrape the organized day
                if (table.get(i).select("td:nth-of-type(1)").text() != null)
                    description += "Ngày bắt đầu (âm lịch): " + table.get(i).select("td:nth-of-type(1)").text() + "\n";
                // Scrape the position
                if (table.get(i).select("td:nth-of-type(2)") != null) {
                    String position = "";
                    for (Element element : table.get(i).select("td:nth-of-type(2)")) {
                        position += element.text();
                    }
                    description += "Vị trí: " + position + "\n";
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
                if (start != null) description += "Lần đầu tổ chức: " + start + "\n";
                // Scrape the connected character
                String connect = null;
                if (table.get(i).select("td:nth-of-type(5)").text() != null) {
                    connect = table.get(i).select("td:nth-of-type(5)").text();
                }
                if (connect != null) description += "Nhân vật liên quan: " + connect + "\n";
                if (scrapeInformation(id, "div.mw-body-content.mw-content-ltr > div > p:first-of-type") != null)
                    description += scrapeInformation(id, "div.mw-body-content.mw-content-ltr > div > p:first-of-type");
                System.out.println(description);
                obj.put("info", description);
                super.getOutput().add(obj);
            }
        } catch (UnknownHostException e){
            throw new UnknownHostException("Turn on your Internet please");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
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

    public static void main(String[] args) throws  Exception{
        FestivalWikiCrawler f = new FestivalWikiCrawler();
        f.crawlAndSave();
    }
}