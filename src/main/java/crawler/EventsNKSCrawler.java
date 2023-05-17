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
//import org.jsoup.nodes.Element;

public class EventsNKSCrawler extends Crawler{
    public EventsNKSCrawler()
    {
        setWebLink("https://nguoikesu.com");
        setFolder("data/eventsNKS.json");
        setStartLink("/tu-lieu/quan-su?filter_tag[0]=");
        setPageLimit(15);
    }

    @Override
    public void scrapePage(List eventList, Set<String> pagesDiscoverd, List<String> pagesToScrape)
    {
        String url = pagesToScrape.remove(0);
        
        pagesDiscoverd.add(url);

        Document doc;
        try {
            doc = Jsoup.connect(getWebLink() + url).userAgent("").header("Accept-Language", "*").get(); 
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements paginationElements = doc.select("ul.pagination a.page-link");
        Elements EventLinks = doc.select(".blog-item h2 a");

        for(Element EventLink : EventLinks) {
            Event eventItem = new Event();

            Document doc2;
            
            try {
                doc2 = Jsoup.connect(getWebLink() + EventLink.attr("href")).userAgent("").header("Accept-Language", "*").get();  
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements name = doc2.select(".page-header h1");
            List<JSONObject> moreEvent = scapeMoreConnection(doc2, "div.com-content-article__body a.annotation");
            String description = scrapeDescription(doc2, "div.com-content-article__body > p:first-of-type");
            JSONObject eventInfo = scrapeInfoBox(doc2, "table.infobox > tbody > tr");

            eventItem.setName(name.text());
            eventItem.setUrl(EventLink.attr("href"));
            eventItem.setDescription(description);
            eventItem.setInfo(eventInfo);
            eventItem.setConnection(moreEvent);
            eventList.add(eventItem);
        }

        for(Element pageElement : paginationElements) {
            String pageUrl = pageElement.attr("href");
            if(!pagesDiscoverd.contains(pageUrl) && !pagesToScrape.contains(pageUrl) && !pageUrl.equals("#")) {
                pagesToScrape.add(pageUrl);    
            }
            pagesDiscoverd.add(pageUrl);
        }  
        System.out.println("Done: " + url);
    }
    @Override
    public void crawlData() throws InterruptedException {
        List<Event> crawlObjectList = Collections.synchronizedList(new ArrayList<>());
        Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());
        List<String> pagesToScrape = Collections.synchronizedList(new ArrayList<>());

        pagesToScrape.add(getStartLink());

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape);

        int i = 1;
        while (!pagesToScrape.isEmpty() && i < getPageLimit()) {
            // registering the web scraping task
            executorService.execute(() -> scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape));

            // adding a 200ms delay to avoid overloading the server
            TimeUnit.MILLISECONDS.sleep(200);

            // incrementing the iteration number
            i++;
        }
        // waiting up to 300 seconds for all pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);
        setOutput(new JSONArray(crawlObjectList));
        System.out.println("Crawled " + getOutput().length() + " events");
    }
}
