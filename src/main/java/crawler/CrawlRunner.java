package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
//    	CharacterNKSCrawler crawler1 = new CharacterNKSCrawler();
//    	crawler1.crawlAndSave();
    //        KingWikiCrawler crawler2 = new KingWikiCrawler();
    //        crawler2.crawlAndSave();
//        EventsNKSCrawler crawler3 = new EventsNKSCrawler();
//        crawler3.crawlAndSave();
        PlaceNKSCrawler crawler = new PlaceNKSCrawler();
        crawler.crawlAndSave();
    }
}
