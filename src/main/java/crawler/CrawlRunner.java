package crawler;

import java.io.IOException;

public class CrawlRunner {
    public static void main(String[] args) throws IOException {
        CharacterNKSCrawler crawler1 = new CharacterNKSCrawler();
        PlaceNKSCrawler crawler2 = new PlaceNKSCrawler();
        PlaceWikiCrawler crawler4 = new PlaceWikiCrawler();
//        KingWikiCrawler kingWikiCrawler = new KingWikiCrawler();
//        kingWikiCrawler.crawlAndSave();
//        crawler1.crawlAndSave();
//        crawler2.crawlAndSave();
//        crawler4.crawlAndSave();
        PlaceWikiCrawler3 crawler5 = new PlaceWikiCrawler3();
        crawler5.crawlAndSave2();
    }
}


