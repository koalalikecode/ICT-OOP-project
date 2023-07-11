package crawler;

import crawler.character.CharacterNKSCrawler;
import crawler.character.KingWikiCrawler;
import crawler.dynasty.DynastyWikiCrawler;
import crawler.event.EventWikiCrawler;
import crawler.event.EventsNKSCrawler;
import crawler.event.WarWiki;
import crawler.festival.FestivalCrawler;
import crawler.place.PlaceNKSCrawler;
import crawler.place.PlaceWikiCrawler;

public class CrawlRunner {
    public static void main(String[] args) throws Exception{
        Crawler characterNKSCrawler = new CharacterNKSCrawler();
        Crawler placeNKSCrawler = new PlaceNKSCrawler();
        Crawler placeWikiCrawler = new PlaceWikiCrawler();
        Crawler kingWikiCrawler = new KingWikiCrawler();
        Crawler eventsNKSCrawler = new EventsNKSCrawler();
        Crawler eventWikiCrawler = new EventWikiCrawler();
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
//        warWikiCrawler.crawlAndSave();
//        eventWikiCrawler.crawlAndSave();
    }
}


