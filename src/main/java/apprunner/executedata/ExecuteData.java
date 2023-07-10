package apprunner.executedata;

import historyobject.HistoryObject;

import java.util.List;

public class ExecuteData {

    protected String dataJson = "processed_data/final.json";
    protected List<HistoryObject> historyObjects;
    protected List<String> hyperlinkTexts;

    public List<HistoryObject> getHistoryObjects() {
        return historyObjects;
    }

    public void setHistoryObjects(List<HistoryObject> historyObjects) {
        this.historyObjects = historyObjects;
    }

    public void setHyperlinkTexts(List<String> hyperlinkTexts) {
        this.hyperlinkTexts = hyperlinkTexts;
    }


}
