package crawler;

import historyobject.PlaceNKS;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp dùng để thu thập di tích lịch sử trên Wikipedia
 * @since: 15/01/2023
 */

public class PlaceWikiCrawler3 extends Crawler{
    public PlaceWikiCrawler3() {
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/PlaceWiki2.json");
        setStartLink("https://vi.wikipedia.org/wiki/Danh_sách_Di_tích_quốc_gia_Việt_Nam");
    }

    List<PlaceNKS> placeList = new ArrayList<>();


    /**
     * Thu thập dữ liệu ở trang web chỉ định
     * @param url
     * @throws IOException
     */
    @Override
    public void scrapePage(String url) throws IOException{
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .ignoreHttpErrors(true)
                    .header("Accept-Language", "*")
                    .get();
            Elements tables = doc.select("div.mw-parser-output > table");
            for (int i = 1 ; i < tables.size(); i++){
                if(i == 22) continue;
                scrapeTable(tables.get(i));
            }
            scrape22thTable(tables.get(22));
        } catch (UnknownHostException e){
            throw new UnknownHostException("Turn on your Internet please");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void crawlData() throws InterruptedException {

    }

    /**
     * Thu thập dữ liệu ở các bảng trên Wikipedia
     * @param table
     * @throws IOException
     */
    public void scrapeTable(Element table) throws IOException{
        Elements rows = table.select("tbody > tr");
        for (int i = 1 ; i < rows.size(); i++){
            JSONObject placeItem = new JSONObject();
            // Lấy tên
            Elements names = rows.get(i).select("td:nth-of-type(1)");
            String name = "";
            for (Element element:names) name += element.text();
//            System.out.println(name);
            placeItem.put("name", name);
            // Lấy đường dẫn
            Element href = rows.get(i).selectFirst("td:nth-of-type(1) > a");
            String id = "/null";
            if(href != null) id = href.attr("href");
//            System.out.println(super.getWebLink() + id);
            placeItem.put("id",super.getWebLink()+id);
            String description = "";
            // Lấy vị trí
            Elements positions = rows.get(i).select("td:nth-of-type(2)");
            String pos = "";
            for (Element element:positions) pos += element.text();
            description += "Vị trí: " + pos + "\n";
            // Lấy loại di tích
            Element type = rows.get(i).selectFirst("td:nth-of-type(3)");
            if(type!=null) description += "Loại di tích: " + type.text() + "\n";
            // Lấy thông tin
            if(scrapeInformation(super.getWebLink()+id,"div.mw-parser-output > p:first-of-type") != null) description += scrapeInformation(super.getWebLink()+id,"div.mw-parser-output > p:first-of-type");
            System.out.println(description);
            placeItem.put("info", description);
            super.getOutput().put(placeItem);
        }
    }

    /**
     * Do bảng này có cấu trúc khác bảng khác nên làm một phương thức riêng
     * @param table
     */
    public void scrape22thTable(Element table){
        Elements rows = table.select("tbody > tr");
        for (int i = 1 ; i < rows.size(); i++){
            JSONObject obj = new JSONObject();
            // Lấy tên
            Elements names = rows.get(i).select("td:nth-of-type(2)");
            String name = "";
            for (Element element:names) name += element.text();
            System.out.println(name);
            obj.put("name", name);
            // Lấy đường dẫn
            Element href = rows.get(i).selectFirst("td:nth-of-type(2) > a");
            String id = "null";
            if(href != null) id = href.attr("href");
            System.out.println(id);
            obj.put("id",super.getWebLink()+id);
            String description = "";
            // Lấy vị trí
            Elements positions = rows.get(i).select("td:nth-of-type(3)");
            String pos = "";
            for (Element element:positions) pos += element.text();
            description += "Vị trí: " + pos + "\n";
            // Lấy thông tin
            Element info = rows.get(i).selectFirst("td:nth-of-type(4)");
            description += info.text();
            obj.put("info",description);
            super.getOutput().put(obj);
        }
    }
    public String scrapeInformation(String url, String css) throws IOException{

        String info = "";
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .ignoreHttpErrors(true)
                    .header("Accept-Language", "*")
                    .get();
            Elements text = doc.select(css);
            for (Element element:text){
                info += element.text() + '\n';
            }
        }  catch (IOException e){
            e.printStackTrace();
        }
        return info;
    }
}
