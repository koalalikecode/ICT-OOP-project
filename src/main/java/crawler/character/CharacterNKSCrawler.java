package crawler.character;

import crawler.Crawler;
import historyobject.Character;
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

public class CharacterNKSCrawler extends Crawler {

    public CharacterNKSCrawler() {
        setWebLink("https://nguoikesu.com");
        setFolder("data/characterNKS.json");
        setStartLink("/nhan-vat");
        setPageLimit(1450);
    }

    @Override
    public void scrapePage(List characterList, Set<String> pagesDiscovered, List<String> pagesToScrape) {

        // Xóa đi phần tử đầu tên của List, đưa vào biến url để scrawl page đó

        String url = pagesToScrape.remove(0);
        pagesDiscovered.add(url);

        Document doc;
        try {
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + url).userAgent("").header("Accept-Language", "*").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements paginationElements = doc.select("ul.pagination a.page-link");
        Elements characterLinks = doc.select(".blog-item h2 a");

        // iterating over the list of HTML products
        for (Element characterLink : characterLinks) {
            Character characterItem = new Character();

            Document doc2;

            try {
                // fetching the target website
                doc2 = Jsoup.connect(getWebLink() + characterLink.attr("href"))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .header("Accept-Language", "*").get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Elements name = doc2.select(".page-header h2");
            List<JSONObject> moreCharacter = scapeMoreConnection(doc2,"div.com-content-article__body a.annotation");
            String description = scrapeDescription(doc2, "div.com-content-article__body > p:first-of-type");
            JSONObject characterInfo = scrapeInfoBox(doc2, "table.infobox > tbody > tr");
            characterItem.setName(name.text());
            if(doc2.select("table.infobox>tbody>tr>td>img").size()>0){if(!doc2.selectFirst("table.infobox>tbody>tr>td>img").attr("data-src").equals(""))characterItem.setImageUrl(getWebLink()+doc2.selectFirst("table.infobox>tbody>tr>td>img").attr("data-src"));
                else characterItem.setImageUrl(null);
            }
            else characterItem.setImageUrl(null);
            characterItem.setUrl(characterLink.attr("href"));
            characterItem.setDescription(description);
            characterItem.setInfo(characterInfo);
            characterItem.setConnection(moreCharacter);
            characterList.add(characterItem);
        }
        System.out.println("Done: " + url);
    }

    @Override
    public void crawlData() throws InterruptedException {
        List<Character> crawlObjectList = Collections.synchronizedList(new ArrayList<>());

        Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());

        List<String> pagesToScrape = Collections.synchronizedList(new ArrayList<>());

        pagesToScrape.add(getStartLink());
        for (int i = 5; i <= getPageLimit(); i+=5) {
            pagesToScrape.add(getStartLink() + "?&start=" + i);
        }

        // initializing the ExecutorService to run the
        // web scraping process in parallel on 4 pages at a time
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape);

        // the number of iteration executed
        int i = 1;

        while (!pagesToScrape.isEmpty()) {
            // registering the web scraping task
            executorService.execute(() -> scrapePage(crawlObjectList, pagesDiscovered, pagesToScrape));

            // adding a 200ms delay to avoid overloading the server
            TimeUnit.MILLISECONDS.sleep(200);

            // incrementing the iteration number
            i++;
        }

        // waiting up to 300 seconds for all pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.SECONDS);

        setOutput(new JSONArray(crawlObjectList));
        System.out.println("Crawled " + getOutput().length() + " characters");
    }
}