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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
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
            
            JSONObject eventInfo1 = scrapeInfoEvent1(doc2, "table > tbody > tr > td > table > tbody >tr:nth-child(4)>td>table>tbody>tr");
            JSONObject eventInfo2 = scrapeInfoEvent2(doc2, "table > tbody > tr > td > table > tbody >tr");
            JSONObject eventInfo = new JSONObject();
            eventInfo.append("",eventInfo1);
            eventInfo.append("",eventInfo2);

           // eventInfo.put("",eventInfo1);
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
    public JSONObject scrapeInfoEvent1(Document doc, String cssQuery) {
        JSONObject info = new JSONObject();
        Elements tr = doc.select(cssQuery); // Lấy ra các thẻ tr của bảng infobox
        //String"tr:nth-child(3) td";
        if (tr != null && tr.size()>0) {
            for (Element element : tr) {
                JSONObject infoItem = new JSONObject();
                Element th = element.selectFirst("td:nth-child(1)");
                Element td = element.selectFirst("td:nth-child(2)");
                //Elements tdGroup = element.select("");
                if (th != null && td != null) {
                    //Element urlConnect = td.selectFirst("a");
                    infoItem.put("name", td.text());
                    /*if (urlConnect != null) {
                        infoItem.put("url", urlConnect.attr("href"));
                    }*/
                    info.put(th.text(), infoItem);
                } /*else if (th == null && tdGroup.size() > 1) {
                    Element urlConnect = tdGroup.get(1).selectFirst("a");
                    infoItem.put("name", tdGroup.get(1).text());
                    if (urlConnect != null) {
                        infoItem.put("url", urlConnect.attr("href"));
                    }
                    info.put(tdGroup.get(0).text(), infoItem);
                }*/
            }
        }
        return info;
    }

    public JSONObject scrapeInfoEvent2(Document doc, String cssQuery) {
        JSONObject info = new JSONObject();
        
        // Elements tr = doc.select("table > tbody > tr > td > table > tbody > tr[valign]");
        // //Elements url = doc.select("table > tbody > tr > td > table > tbody > tr[valign]>td");
        // Elements thKey = doc.select("table > tbody > tr > td > table > tbody > tr > th");
        // //Elements group = doc.select("table > tbody > tr > td > table > tbody > tr > th"); 
        // for (int i = 0; i < tr.size(); i++) {
        //     //if
        //     info.put(thKey.get(i).text(), tr.get(i).text());
        //     //Element url = group.get(i).text()
            

        // }
        // return info;    
        Elements tr = doc.select("table > tbody > tr > td > table > tbody > tr[valign]");
        Elements thKey = doc.select("table > tbody > tr > td > table > tbody > tr > th");
        
        // for(int i= 0; i < tr.size(); i++) {
        //     info.put(thKey.get(i).text(),tr.get(i).text());
        //     //System.out.println(info);

        // }
    
        for (int i = 0; i < tr.size(); i++) {
            Elements td = tr.get(i).select("td");
            JSONObject url = new JSONObject();
            Elements hrefs = td.select("a");
            //System.out.println(hrefs);
            //if(hrefs==null)
             info.append (thKey.get(i).text(),tr.get(i).text());
             //else{
            for (Element href : hrefs){
                JSONObject hrefObject = new JSONObject();
                hrefObject.append("name",href.text());
                hrefObject.append("url", href.attr("href"));
                url.append("url", hrefObject);
            }
            //info.put("url",url.get(1).selectFirst("a[href]").text());
            //info.put("name", thKey.text());
            info.append(thKey.get(i).text(), url);
        //}
    }
        return info;
       //System.out.println(info);
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
