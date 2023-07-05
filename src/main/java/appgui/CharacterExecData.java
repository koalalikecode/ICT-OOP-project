package appgui;

import historyobject.Character;
import historyobject.HistoryObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CharacterExecData extends ExecData{
    public CharacterExecData(List<HistoryObject> historyObjects) {
        this.historyObjectList = historyObjects;
        this.hyperlinkTexts = getHyperlinkTexts(historyObjects);
    }

}
