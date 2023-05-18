package crawler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import historyobject.Event;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//public class EventsWikiCrawler() extends Crawler {
public class EventsWikiCrawler extends Crawler {
    List eventList;
    Event eventItem = new Event();
    JSONArray eventArray = new JSONArray();
    JSONObject crawlObject = new JSONObject();
    List<JSONArray> list = new ArrayList<>(); 
    public EventsWikiCrawler() {
        setWebLink("https://vi.wikipedia.org");
        setStartLink("https://vi.wikipedia.org/wiki/C%C3%A1c_cu%E1%BB%99c_chi%E1%BA%BFn_tranh_Vi%E1%BB%87t_Nam_tham_gia");
        setFolder("data/eventWiki.json");
    }
    public void scrapePage(String pageToScrape){
        String url = pageToScrape;
        Document doc;
        try {
            doc = Jsoup.connect(url).userAgent("").timeout(10000).get();
            Elements table = doc.select("div.mw-parser-output > table.wikitable > tbody");
            for(int i=1; i<table.size();i++)
            {
                scrapeTable(table.get(i));   
            }

        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    } 
    public void scrapeTable(Element table) {
        Elements row = table.select("tbody > tr");
        for(int i = 1; i<=row.size(); i++)
        {
            
            Elements name = row.select("td:nth-of-type(1)");
            Elements object1 = row.select("td:nth-of-type(2)");
            Elements url1 = row.select("td:nth-of-type(2)> a");
            Elements object2 = row.select("td:nth-of-type(3)");
            Elements url2 = row.select("td:nth-of-type(3)> a");
            Elements result = row.select("td:nth-of-type(4)");
            Elements url3 = row.select("td:nth-of-type(4)> a");
            Elements url = row.select("td:nth-of-type(1)");
            
            String description = "";
            description = description + object1.text() + "giao chiến" + object2.text() + ". Kết quả: "+ result +"\n";
            eventItem.setName(name.text());
            if(url != null) eventItem.setUrl(url.text());
            eventItem.setDescription(description);
            String Connect = url1.text() + "\n" + url2.text() + "\n" + url3.text();
            eventItem.setConnect(Connect);
            eventList.add(eventItem);
            this.eventArray = new JSONArray(eventList);
            list.add(this.eventArray);
            //setOutput(eventArray);
        }
    }
    @Override
    public void crawlData() throws InterruptedException {
        //List<Event> crawlObjectList = Collections.synchronizedList(new ArrayList<>());
        //ExecutorService executorService = Executors.newFixedThreadPool(4);
        String pageToScrape = "";
        pageToScrape+=getStartLink();
        scrapePage(pageToScrape);
        setOutput(new JSONArray(list));
        }
    
}

    
    

    
