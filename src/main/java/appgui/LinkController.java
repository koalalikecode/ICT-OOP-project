package appgui;

import historyobject.*;
import historyobject.Character;

public class LinkController {
    protected static HistoryObject selectedCharacter;
    protected static String selectedCharacterName;
    protected static Event selectedEvent;
    protected static String selectedEventName;
    protected static Dynasty selectedDynasty;
    protected static String selectedDynastyName;
    protected static Festival selectedFestival;
    protected static String selectedFestivalName;
    protected static Place selectedPlace;
    protected static String selectedPlaceName;

    public static void setSelectedObject(String name, String fieldName){
        if (fieldName.equals("Character")){
            selectedCharacterName = name;
        } else if (fieldName.equals("Event")){
            selectedEventName = name;
        } else if (fieldName.equals("Dynasty")){
            selectedDynastyName = name;
        } else if (fieldName.equals("FestivalWiki")){
            selectedFestivalName = name;
        } else if (fieldName.equals("Place")){
            selectedPlaceName = name;
        }
    }

}
