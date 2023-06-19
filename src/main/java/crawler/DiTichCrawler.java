package crawler;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DiTichCrawler extends Crawler {

    public DiTichCrawler() {
        setWebLink("http://www.ditich.vn");
        setFolder("data/ditich.json");
        setStartLink("/FrontEnd/DiTich");
    }

    @Override
    public void scrapePage(String pageToScrape) {
        Document doc;

        try {
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + pageToScrape)
                    .get();;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        scrapeInfoBox(doc, "div.hl__illustrated-list__list-item > span:not(.hl__illustrated-list__image-wrapper)");
    }

    @Override
    public JSONObject scrapeInfoBox(Document doc, String cssQuery) {
        Elements infoItem = doc.select(cssQuery);
        System.out.println(infoItem);
        return null;
    }

    public static void main(String[] args) {
        DiTichCrawler a = new DiTichCrawler();
        a.scrapePage("/FrontEnd/DiTich/Form?do=&ItemId=6193");
    }
}
