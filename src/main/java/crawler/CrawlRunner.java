package crawler;

public class CrawlRunner {
    public static void main(String[] args) throws Exception{
        DynastyWikiCrawler crawler = new DynastyWikiCrawler();
        crawler.crawlAndSave();
    }
}


