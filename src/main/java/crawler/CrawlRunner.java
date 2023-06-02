package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
        CharacterNKSCrawler characterNKSCrawler = new CharacterNKSCrawler();
        PlaceNKSCrawler placeNKSCrawler = new PlaceNKSCrawler();
        PlaceWikiCrawler placeWikiCrawler = new PlaceWikiCrawler();
        KingWikiCrawler kingWikiCrawler = new KingWikiCrawler();
        EventsNKSCrawler eventsNKSCrawler = new EventsNKSCrawler();
        WarWiki warWikiCrawler = new WarWiki();
        
//        kingWikiCrawler.crawlAndSave();
//        characterNKSCrawler.crawlAndSave();
//        placeNKSCrawler.crawlAndSave();
//        placeWikiCrawler.crawlAndSave();
//        eventsNKSCrawler.crawlAndSave();
//        warWikiCrawler.crawlAndSave();
    }
}


