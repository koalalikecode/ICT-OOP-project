package crawler;

import historyobject.Event;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventWikiCrawler extends Crawler{
    public EventWikiCrawler(){
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/eventWiki.json");
        setStartLink("/wiki/Các_cuộc_chiến_tranh_Việt_Nam_tham_gia");
        setPageLimit(1);
    }

    @Override
    public void scrapePage(List eventList, Set<String> pagesDiscovered, List<String> pagesToScrape){
        String url = pagesToScrape.remove(0);
        pagesDiscovered.add(url);
        Document doc;
        try {
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Successfully connected to " + url);
        Elements eventNames = doc.select("div.mw-parser-output > h3 > span.mw-headline");
        System.out.println(eventNames.size());
        Elements eventTable = doc.select("table.wikitable > tbody");

        for (int i = 0; i < eventNames.size(); i++){
            // Create a event JSONOject
            Event eventItem = new Event();

            // Get event name:
            String name = eventNames.get(i).text();
            // Get event url:
            String eventURL = eventNames.get(i).selectFirst("a").attr("href");

            // Get event description
            Document doc2;
            try {
                // fetching the target website
                doc2 = Jsoup.connect(getWebLink() + eventURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String description = doc2.select("table[class='table toccolours'] + p").text();

            // Info of the event
            JSONObject eventInfo = new JSONObject();
            // Table info
            Elements table = eventTable.get(i).select("tr");
            // Connection
            List connections = new ArrayList<>();
            List exist = new ArrayList<String>();
            Elements moreEvent = table.select("a");

            int NUMBER_OF_CONNECTION = 8;
            int k = 0, m = 0;
            while (m < NUMBER_OF_CONNECTION && k < moreEvent.size()){
                JSONObject connection = new JSONObject();
                String moreName = moreEvent.get(k).text();
                if (!exist.contains(moreName)){
                    exist.add(moreName);
                    connection.put("name", moreName);
                    connection.put("url", moreEvent.get(k).attr("href"));
                    connections.add(connection);
                    m++;
                }
                k++;
            }

            // Key list:
            Elements keys = table.get(0).select("th");
            // Value list and get info
            for (int j = 1; j < table.size(); j++){
                JSONObject info = new JSONObject();
                for (int z = 0; z < keys.size(); z++){
                    info.put(keys.get(z).text(), table.get(j).select("td").get(z).text());
                }
                eventInfo.put("event " + j, info);
            }

            eventItem.setName(name);
            eventItem.setUrl(eventURL);
            eventItem.setInfo(eventInfo);
//            eventItem.setConnection(null);
            eventItem.setDescription(description);
            eventItem.setConnection(connections);
            eventList.add(eventItem);
        }
        System.out.println("Done: " + url);
    }

    @Override
    public void crawlData() throws InterruptedException {
        List<Event> crawlObjectList = Collections.synchronizedList(new ArrayList<>());

        Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());

        List<String> pagesToScrape = Collections.synchronizedList(new ArrayList<>());

        pagesToScrape.add(getStartLink());

        // initializing the ExecutorService to run the
        // web scraping process in parallel on 4 pages at a time
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape);

        // the number of iteration executed
//        int i = 1;
//
//        while (!pagesToScrape.isEmpty() && i < getPageLimit()) {
//            // registering the web scraping task
//            executorService.execute(() -> scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape));
//
//            // adding a 200ms delay to avoid overloading the server
//            TimeUnit.MILLISECONDS.sleep(200);
//
//            // incrementing the iteration number
//            i++;
//        }

        // waiting up to 300 seconds for all pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);

        setOutput(new JSONArray(crawlObjectList));
        System.out.println("Crawled " + getOutput().length() + " events");
    }

}
