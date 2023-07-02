package crawler;

public class CrawlRunner {
    public static void main(String[] args) throws Exception{
        Crawler characterNKSCrawler = new CharacterNKSCrawler();
        Crawler placeNKSCrawler = new PlaceNKSCrawler();
        Crawler placeWikiCrawler = new PlaceWikiCrawler();
        Crawler kingWikiCrawler = new KingWikiCrawler();
        Crawler eventsNKSCrawler = new EventsNKSCrawler();
        Crawler warWikiCrawler = new WarWiki();
        Crawler dynastyWikiCrawler = new DynastyWikiCrawler();
        Crawler festivalCrawler = new FestivalCrawler();


//        festivalCrawler.crawlAndSave();
//        dynastyWikiCrawler.crawlAndSave();
//        kingWikiCrawler.crawlAndSave();
//        characterNKSCrawler.crawlAndSave();
//        placeNKSCrawler.crawlAndSave();
//        placeWikiCrawler.crawlAndSave();
//        eventsNKSCrawler.crawlAndSave();
        warWikiCrawler.crawlAndSave();
    }
}


