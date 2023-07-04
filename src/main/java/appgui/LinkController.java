package appgui;

import historyobject.Character;
import historyobject.Event;
import historyobject.Dynasty;
import historyobject.FestivalWiki;
import historyobject.PlaceNKS;

import appgui.CharacterExecData;
import appgui.EventExecData;

public class LinkController {
    protected static Character selectedCharacter;
    protected static String selectedCharacterName;
    protected static Event selectedEvent;
    protected static String selectedEventName;
    protected static Dynasty selectedDynasty;
    protected static String selectedDynastyName;
    protected static FestivalWiki selectedFestivalWiki;
    protected static String selectedFestivalWikiName;
    protected static PlaceNKS selectedPlaceNKS;
    protected static String selectedPlaceNKSName;

    public static void setSelectedObject(String name, String fieldName){
        if (fieldName.equals("Character")){
            selectedCharacterName = name;
        } else if (fieldName.equals("Event")){
            selectedEventName = name;
        } else if (fieldName.equals("Dynasty")){
            selectedDynastyName = name;
        } else if (fieldName.equals("FestivalWiki")){
            selectedFestivalWikiName = name;
        } else if (fieldName.equals("PlaceNKS")){
            selectedPlaceNKSName = name;
        }
    }

}
