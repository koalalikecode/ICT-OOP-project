package crawler.event;

import crawler.Crawler;
import historyobject.Event;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventWikiCrawler extends Crawler {
    public EventWikiCrawler(){
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/eventWiki.json");
        setStartLink("/wiki/Danh_sách_trận_đánh_trong_lịch_sử_Việt_Nam");
        setPageLimit(1);
    }

    @Override
    public void scrapePage(List eventList, Set<String> pagesDiscovered, List<String> pagesToScrape){
        String url = pagesToScrape.remove(0);
        pagesDiscovered.add(url);
        Document doc;
        try {
            url = URLDecoder.decode(url, "UTF-8");
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements ListOfWar = doc.select("div.mw-parser-output > ul");
        for (int i = 0; i < ListOfWar.size() - 2; i++){
            Elements wars = ListOfWar.get(i).select("li");
            for (Element war: wars){
                if (war.select("a").size() > 0) {
                    Event warItem = new Event();
                    String warURL = war.select("a").attr("href");
                    String name = war.select("a").text();
                    System.out.println("Name: " + name);
                    Document doc2;
                    try {
                        warURL = URLDecoder.decode(warURL, "UTF-8");
                        // fetching the target website
                        doc2 = Jsoup.connect(getWebLink() + warURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
                        String description = doc2.select("div.navbox + p").text();
                        // Info of war
                        JSONObject warInfo = new JSONObject();
                        Elements tableInfo = doc2.select("div.mw-parser-output > table.infobox > tbody > tr");

                        Elements summarytable = tableInfo.select("tr > td > table > tbody > tr");
                        if (summarytable.size() > 0) {
                            String time = summarytable.get(0).select("td").text();
                            String place = summarytable.get(1).select("td").text();
                            String result = summarytable.get(2).select("td").text();
                            warInfo.put("Thời gian", time);
                            warInfo.put("Địa điểm", place);
                            warInfo.put("Kết quả", result);
                        }
                        int position = -1;
                        for (int j = 0; j < tableInfo.size(); j++){
                            if (tableInfo.get(j).select("th").text().equals("Tham chiến")){
                                position = j;
                                break;
                            }
                        }
                        if (position != -1){
                            for (int j = position; j <= tableInfo.size() - 2; j += 2){
                                String field = tableInfo.get(j).select("th").text();
                                JSONObject side = new JSONObject();
                                Elements attribute = tableInfo.get(j + 1).select("td");
                                if (attribute.size() == 2){
                                    side.put("phe 2", attribute.get(1).text());
                                    side.put("phe 1", attribute.get(0).text());
                                }
                                else side.put("phe", attribute.text());
                                warInfo.put(field, side);
                            }
                        }

                        // Get connection
                        List connections = new ArrayList<JSONObject>();
                        Elements ConnectionList = doc2.select("h2:has(span#Xem_thêm) + ul").select("li");
                        for (Element cnt: ConnectionList){
                            JSONObject connection = new JSONObject();
                            connection.put("name", cnt.select("a").text());
                            connection.put("url", cnt.select("a").attr("href"));
                            connections.add(connection);
                        }

                        warItem.setConnection(connections);
                        warItem.setInfo(warInfo);
                        warItem.setDescription(description);
                        warItem.setName(name);
                        warItem.setUrl(warURL);
                        eventList.add(warItem);
                        System.out.println("Done: " + warURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
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

        // waiting up to 300 seconds for all pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);

        setOutput(new JSONArray(crawlObjectList));
        System.out.println("Crawled " + getOutput().length() + " events");
    }
}