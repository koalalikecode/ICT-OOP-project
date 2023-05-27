package crawler;

public class CrawlRunner {
    public static void main(String[] args) {
//        CharacterNKSCrawler crawler1 = new CharacterNKSCrawler();
//        crawler1.crawlAndSave();
        DynastyWikiCrawler crawler1 = new DynastyWikiCrawler();
        crawler1.crawlAndSave();
    }
}
