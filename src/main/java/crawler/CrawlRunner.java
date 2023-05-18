package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
        //EventsNKSCrawler crawler2 = new EventsNKSCrawler();
        //crawler2.crawlAndSave();
        EventsWikiCrawler crawler3 = new EventsWikiCrawler();
        crawler3.crawlAndSave();
    }
}
