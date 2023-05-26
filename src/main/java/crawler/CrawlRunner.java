package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
    	//CharacterNKSCrawler crawler1 = new CharacterNKSCrawler();
    	//crawler1.crawlAndSave();
        EventsNKSCrawler crawler2 = new EventsNKSCrawler();
        crawler2.crawlAndSave();
        //EventsWikiCrawler crawler3 = new EventsWikiCrawler();
        //crawler3.crawlAndSave();
    }
}
