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


public class FestivalCrawler extends Crawler {


    public FestivalCrawler() {
        setOutput(new JSONArray());
            setWebLink("https://vi.wikipedia.org");
            setFolder("data/FestivalWiki.json");
            setStartLink("https://vi.wikipedia.org/wiki/L%E1%BB%85_h%E1%BB%99i_Vi%E1%BB%87t_Nam");
        }

    public String scrapeDescription(String url, String css) throws IOException{

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
                JSONObject info = new JSONObject();
                System.out.println(name);
                System.out.println(url);
                obj.put("name", name);

                String description = "";
                String startDate="";
                // Scrape the organized day
                if (table.get(i).select("td:nth-of-type(1)").text() != null)
                    startDate=table.get(i).select("td:nth-of-type(1)").text() ;
                info.put("Ngày bắt đầu(Âm lịch): ", startDate);
                // Scrape the position
                if (table.get(i).select("td:nth-of-type(2)") != null) {
                    String position = "";
                    for (Element element : table.get(i).select("td:nth-of-type(2)")) {
                        position += element.text();
                    }
                    info.put("Vị trí:",position);
                }
                String start = null;
                if (table.get(i).select("td:nth-of-type(4) > a") != null) {
                    start = table.get(i).select("td:nth-of-type(4) > a").text();
                } else {
                    if (table.get(i).select("td:nth-of-type(4)").text() != null) {
                        start = table.get(i).select("td:nth-of-type(4)").text();
                    }
                }
                if (start != null) info.put("Lần đầu tổ chức: " ,start ) ;
                // Scrape the connected character
                String connect = null;
                if (table.get(i).select("td:nth-of-type(5)").text() != null) {
                    connect = table.get(i).select("td:nth-of-type(5)").text();
                }
                if (connect != null)
//                    info.put("Url: ",url);
                info.put("Nhân vật liên quan: ",connect);
                String note=null;
                if (table.get(i).select("td:nth-of-type(6)").text() != null) {
                    note = table.get(i).select("td:nth-of-type(6)").text();}
                if(note!=null){
                    info.put("Ghi chú: ",note);
                }
                if (scrapeDescription(url, "div.mw-body-content.mw-content-ltr > div > p:first-of-type") != null)
                    description += scrapeDescription(url, "div.mw-body-content.mw-content-ltr > div > p:first-of-type");
                System.out.println(description);
                obj.put("description", description);
                obj.put("info",info);
                super.getOutput().put(obj);
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

    public void crawlAndSave() throws IOException{
        this.scrapePage(getStartLink());
        this.saveData(getFolder());
        System.out.println("Crawled " + getOutput().length() + " objects");
    }




}


