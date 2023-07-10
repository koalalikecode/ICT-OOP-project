package crawler;

import crawler.Character.CharacterNKSCrawler;
import crawler.Character.KingWikiCrawler;
import crawler.Dynasty.DynastyWikiCrawler;
import crawler.Event.EventWikiCrawler;
import crawler.Event.EventsNKSCrawler;
import crawler.Event.WarWiki;
import crawler.Festival.FestivalCrawler;
import crawler.Place.PlaceNKSCrawler;
import crawler.Place.PlaceWikiCrawler;

public class CrawlRunner {
    public static void main(String[] args) throws Exception{
        CharacterNKSCrawler characterNKSCrawler = new CharacterNKSCrawler();
        PlaceNKSCrawler placeNKSCrawler = new PlaceNKSCrawler();
        PlaceWikiCrawler placeWikiCrawler = new PlaceWikiCrawler();
        KingWikiCrawler kingWikiCrawler = new KingWikiCrawler();
        EventsNKSCrawler eventsNKSCrawler = new EventsNKSCrawler();
        EventWikiCrawler eventWikiCrawler = new EventWikiCrawler();
        WarWiki warWikiCrawler = new WarWiki();
        DynastyWikiCrawler dynastyWikiCrawler = new DynastyWikiCrawler();
        FestivalCrawler festivalCrawler = new FestivalCrawler();


//        festivalCrawler.crawlAndSave();
//        dynastyWikiCrawler.crawlAndSave();
//        kingWikiCrawler.crawlAndSave();
//        characterNKSCrawler.crawlAndSave();
//        placeNKSCrawler.crawlAndSave();
//        placeWikiCrawler.crawlAndSave();
        eventsNKSCrawler.crawlAndSave();
//        warWikiCrawler.crawlAndSave();
        eventWikiCrawler.crawlAndSave();
    }
}


