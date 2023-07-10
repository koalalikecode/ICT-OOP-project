package crawler.character;

import crawler.Crawler;
import historyobject.Character;
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

public class KingWikiCrawler extends Crawler {
    public KingWikiCrawler() {
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/kingWiki.json");
        setStartLink("/wiki/Vua_Việt_Nam");
    }

    @Override
    public void crawlData() {
        List<Character> kingList = new ArrayList<>();

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

        Elements kingLinks = doc.select("table[cellpadding='0'] > tbody > tr:gt(1) > td:eq(1) b a:not(.new)");

        for (Element kingLink : kingLinks) {
            Character king = new Character();
            Document kingDoc;
            try {
                // fetching the target website
                String kingLinkStr = URLDecoder.decode(kingLink.attr("abs:href"), "UTF-8");
                kingDoc = Jsoup.connect(kingLinkStr)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
//                        .ignoreHttpErrors(true)
                        .header("Accept-Language", "*")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements name = kingDoc.select("h1 span");
            if(kingDoc.select("table.infobox>tbody>tr>td[colspan]>span>a>img.mw-file-element").size()>0) king.setImageUrl(kingDoc.selectFirst("table.infobox>tbody>tr>td[colspan]>span>a>img.mw-file-element").attr("src"));
            else king.setImageUrl(null);
            List<JSONObject> moreCharacter = scapeMoreConnection(kingDoc,"div.mw-parser-output p a");
            String description = scrapeDescription(kingDoc, "div.mw-parser-output > p:first-of-type");
            JSONObject kingInfo = scrapeInfoBox(kingDoc, "table.infobox > tbody > tr:has(th, td)");
            king.setName(name.text());
            king.setUrl(kingLink.attr("href"));
            king.setDescription(description);
            king.setInfo(kingInfo);
            king.setConnection(moreCharacter);
            kingList.add(king);
            System.out.println(king.getName());
        }
        setOutput(new JSONArray(kingList));
    }

    @Override
    public JSONObject scrapeInfoBox(Document doc, String cssQuery) {
        JSONObject info = new JSONObject();
        Elements tr = doc.select(cssQuery); // Lấy ra các thẻ tr của bảng infobox
        if (tr.size() > 0) {
            for (Element element : tr) {
                JSONObject infoItem = new JSONObject();
                Element th = element.selectFirst("th");
                Element td = element.selectFirst("td");
                if (th != null && td != null) {
                    Element urlConnect = td.selectFirst("a");
                    infoItem.put("name", td.text());
                    if (urlConnect != null) {
                        infoItem.put("url", urlConnect.attr("href"));
                    }
                    info.put(th.text(), infoItem);
                }
            }
        }
        return info;
    }
}
