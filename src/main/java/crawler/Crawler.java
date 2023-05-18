package crawler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Crawler {
    private String webLink; // trang web để crawl data
    private String startLink; // trang web con để crawl data, ví dụ "/nhan-vat" của "https://nguoikesu.com"
    private JSONArray output; // kết quả mảng JSON sau khi crawl
    private String folder; // nơi lưu trữ file json kết quả
    private int pageLimit; // giới hạn số lượng page trong startLink để crawl

    public JSONArray getOutput() {
        return output;
    }

    public void setOutput(JSONArray output) {
        this.output = output;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getStartLink() {
        return startLink;
    }

    public void setStartLink(String startLink) {
        this.startLink = startLink;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    // Hàm để scrape description của đối tượng từ trang web
    public String scrapeDescription(Document doc, String cssQuery) {
        Elements description = doc.select(cssQuery);
        String res = "";
        for (Element item:description) {
            res += item.text() + "\n";
        }
        return res;
    }

    // Hàm để crawl các đối tượng xem thêm của các object
    public List<JSONObject> scapeMoreConnection(Document doc, String cssQuery) {
        List<JSONObject> res = new ArrayList<>();
        Elements connections = doc.select(cssQuery);
        if(connections.size() > 0) {
            int i = 0;
            for(Element connection: connections) {
                if(res.stream().noneMatch(p -> p.getString("name").equals(connection.text()))) {
                    JSONObject item = new JSONObject();
                    item.put("name", connection.text());
                    item.put("url", connection.attr("href"));
                    res.add(item);
                    i++;
                }
                if (i >= 8) break;
            }
        }
        return res;
    }

    // Hàm để scrape bảng thông tin nhân vật trong web NKS
    public JSONObject scrapeInfoBox(Document doc, String cssQuery) {
        JSONObject info = new JSONObject();
        Elements tr = doc.select(cssQuery); // Lấy ra các thẻ tr của bảng infobox
        if (tr != null && tr.size() > 0) {
            for (Element element : tr) {
                JSONObject infoItem = new JSONObject();
                Element th = element.selectFirst("th");
                Element td = element.selectFirst("td");
                Elements tdGroup = element.select("td");
                if (th != null && td != null) {
                    Element urlConnect = td.selectFirst("a");
                    infoItem.put("name", td.text());
                    if (urlConnect != null) {
                        infoItem.put("url", urlConnect.attr("href"));
                    }
                    info.put(th.text(), infoItem);
                } else if (th == null && tdGroup.size() > 1) {
                    Element urlConnect = tdGroup.get(1).selectFirst("a");
                    infoItem.put("name", tdGroup.get(1).text());
                    if (urlConnect != null) {
                        infoItem.put("url", urlConnect.attr("href"));
                    }
                    info.put(tdGroup.get(0).text(), infoItem);
                }
            }
        }
        return info;
    }

    // Hàm scrape dữ liệu dành cho page Nguoi ke su
    public void scrapePage(
            List CrawlerObjectList,
            Set<String> pagesDiscovered,
            List<String> pagesToScrape) {
    }

    // Ham scrape du lieu danh cho page khac
    public void scrapePage(String pageToScrape) {
    }

    // Kết hợp với hàm scrapePage để crawl toàn bộ các page, phân trang,...
    public void crawlData() throws InterruptedException {
    }


    //    Hàm để lưu dữ liệu vào file Json
    private void saveData() {
        FileWriter file = null;
        try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            file = new FileWriter(folder);
            file.write(output.toString(1));
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
    // Hàm để thực hiện crawl và save dữ liệu vào file
    public void crawlAndSave() {
        try {
            crawlData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        saveData();
    }
}



