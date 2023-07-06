package crawler;

import historyobject.Place;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PlaceNKSCrawler extends Crawler {

    public PlaceNKSCrawler() {
        setWebLink("https://nguoikesu.com");
        setFolder("data/placeNKS.json");
        setStartLink("/di-tich-lich-su?types[0]=1");
        setPageLimit(3);
    }
    @Override
    public void scrapePage(List placeList, Set<String> pagesDiscovered, List<String> pagesToScrape) {

        // Remove the first element from the List, assign it to the 'url' variable to crawl that page
        String url = pagesToScrape.remove(0);

        pagesDiscovered.add(url);

        Document doc;
        try {
            // Fetching the target website
            doc = Jsoup.connect(getWebLink() + url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements paginationElements = doc.select("ul.pagination a.page-link");
        Elements placeLinks = doc.select(".list-group-item h3 a");

        // Iterating over the list of HTML places
        for (Element placeLink : placeLinks) {
            Place placeItem = new Place();

            Document doc2;

            try {
                // Fetching the target website
                doc2 = Jsoup.connect(getWebLink() + placeLink.attr("href"))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .header("Accept-Language", "*")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Elements name = doc2.select(".page-header h2");
            if(doc2.select("img").size()>0) {
                if(!doc2.selectFirst("img").attr("data-src").equals(""))placeItem.setImageUrl(getWebLink()+doc2.selectFirst("img").attr("data-src"));
                else placeItem.setImageUrl(null);
            }
            else placeItem.setImageUrl(null);
            List<JSONObject> connect = scapeMoreConnection(doc2, "div.com-content-article__body a.annotation");
            String description = scrapeDescription(doc2, "div.com-content-article__body > p:first-of-type");
            JSONObject placeInfo = scrapeInfoBox(doc2, "table.infobox > tbody > tr");
            placeItem.setName(name.text());
            placeItem.setUrl(placeLink.attr("href"));
            placeItem.setDescription(description);
            placeItem.setInfo(placeInfo);
            placeItem.setConnection(connect);
            placeList.add(placeItem);
        }

        // Iterating over the pagination HTML elements
        for (Element pageElement : paginationElements) {
            // The new link discovered
            String pageUrl = pageElement.attr("href");

            // If the web page discovered is new and should be scraped
            if (!pagesDiscovered.contains(pageUrl) && !pagesToScrape.contains(pageUrl) && !pageUrl.equals("#")) {
                pagesToScrape.add(pageUrl);
            }

            // Adding the link just discovered
            // to the set of pages discovered so far
            pagesDiscovered.add(pageUrl);
        }
        System.out.println("Done: " + url);
    }
    @Override
    public void crawlData() throws InterruptedException {
        List<Place> crawlObjectList = Collections.synchronizedList(new ArrayList<>());

        Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());

        List<String> pagesToScrape = Collections.synchronizedList(new ArrayList<>());

        pagesToScrape.add(getStartLink());

        // Initializing the ExecutorService to run the
        // web scraping process in parallel on 4 pages at a time
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape);

        // The number of iterations executed
        int i = 1;

        while (!pagesToScrape.isEmpty() && i < getPageLimit()) {
            // Registering the web scraping task
            executorService.execute(() -> scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape));

            // Adding a 200ms delay to avoid overloading the server
            TimeUnit.MILLISECONDS.sleep(200);

            // Incrementing the iteration number
            i++;
        }

        // Waiting up to 300 seconds for all pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);

        setOutput(new JSONArray(crawlObjectList));
        System.out.println("Crawled " + getOutput().length() + " places");
    }
}