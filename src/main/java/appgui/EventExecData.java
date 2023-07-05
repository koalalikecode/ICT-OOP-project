package appgui;

import historyobject.Event;

import historyobject.HistoryObject;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.Set;

public class EventExecData  extends ExecData{
    public EventExecData(List<HistoryObject> events) {
        this.historyObjectList = events;
//        this.hyperlinkTexts = getHyperlinkTexts(events);
    }

}


