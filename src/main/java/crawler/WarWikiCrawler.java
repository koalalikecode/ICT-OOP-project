package crawler;

import historyobject.Event;
import historyobject.War;
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

public class WarWikiCrawler extends Crawler{
    public WarWikiCrawler(){
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/warWiki.json");
        setStartLink("/wiki/Danh_sách_trận_đánh_trong_lịch_sử_Việt_Nam");
        setPageLimit(1);
    }
    @Override
    public void scrapePage(List warList, Set<String> pagesDiscovered, List<String> pagesToScrape){
        String url = pagesToScrape.remove(0);
        pagesDiscovered.add(url);
        Document doc;
        try {
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements ListOfWar = doc.select("div.mw-parser-output > ul");
        int number_of_wars = 0;
        for (int i = 0; i < ListOfWar.size() - 2; i++){
            Elements wars = ListOfWar.get(i).select("li");
            for (Element war: wars){
                if (war.select("a").size() > 0) {
                    War warItem = new War();
                    String warURL = war.select("a").attr("href");
                    String name = war.select("a").text();
                    System.out.println("Name: " + name);
                    number_of_wars++;
                    Document doc2;
                    try {
                        // fetching the target website
                        doc2 = Jsoup.connect(getWebLink() + warURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
                        String description = doc2.select("div.navbox + p").text();
                        JSONObject warInfo = new JSONObject();

                        Elements tableInfo = doc2.select("div.mw-parser-output > table.infobox > tbody > tr > td > table");
//                        System.out.println(tableInfo);
                        Elements infos = tableInfo.select("tbody > tr");
                        for (Element info: infos){
                            warInfo.put(info.select("th").text(), info.select("td").text());
                        }

                        warItem.setConnection(null);
                        warItem.setInfo(warInfo);
//                        if (warInfo != null) warItem.setInfo(warInfo);
//                        else  warItem.setInfo(null);
                        warItem.setDescription(description);
                        warItem.setName(name);
                        warItem.setUrl(warURL);
                        warList.add(warItem);
                        System.out.println("Done: " + warURL);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        System.out.println("Number of wars: " + number_of_wars);
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
        System.out.println("Crawled " + getOutput().length() + " wars");
    }

}
