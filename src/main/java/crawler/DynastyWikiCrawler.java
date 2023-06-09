package crawler;

import historyobject.Dynasty;
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

public class DynastyWikiCrawler extends Crawler {

    public DynastyWikiCrawler() {
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/dynastyWiki.json");
        setStartLink("/wiki/L%E1%BB%8Bch_s%E1%BB%AD_Vi%E1%BB%87t_Nam");
        setPageLimit(1);
    }

    @Override
    public void scrapePage(List dynastyList, Set<String> pagesDiscovered, List<String> pagesToScrape){
        String url = pagesToScrape.remove(0);
        pagesDiscovered.add(url);
        Document doc;
        try {
        // fetching the target website
            doc = Jsoup.connect(getWebLink() + url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements ListOfDynasties = doc.select("table.toccolours > tbody > tr").get(1).select("td > table > tbody > tr[bgcolor]");
        for (Element ListOfDynasty: ListOfDynasties){
            Elements dynasties = ListOfDynasty.select("td > b");
            for (int i = 0; i < dynasties.size(); i++){
                Dynasty dynastyItem = new Dynasty();
                // Get name
                String name = dynasties.get(i).select("a").attr("title");
                // Get url
                String dynastyURL = dynasties.get(i).select("a").attr("href");
                JSONObject info = new JSONObject();
                Document doc2;
                try {
                    // fetching the target website
                    doc2 = Jsoup.connect(getWebLink() + dynastyURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
                    // Get description
                    String description = doc2.selectFirst("div.mw-parser-output > p").text();
                    Elements tableInfo = doc2.select("div.mw-parser-output > table.infobox > tbody > tr");
                    int start_index = 0;
                    for (int j = 0; j < tableInfo.size(); j++){
                        Element check = tableInfo.get(j).selectFirst("td > span.mw-default-size");
                        if (check != null){
                            start_index = j + 1;
                            break;
                        }
                    }
                    int mergedrow = 0;
                    for (int j = start_index; j < tableInfo.size() - 2; j++){
                        if (tableInfo.get(j).hasClass("mergedrow")){
                            mergedrow = j;
                            break;
                        }
                    }
                    for (int j = start_index; j < tableInfo.size() - 2; j++){
                        if (tableInfo.get(j).hasClass("mergedrow")) continue;
                        Elements th = tableInfo.get(j).select("th");
                        Elements td = tableInfo.get(j).select("td");
                        if (th.size() > 0 && td.size() > 0){
                            String key = th.text();
                            String value = td.text();
                            info.put(key, value);
                        }
                    }
                    String key_side = "";
                    JSONObject value_side = new JSONObject();
                    for (int j = mergedrow; j < tableInfo.size() - 2; j++){
                        Elements th = tableInfo.get(j).select("th");
                        Elements td = tableInfo.get(j).select("td");
                        if (td.text().length() == 0 && th.text().length() > 0){
                            key_side = th.text();
                        }
                        else if (tableInfo.get(j).attr("style").equals("display:none")){
                            info.put(key_side, value_side);
                            key_side = "";
                            value_side = new JSONObject();
                        }
                        else if (th.size() > 0 && td.size() > 0){
                            String field1 = "";
                            int index1 = 0;
                            for (int k = 0; k < th.text().length(); k++){
                                if (th.text().charAt(k) == ' '){
                                    index1 = k;
                                    break;
                                }
                            }
                            field1 = th.text().substring(index1 + 1);;

                            value_side.put(field1, td.text());
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

                    dynastyItem.setName(name);
                    dynastyItem.setUrl(dynastyURL);
                    dynastyItem.setConnection(connections);
                    dynastyItem.setInfo(info);
                    dynastyItem.setDescription(description);
                    dynastyList.add(dynastyItem);

                    System.out.println("Done: " + dynastyURL);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void crawlData() throws InterruptedException {
        List<Dynasty> crawlObjectList = Collections.synchronizedList(new ArrayList<>());

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
        System.out.println("Crawled " + getOutput().length() + " dynasties");
    }
}