package dataprocessing;

public class ProcessingRunner {
    public static void main(String[] args) {
        DataProcessing characterProcess = new DataProcessing("data/characterNKS.json", "data/kingWiki.json",
                "processed_data/character.json", "c");
        DataProcessing eventProcess = new DataProcessing("data/eventsNKS.json", "data/eventWiki.json",
                "processed_data/event.json", "e");
        DataProcessing eventProcess2 = new DataProcessing("processed_data/event.json", "data/warWiki.json",
                "processed_data/event.json", "e");
        DataProcessing placeProcess = new DataProcessing("data/placeNKS.json", "data/placeWiki.json",
                "processed_data/place.json", "p");
        DataProcessing festivalProcess = new DataProcessing("data/FestivalWiki.json",
                "processed_data/festival.json", "f");
        DataProcessing dynastyProcess = new DataProcessing("data/dynastyWiki.json",
                "processed_data/dynasty.json", "d");

//        characterProcess.processAndSave();
//        eventProcess.processAndSave();
//        eventProcess2.processAndSave();
//        placeProcess.processAndSave();
//        festivalProcess.processAndSave();
        dynastyProcess.processAndSave();
    }

}
