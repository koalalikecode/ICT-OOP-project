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
//        eventsNKSCrawler.crawlAndSave();
//        warWikiCrawler.crawlAndSave();
//        eventWikiCrawler.crawlAndSave();
    }
}


