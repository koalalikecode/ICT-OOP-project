package crawler;
import java.util.Date;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import historyobject.Event;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONML;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
//import java.lang.IndexOutOfBoundsException;
//import org.jsoup.nodes.Element;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;

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
            JSONObject eventInfo = scrapeInfoEvent1(doc2);
            if(doc2.select("div>img").size()>0) {
                if(!doc2.selectFirst("div>img").attr("data-src").equals(""))eventItem.setImageUrl(getWebLink()+doc2.selectFirst("div>img").attr("data-src"));
            }else eventItem.setImageUrl(null);
            eventItem.setName(name.text());
            eventItem.setUrl(EventLink.attr("href"));
            eventItem.setInfo(eventInfo);
            eventItem.setDescription(description);
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
    public JSONObject scrapeInfoEvent1(Document doc) {
        JSONObject info = new JSONObject();
        Elements tr = doc.select("table > tbody > tr > td > table > tbody >tr:nth-child(4)>td>table>tbody>tr"); // Lấy ra các thẻ tr của bảng infobox
        if (tr != null && tr.size()>0) {
            for (Element element : tr) {
                JSONObject infoItem = new JSONObject();
                Element th = element.selectFirst("td:nth-child(1)");
                Element td = element.selectFirst("td:nth-child(2)");
                //Elements tdGroup = element.select("");
                if (th != null && td != null) {
                    infoItem.put("name", td.text());
                    info.put(th.text(), infoItem);
                }
            }
        }
        Elements trKey = doc.select("table > tbody > tr > td > table > tbody > tr[valign]");
        Elements thKey = doc.select("table > tbody > tr > td > table > tbody > tr > th");
        //System.out.println(tr.size());
        for (int i=0 ; i<trKey.size() ; i++) {
            Elements tdchild_1 = trKey.get(i).select("td:nth-child(1)");
            Elements tdChild_2 = trKey.get(i).select("td:nth-child(2)");
            JSONObject url = new JSONObject();
            Elements hrefs_1 = tdchild_1.select("a");
            Elements hrefs_2 = tdChild_2.select("a");
            if(hrefs_1.size()==0 && hrefs_2.size()==0) {
                JSONObject info1 = new JSONObject();
                info1.put("phe 1", tdchild_1.text());
                info1.put("phe 2",tdChild_2.text());
             info.put(thKey.get(i).text(), info1);
            }
            if(hrefs_1.size()!=0||hrefs_2.size()!=0){
            if(hrefs_1.size()!=0){
            for (Element href_1 : hrefs_1){
                JSONObject hrefObject = new JSONObject();
                hrefObject.put("name",href_1.text());
                hrefObject.put("url", href_1.attr("href"));
                url.put("phe 1",hrefObject);
                }
            }else url.put("phe 1",tdchild_1.text());
            if(hrefs_2.size()!=0){
            for (Element href_2 : hrefs_2){
                JSONObject hrefObject = new JSONObject();
                hrefObject.put("name",href_2.text());
                hrefObject.put("url", href_2.attr("href"));
                url.put("phe 2",hrefObject);
                }
            }else url.put("phe 1",tdchild_1.text());
            info.put(thKey.get(i).text(), url);
            }
    }
        return info;
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
