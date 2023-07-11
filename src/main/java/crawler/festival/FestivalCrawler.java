package crawler.festival;
import crawler.Crawler;
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

import historyobject.Festival;


public class FestivalCrawler extends Crawler {

    public FestivalCrawler() {
            setWebLink("https://vi.wikipedia.org");
            setFolder("data/Festival.json");
            setStartLink("/wiki/L%E1%BB%85_h%E1%BB%99i_Vi%E1%BB%87t_Nam");
        }
    public void scrapePage(String pageToScrape, List<Festival> festivalList) {
        Document doc;
//        System.out.println(pageToScrape);//
        try {
            pageToScrape = URLDecoder.decode(pageToScrape, "UTF-8");
            doc = Jsoup.connect(pageToScrape).userAgent("").header("Accept-Language", "*").get();
//            System.out.println(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements trRoot = doc.select("table.prettytable.wikitable>tbody>tr");
        //System.out.println(trRoot.size());
        for(int i=1; i<trRoot.size(); i++) {
            Festival festival = new Festival();
             festival.setName(trRoot.get(i).selectFirst("td:nth-child(3)").text());
//             System.out.println(festival.getName());
//             String link = null;
             Document doc2;
             String nhanvatUrl[] = new String[100];
//             for(int l=0; l< nhanvatUrl.length; l++){
//                 System.out.println(nhanvatUrl[l]);
//             }
             Element link = trRoot.get(i).selectFirst("td:nth-child(3)>a:not(.new)");

             if(link==null){
                 JSONObject infoNew = new JSONObject();
                 if (!trRoot.get(i).selectFirst("td:nth-child(1)").text().equals("")) {
                     infoNew.put("Ngày bắt đầu (âm lịch)",trRoot.get(i).selectFirst("td:nth-child(1)").text());
                 } //else infoNew.put("Ngày bắt đầu (âm lịch)", "không rõ");
                 if (!trRoot.get(i).selectFirst("td:nth-child(2)").text().equals("")) {
                     infoNew.put("Vị trí",trRoot.get(i).selectFirst("td:nth-child(2)").text());
                 } //else infoNew.put("Vị trí", "không rõ");
                 if (!trRoot.get(i).selectFirst("td:nth-child(5)").text().equals("")) {
                     JSONObject  nhanvat = new JSONObject();
                     nhanvat.put("name",trRoot.get(i).selectFirst("td:nth-child(5)").text());
                     if(nhanvatUrl!=null) nhanvat.put("url",nhanvatUrl);
                     infoNew.put("Nhân vật liên quan",nhanvat);
                 } //else infoNew.put("Nhân vật liên quan", "không rõ");
                 if (!trRoot.get(i).selectFirst("td:nth-child(6)").text().equals("")) {
                     infoNew.put("Ghi chú",trRoot.get(i).selectFirst("td:nth-child(6)").text());
                 } //else infoNew.put("Ghi chú", "không rõ");
                 continue;
             }
             String url = link.attr("href");
             String nameSplit = trRoot.get(i).selectFirst("td:nth-child(5)").text();
             String[] nameArray = nameSplit.split(",");
             if(link!=null) {
                 try {
                     url = URLDecoder.decode(url, "UTF-8");
//                     System.out.println(link);
                     doc2 = Jsoup.connect(getWebLink()+url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").get();
                     festival.setUrl(getWebLink()+url);
                     if(doc2.select("div>figure>a>img").size()>0) festival.setImageUrl(doc2.selectFirst("div>figure>a>img").attr("src"));
                     else festival.setImageUrl(null);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
                 int size = 0;
                 if(doc2.select("div.mw-parser-output>ul>li>a").size()>0&&trRoot.get(i).selectFirst("td:nth-child(5)")!=null) {
                     size = doc2.select("div.mw-parser-output>ul>li>a").size();
                     Elements findName = doc2.select("div.mw-parser-output>ul>li>a");
//                     String nameSplit = trRoot.get(i).selectFirst("td:nth-child(5)").text();
//                     String[] nameArray = nameSplit.split(",");
                     for(int v=0; v < nameArray.length; v++) {
//                         System.out.println(nameArray[v]);
                         if(v>0) nameArray[v] = nameArray[v].substring(1);
                         //System.out.println(nameArray[v]);
                         for(int j=0; j<size; j++) {
                            if(findName.get(j).text().equals(nameArray[v])) {
                                nhanvatUrl[v] = findName.get(j).attr("href");
                                continue;
                            }
                        }
                     }
                 }
                 festival.setDescription(doc2.selectFirst("div.mw-parser-output>p").text());
                 JSONObject info = new JSONObject();
                 if (!trRoot.get(i).selectFirst("td:nth-child(1)").text().equals("")) {
                     info.put("Ngày bắt đầu (âm lịch)",trRoot.get(i).selectFirst("td:nth-child(1)").text());
                 } //else info.put("Ngày bắt đầu (âm lịch)", "không rõ");
                 if (!trRoot.get(i).selectFirst("td:nth-child(2)").text().equals("")) {
                     info.put("Vị trí",trRoot.get(i).selectFirst("td:nth-child(2)").text());
                 } //else info.put("Vị trí", "không rõ");
                 if (!trRoot.get(i).selectFirst("td:nth-child(4)").text().equals("")) {
                     info.put("Lần đầu tổ chức năm",trRoot.get(i).selectFirst("td:nth-child(4)").text());
                 } //else info.put("Lần đầu tổ chức năm","không rõ");
                 if (!trRoot.get(i).selectFirst("td:nth-child(5)").text().equals("")) {
                     JSONObject  nhanvat = new JSONObject();
                     List<JSONObject> NvList = new ArrayList<>();
                     for(int k=0; k<nameArray.length; k++){
                         JSONObject nhanvatChild = new JSONObject();
                         nhanvatChild.put("name",nameArray[k]);
                         if(nhanvatUrl[k]!=null) {nhanvatChild.put("url",nhanvatUrl[k]);NvList.add(nhanvatChild);  nhanvatUrl[k]=null; continue;}
                         NvList.add(nhanvatChild);
//                         nhanvatUrl[k]=null;
                     }
                     info.put("Nhân vật liên quan",NvList);

                 } //else info.put("Nhân vật liên quan", "không rõ");
                 if (!trRoot.get(i).selectFirst("td:nth-child(6)").text().equals("")) {
                     info.put("Ghi chú",trRoot.get(i).selectFirst("td:nth-child(6)").text());
                 } //else info.put("Ghi chú", "không rõ");
                 festival.setInfo(info);

             }
            festivalList.add(festival);
            System.out.println("Done "+festival.getName());
        }
    }
    public void crawlData() throws InterruptedException {
        List<Festival> crawlObjectList = Collections.synchronizedList(new ArrayList<>());
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
        System.out.println("Crawled " + getOutput().length() + " festivals");
    }

}


