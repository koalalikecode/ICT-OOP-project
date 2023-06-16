package crawler;

import historyobject.Character;
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

public class TrangNguyenWikiCrawler extends Crawler{
    public TrangNguyenWikiCrawler(){
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/TrangNguyenWiki.json");
        setStartLink("/wiki/Danh_sách_Trạng_nguyên_Việt_Nam");
        setPageLimit(1);
    }

    @Override
    public void scrapePage(List characterList, Set<String> pagesDiscovered, List<String> pagesToScrape){
        String url = pagesToScrape.remove(0);
        pagesDiscovered.add(url);
        Document doc;
        try {
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements characterTables = doc.select("table.wikitable > tbody > tr");
        for (Element characterTable: characterTables) {
            if (characterTable.select("td").size() < 1) continue;
            String name = characterTable.select("td").get(1).select("a:not(.new)").attr("title");
            String characterURL = characterTable.select("td").get(1).select("a:not(.new)").attr("href");
            if (characterURL.length() == 0) continue;
            JSONObject info = new JSONObject();
            Character characterItem = new Character();
            Document doc2;
            try {
                // fetching the target website
                characterURL = URLDecoder.decode(characterURL, "UTF-8");
                doc2 = Jsoup.connect(getWebLink() + characterURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
                Elements tableInfos = doc2.select("table.infobox > tbody > tr");
                String description = doc2.selectFirst("div.mw-parser-output > p").text();
                for (Element tableInfo : tableInfos) {
                    Elements th = tableInfo.select("th");
                    Elements td = tableInfo.select("td");
                    if (th.size() > 0 && td.size() > 0) {
                        info.put(th.text(), td.text());
                    }
                }
                if (info.isEmpty()){
                    String dob = characterTable.select("td").get(2).text();
                    String place = characterTable.select("td").get(3).text();
                    String year = characterTable.select("td").get(4).text();
                    String king = characterTable.select("td").get(5).text();
                    if (dob.length() > 0) info.put("Sinh", dob);
                    if(place.length() > 0) info.put("QueQuan", place);
                    if (year.length() > 0) info.put("Tại vị", year);
                    if (king.length() > 0) info.put("Vua", king);
                }

                List connections = new ArrayList<JSONObject>();
                Elements ConnectionList = doc2.select("h2:has(span#Xem_thêm) + ul").select("li");
                for (Element cnt : ConnectionList) {
                    JSONObject connection = new JSONObject();
                    connection.put("name", cnt.select("a").text());
                    connection.put("url", cnt.select("a").attr("href"));
                    connections.add(connection);
                }
                characterItem.setName(name);
                characterItem.setUrl(characterURL);
                characterItem.setDescription(description);
                characterItem.setInfo(info);
                characterItem.setConnection(connections);
                characterList.add(characterItem);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Done " + characterURL);
        }
    }
    @Override
    public void crawlData() throws InterruptedException {
        List<Character> crawlObjectList = Collections.synchronizedList(new ArrayList<>());

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
        System.out.println("Crawled " + getOutput().length() + " characters");
    }
}
