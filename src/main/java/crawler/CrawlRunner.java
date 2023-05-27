package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
//        CharacterNKSCrawler crawler1 = new CharacterNKSCrawler();
//        crawler1.crawlAndSave();
//        EventNKSCrawler crawler2 = new EventNKSCrawler();
//        crawler2.crawlAndSave();
//        EventWikiCrawler crawler3 = new EventWikiCrawler();
//        crawler3.crawlAndSave();
        WarWikiCrawler crawler4 = new WarWikiCrawler();
        crawler4.crawlAndSave();
    }
}
