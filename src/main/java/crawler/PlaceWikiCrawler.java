package crawler;

import historyobject.PlaceNKS;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public class PlaceWikiCrawler extends Crawler {
    public PlaceWikiCrawler() {
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/PlaceWiki.json");
        setStartLink("/wiki/Danh_sách_Di_tích_quốc_gia_Việt_Nam");
    }

    @Override
    public void crawlData() {
        List<PlaceNKS> placeList = new ArrayList<>();
        int nameIndex = 0;
        Document doc;
        try {
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + getStartLink())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Elements placeLinks = doc.select("table.wikitable > tbody > tr:gt(0) > td:eq(0) a");
        Elements name = doc.select("table.wikitable > tbody > tr > td:eq(0) ");

        for (Element placeLink : placeLinks) {
            PlaceNKS placeWiki = new PlaceNKS();
            Document Doc;
            try {
                // fetching the target website
                String placeLinkStr = URLDecoder.decode(placeLink.attr("abs:href"), "UTF-8");
                Doc = Jsoup.connect(placeLinkStr)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
//                        .ignoreHttpErrors(true)
                        .header("Accept-Language", "*")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            List<JSONObject> morePlace = scapeMoreConnection(Doc,".mw-parser-output > p > a");
            String description = scrapeDescription(Doc, "div.mw-parser-output > p:first-of-type");
            JSONObject placeInfo = scrapeInfoBox(doc, "table.wikitable ");

            placeWiki.setName(name.get(nameIndex).text());
            nameIndex++;

            placeWiki.setUrl(placeLink.attr("href"));
            placeWiki.setDescription(description);
            placeWiki.setInfo(placeInfo);
            placeWiki.setConnection(morePlace);
            placeList.add(placeWiki);
            System.out.println(placeWiki.getName());
        }
        setOutput(new JSONArray(placeList));
    }
    @Override
    public JSONObject scrapeInfoBox(Document doc, String cssQuery) {
        JSONObject info = new JSONObject();
        List<String> header = new ArrayList<String>();
        header.add("Vị trí");
        header.add("Loại di tích");
        header.add("Năm CN");
        header.add("Ghi chú");
        Elements tr = doc.select(cssQuery + " > tbody > tr");

        if (tr.size() > 0) {
            for (Element element : tr) {
                JSONObject infoItem = new JSONObject();
                Elements dataCells = element.select("td:not(:first-child)");
                int index = 0;
                for (Element dataCell : dataCells) {
                    if (index >= header.size()) {
                        break;
                    }
                    String dataText = dataCell.text();
                    Element urlConnect = dataCell.selectFirst("a");
                    infoItem.put("name", dataText);
                    if (urlConnect != null) {
                        infoItem.put("url", urlConnect.attr("href"));
                    }
                    infoItem.put(header.get(index), infoItem);
                    index++;
                }
            }
        }
        return info;
    }


}