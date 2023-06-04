//package crawler;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import historyobject.Dynasty;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DynastyWikiCrawler extends Crawler {
//    private JSONArray data = new JSONArray();
//
//    public DynastyWikiCrawler() {
//        setWebLink("https://vi.wikipedia.org/wiki/L%E1%BB%8Bch_s%E1%BB%AD_Vi%E1%BB%87t_Nam");
//        setFolder("data/dynastyWiki.json");
//    }
//
//    @Override
//    public void crawlData() throws InterruptedException {
//        try {
//            Document doc = Jsoup.connect(getWebLink()).get();
//            Elements com = doc.select(".toccolours>tbody>tr>td>table>tbody>tr");
//            for (Element el : com) {
//                Elements links = el.select("b:has(a)");
//                if (links.size() == 0) continue;
//                for (Element link : links) {
//                    try {
//                        Document document = Jsoup.connect("https://vi.wikipedia.org" + link.firstChild().attr("href")).get();
//                        String name = link.text();
//                        System.out.println(name);
//                        JSONObject tmp = findObject(name);
//                        if (tmp == null) {
//                            JSONObject jsonObject = new JSONObject();
//                            Dynasty dnt = new Dynasty();
//                            dnt.setName(name);
//                            dnt.setStart(getStart(link.nextElementSibling()));
//                            dnt.setEnd(getEnd(link.nextElementSibling()));
//                            dnt.setCapital(getCapital(document));
//                            dnt.setKings(getKings(document));
//                            dnt.loadField(jsonObject);
//                            this.data.put(jsonObject);
//                        } else {
//                            Dynasty dnt = new Dynasty(tmp);
//                            if (dnt.getStart() == null) dnt.setStart(getStart(link.nextElementSibling()));
//                            if (dnt.getEnd() == null) dnt.setEnd(getEnd(link.nextElementSibling()));
//                            if (dnt.getCapital() == null) dnt.setCapital(getCapital(document));
//                            if (dnt.getKings() == null) dnt.setKings(getKings(document));
//                            dnt.loadField(tmp);
//                        }
//                    } catch (Exception e) {
//                        System.out.println("Error getData in DynastyWiki 1");
//                    }
//                }
//                setOutput(data);
//            }
//        } catch (IOException e) {
//            System.out.println("Error getData in DynastyWiki 2");
//        }
//    }
//
//    public JSONObject findObject(String name) {
//        for (int i = 0; i < this.data.length(); ++i) {
//            JSONObject obj = this.data.getJSONObject(i);
//            if (obj.getString("tên").equalsIgnoreCase(name)) {
//                return obj;
//            }
//        }
//        return null;
//    }
//
//    private String getStart(Element el) {
//        if (el != null) {
//            String tmp = el.text();
//            int index1 = tmp.indexOf('(');
//            int index2 = tmp.indexOf(8211);
//            int index3 = tmp.indexOf(')');
//            if (index1 == -1) return null;
//            if (index3 == -1) return null;
//            if (index2 == -1) return tmp.substring(index1 + 1, index3).trim();
//            else return tmp.substring(index1 + 1, index2).trim();
//        }
//        return null;
//    }
//
//    private String getEnd(Element el) {
//        if (el != null) {
//            String tmp = el.text();
//            int index1 = tmp.indexOf('(');
//            int index2 = tmp.indexOf(8211);
//            int index3 = tmp.indexOf(')');
//            if (index1 == -1) return null;
//            if (index3 == -1) return null;
//            if (index2 == -1) return null;
//            else return tmp.substring(index2 + 1, index3).trim();
//        }
//        return null;
//    }
//
//    private String getCapital(Element el) {
//        Elements infoBox = el.select(".infobox");
//        if (infoBox.size() == 0) {
//            return null;
//        } else {
//            Elements items = infoBox.get(0).select(">tbody>tr:contains(thủ đô)");
//            if (items.size() != 0)
//                return items.get(0).select("td").get(0).text();
//            return null;
//        }
//    }
//
//    private List<String> getKings(Element el) {
//        List<String> kings = new ArrayList<>();
//        Elements infoBox = el.select(".infobox");
//        if (infoBox.size() != 0) {
//            Elements list = infoBox.get(0).select("tr.mergedrow:not(:has(td[style='padding-left:0em;text-align:left;']))>td:not(:has(sup))>a[title~=[^0-9]]:not(:has(sup))");
//            for (Element element : list) {
//                kings.add(element.text());
//            }
//            if (kings.size() != 0)
//                return kings;
//        }
//        return null;
//    }
//}
