package crawler;
import historyobject.Character;
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
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class  AnhHungLLVT extends Crawler {
    public AnhHungLLVT(){
        setWebLink("https://vi.wikipedia.org");
        setFolder("data/AHLLVT.json");
        setStartLink("/wiki/Danh_s%C3%A1ch_Anh_h%C3%B9ng_L%E1%BB%B1c_l%C6%B0%E1%BB%A3ng_v%C5%A9_trang_nh%C3%A2n_d%C3%A2n");
        setPageLimit(1);
    }

    public void scrapePage(String pageToScrape, List<Character> CharacterList) {
        List<Character> characterList = new ArrayList<>();
        System.setProperty("webdriver.chrome.driver","D:\\Documents\\Downloads\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("file.encoding", "UTF-8");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-extensions");
        options.addArguments("--charset=UTF-8");
        WebDriver driver = new ChromeDriver(options);
        driver.get(pageToScrape);
        List<WebElement> tables = driver.findElements(By.cssSelector("table.wikitable.sortable"));
        for(WebElement table: tables) {
            WebElement clickBtn = table.findElement(By.className("headerSort"));
            clickBtn.click();
        }
        String html = driver.getPageSource();
        Document doc = Jsoup.parse(html);
        Elements trRoot = doc.select("table.wikitable>tbody>tr");
        //System.out.println(trRoot.size());
        for(int i=0; i<trRoot.size(); i++ ){
            Character AHLLVTChar = new Character();
            Element link = trRoot.get(i).selectFirst("tr>td:nth-child(1)>a:not(.external.text):not(.new)");
            if(link==null) {
                JSONObject infoNew = new JSONObject();
                AHLLVTChar.setName(trRoot.get(i).selectFirst("tr>td:nth-child(1)").text());
                infoNew.put("Năm sinh - Năm mất",trRoot.get(i).selectFirst("tr>td:nth-child(2)").text());
                infoNew.put("Dân tộc",trRoot.get(i).selectFirst("tr>td:nth-child(3)").text());
                infoNew.put("Quê quán", trRoot.get(i).selectFirst("tr>td:nth-child(4)").text());
                infoNew.put("Năm phong", trRoot.get(i).selectFirst("tr>td:nth-child(5)").text());
                infoNew.put("Chức vụ và thành tích", trRoot.get(i).selectFirst("tr>td:nth-child(6)").text());
                AHLLVTChar.setInfo(infoNew);
                CharacterList.add(AHLLVTChar);
                System.out.println("Done "+ trRoot.get(i).selectFirst("tr>td:nth-child(1)").text());
                continue;
            }
            String name = link.text();
            AHLLVTChar.setName(name);
            String characterURL = link.attr("href");
            //AHLLVTChar.setUrl(characterURL);
            Document doc2;
            try {
                characterURL = URLDecoder.decode(characterURL, "UTF-8");
                doc2= Jsoup.connect("https://vi.wikipedia.org" + characterURL).userAgent("").header("Accept-Language", "*").timeout(10000).get();
//                characterURL = URLDecoder.decode(characterURL, "UTF-8");
                //System.out.println(characterURL);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            AHLLVTChar.setUrl(characterURL);
            String description = doc2.select("div.mw-parser-output > p").text();
            AHLLVTChar.setDescription(description);
            //AHLLVTChar.setInfo(new JSONObject(scrapeInfoBox(doc2)));
            Elements tr = doc2.select("table.infobox > tbody >tr");
            JSONObject info = new JSONObject();
            if(tr.size()==0) {
                //info.put("name",trRoot.get(i).selectFirst("tr>td:nth-child(1)"));
                info.put("Năm sinh - Năm mất",trRoot.get(i).selectFirst("tr>td:nth-child(2)").text());
                info.put("Dân tộc",trRoot.get(i).selectFirst("tr>td:nth-child(3)").text());
                info.put("Quê quán", trRoot.get(i).selectFirst("tr>td:nth-child(4)").text());
                info.put("Năm phong", trRoot.get(i).selectFirst("tr>td:nth-child(5)").text());
                info.put("Chức vụ và thành tích", trRoot.get(i).selectFirst("tr>td:nth-child(6)").text());
            }
            else {
                for(int r=0; r<tr.size(); r++) {
                    Element thChild = tr.get(r).selectFirst("th");
                    Element tdChild = tr.get(r).selectFirst("td");
                    JSONObject td = new JSONObject();
                    if(thChild!=null && tdChild!=null) {
                        info.put(thChild.text(),tdChild.text());
                    }
                }
            }
                AHLLVTChar.setInfo(info);
                CharacterList.add(AHLLVTChar);
            System.out.println("Done "+ characterURL);
        }

    }
    public void crawlData() throws InterruptedException {
        List<Character> crawlObjectList = Collections.synchronizedList(new ArrayList<>());
        //Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());
        String pagesToScrape = getWebLink()+getStartLink() ;
        // initializing the ExecutorService to run the
        // web scraping process in parallel on 4 pages at a time
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        scrapePage(pagesToScrape,crawlObjectList);
        // waiting up to 300 seconds for all pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);
        setOutput(new JSONArray(crawlObjectList));
        System.out.println("Crawled " + getOutput().length() + " characters");
    }

}

