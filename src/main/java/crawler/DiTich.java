package crawler;

import historyobject.Character;
import historyobject.Place;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiTich extends Crawler {
    public DiTich() {
        setWebLink("http://ditich.vn/FrontEnd/DiTich/Form?do=&ItemId=");
        setFolder("data/diTich.json");
    }
    public void scrapePage(List<Place> placeList) {
        for(int id = 1865; id<=6193; id++){
            String pageToScrape = getWebLink() + Integer.toString(id);
            Document doc;
            try {
                doc = Jsoup.connect(pageToScrape).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements divItems = doc.select("div.hl__illustrated-list__list-item>span:not(.hl__illustrated-list__image-wrapper)");
            if(doc.select("h2.hl__comp-heading.hl__comp_heading_custom").size()<3) continue;
            Place diTich = new Place();
            String name = doc.select("div.hl__library-info__features>section>h2").text();
            diTich.setName(name);
            diTich.setUrl(pageToScrape);
            JSONObject info = new JSONObject();
            if(doc.selectFirst("div.hl__contact-info__address>span.address-line1")!=null)
            info.put("Địa chỉ",doc.selectFirst("div.hl__contact-info__address>span.address-line1").text());
            else info.put("Địa chỉ","không rõ");
//            Elements divItems = doc.select("div.hl__illustrated-list__list-item>span:not(.hl__illustrated-list__image-wrapper)");
            for(int i=0; i<divItems.size(); i++) {
                String divText = divItems.get(i).text();
                if(divText.equals("")) continue;
                //System.out.println(divText);
                String []parts = divText.split(":",2);
                String key = parts[0];
                String value = parts[1];
                info.put(key, value);
            }
            diTich.setInfo(info);
            placeList.add(diTich);
            System.out.println("Done "+ pageToScrape);
        }
    }
    @Override
    public void crawlData() throws InterruptedException {
        List<Place> crawlObjectList = Collections.synchronizedList(new ArrayList<>());
        //Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());
        // initializing the ExecutorService to run the
        // web scraping process in parallel on 4 pages at a time
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        scrapePage(crawlObjectList);
        // waiting up to 300 seconds for all pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);
        setOutput(new JSONArray(crawlObjectList));
        System.out.println("Crawled " + getOutput().length() + " characters");

    }
}
