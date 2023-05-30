package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
        CharacterNKSCrawler crawler1 = new CharacterNKSCrawler();
        PlaceNKSCrawler crawler2 = new PlaceNKSCrawler();
        PlaceWikiCrawler crawler4 = new PlaceWikiCrawler();
        KingWikiCrawler kingWikiCrawler = new KingWikiCrawler();
//        kingWikiCrawler.crawlAndSave();
//        crawler1.crawlAndSave();
//        crawler2.crawlAndSave();
        crawler4.crawlAndSave();
    }
}


