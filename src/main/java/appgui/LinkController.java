package appgui;

import historyobject.*;

public class LinkController {
    protected static HistoryObject selectedObject;
    protected static String selectedObjectName;

    public static void setSelectedObject(String name, String fieldName){
        if (fieldName.equals("Character")){
            selectedObjectName = name;
        } else if (fieldName.equals("Event")){
            selectedObjectName = name;
        } else if (fieldName.equals("Dynasty")){
            selectedObjectName = name;
        } else if (fieldName.equals("FestivalWiki")){
            selectedObjectName = name;
        } else if (fieldName.equals("PlaceNKS")){
            selectedObjectName = name;
        }
    }

}
