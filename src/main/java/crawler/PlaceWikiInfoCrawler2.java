package crawler;

import historyobject.PlaceNKS;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaceWikiInfoCrawler2 {
    public static void wikiInfoCrawler(PlaceNKS placeWiki, int placeIndex){

        Document doc;
        try {
            // fetching the target website
            doc = Jsoup.connect("https://vi.wikipedia.org/wiki/Danh_sách_Di_tích_quốc_gia_Việt_Nam")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int count = 0;
        // Headers Title
        List<String> header = new ArrayList<String>();
        header.add("Vị trí");
        header.add("Loại di tích");
        header.add("Năm CN");
        header.add("Ghi chú");
        List<String> headerSpecial = new ArrayList<String>();
        header.add("Địa điểm");
        header.add("Giá trị nổi bật");

        Elements speTableCheck = doc.select("table.wikitable > thead > tr:has(center)");
        Elements tr = doc.select("table.wikitable > tbody > tr");
        JSONObject info = new JSONObject();
        if (tr.size() > 0) {
            for (Element element : tr) {
                Elements dataCells = element.select("td:not(:first-child)");
                Elements speCheck = element.select("td:first-child");
                String specCheck = speCheck.text().trim();
                if (isNumeric(specCheck)) {
                    int index = 0;
                    for (Element dataCell : dataCells) {
                        if (index >= header.size()) {
                            break;
                        }
                        JSONObject infoItem = new JSONObject();
                        Element urlConnect = dataCell.selectFirst("a");
                        infoItem.put("name", dataCell.text());
                        if (urlConnect != null) {
                            infoItem.put("url", urlConnect.attr("href"));
                        }
                        info.put(header.get(index), infoItem);
                        if ((count+1) % 4 == 0 && (count+1)/4 == placeIndex){
                            placeWiki.setInfo(info);
                            return;
                        }
                        index++;
                        count++;
                    }
                }else {

                }

            }
        }
    }
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}

