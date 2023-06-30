package crawler;

import historyobject.PlaceNKS;
import crawler.PlaceWikiInfoCrawler;

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

        Elements placeLinks = doc.select("table.wikitable > tbody > tr:gt(0) > td:eq(0) > a:eq(0)");
        Elements name = doc.select("table.wikitable > tbody > tr > td:eq(0) ");

        for (Element placeLink : placeLinks) {
            PlaceNKS placeWiki = new PlaceNKS();
            Document Doc;
            try {
                // fetching the target website
                String placeLinkStr = URLDecoder.decode(placeLink.attr("abs:href"), "UTF-8");
                Doc = Jsoup.connect(placeLinkStr)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .ignoreHttpErrors(true)
                        .header("Accept-Language", "*")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            List<JSONObject> morePlace = scapeMoreConnection(Doc,".mw-parser-output > p > a");
            String description = scrapeDescription(Doc, "div.mw-parser-output > p:first-of-type");

            placeWiki.setName(name.get(nameIndex).text());
            nameIndex++;

            if (placeLink.attr("href").contains("redlink=1")){
                placeWiki.setUrl("The page does not exist");
            } else {
                placeWiki.setUrl(placeLink.attr("abs:href"));
            }

            placeWiki.setDescription(description);
//            PlaceWikiInfoCrawler.wikiInfoCrawler(placeWiki, nameIndex);
            placeWiki.setConnection(morePlace);
            placeList.add(placeWiki);
            System.out.println(placeWiki.getName());
        }
        setOutput(new JSONArray(placeList));
    }

}