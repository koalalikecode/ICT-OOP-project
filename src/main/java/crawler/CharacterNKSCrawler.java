package crawler;

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
        setPageLimit(1);
    }

    @Override
    public void scrapePage(List<Character> characterList, Set<String> pagesDiscovered, List<String> pagesToScrape) {

        // Xóa đi phần tử đầu tên của List, đưa vào biến url để scrawl page đó
        String url = pagesToScrape.remove(0);

        pagesDiscovered.add(url);

        Document doc;
        try {
            // fetching the target website
            doc = Jsoup.connect(getWebLink() + url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
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
                doc2 = Jsoup.connect(getWebLink() + characterLink.attr("href")).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Elements name = doc2.select(".page-header h2");
            Elements tr = doc2.select("table.infobox > tbody > tr");
            String description = scrapeDescription(doc2, "div.com-content-article__body > p:first-of-type");
            JSONObject characterInfo = new JSONObject();
            if (tr != null && tr.size() > 0) {
                for (Element element : tr) {
                    Element th = element.selectFirst("th");
                    Element td = element.selectFirst("td");
                    Elements tdGroup = element.select("td");
                    if (th != null && td != null) {
                        characterInfo.put(th.text(), td.text());
                    } else if (th == null && tdGroup.size() > 1) {
                        characterInfo.put(tdGroup.get(0).text(), tdGroup.get(1).text());
                    }
                }
            }
            characterItem.setName(name.text());
            characterItem.setUrl(characterLink.attr("href"));
            characterItem.setDescription(description);
            characterItem.setInfo(characterInfo);
            characterList.add(characterItem);
        }

        // iterating over the pagination HTML elements
        for (Element pageElement : paginationElements) {
            // the new link discovered
            String pageUrl = pageElement.attr("href");

            // if the web page discovered is new and should be scraped
            if (!pagesDiscovered.contains(pageUrl) && !pagesToScrape.contains(pageUrl) && !pageUrl.equals("#")) {
                pagesToScrape.add(pageUrl);
            }

            // adding the link just discovered
            // to the set of pages discovered so far
            pagesDiscovered.add(pageUrl);
        }
        System.out.println("Done: " + url);
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

        // the number of iteration executed
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
        System.out.println("Crawled " + getOutput().length() + " characters");
    }
}
