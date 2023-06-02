package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
//        EventWikiCrawler crawler3 = new EventWikiCrawler();
//        crawler3.crawlAndSave();
        WarWiki crawler = new WarWiki();
        crawler.crawlAndSave();
    }
}
