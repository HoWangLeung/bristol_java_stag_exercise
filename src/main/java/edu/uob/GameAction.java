package edu.uob;

import edu.uob.actions.Actions;
import edu.uob.actions.Consumed;
import edu.uob.actions.Produced;
import edu.uob.actions.Subject;
import edu.uob.subEntities.*;
import edu.uob.subEntities.Character;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class GameAction
{
    private List<String> triggers = new ArrayList<>();
    private List<Actions> subjects = new ArrayList<>();
    private List<Actions> consumed = new ArrayList<>();
    private List<Actions> produced = new ArrayList<>();

    private String narration;


    public GameAction() {
    }


    public List<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public List<Actions> getSubjects() {
        return subjects;
    }


    public List<Actions> getConsumed() {
        return consumed;
    }



    public List<Actions> getProduced() {
        return produced;
    }

    public void setProduced(List<Actions> produced) {
        this.produced = produced;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public void retriveTriggers(Element triggers){



                int count =0;
                //System.out.println(triggers.getChildNodes() + "<<");
                for(int ii =0;ii<triggers.getChildNodes().getLength();ii++){
                    if(triggers.getChildNodes().item(ii).getNodeType()==1){
                        count++;
                    }
                }
                for(int i = 0 ;i<count;i++){
                    String keyword = triggers.getElementsByTagName("keyword").item(i).getTextContent();
                    this.triggers.add(keyword);
                }


    }

    public void retriveProduced(Element produced, TreeMap<String, Location> locationMap, GameState gameState){

        int count =0;
        for(int ii =0;ii<produced.getChildNodes().getLength();ii++){
            if(produced.getChildNodes().item(ii).getNodeType()==1){
                count++;
            }
        }
        for(int i = 0 ;i<count;i++){
            String entity = produced.getElementsByTagName("entity").item(i).getTextContent();
            Produced producedEntity = new Produced(entity);
            this.produced.add(producedEntity);
        }

       this.mapEntityShape(locationMap,  this.produced);
//        storeToStoreRoom(gameState,this.produced);

    }

//    private void storeToStoreRoom(GameState gameState, List<Actions> produced){
//
//        this.produced.forEach(prod->{
//
//            if(prod.getShape() !=null && prod.getShape().equals("diamond")){
//              gameState.getStoreroom().addArtefact(new Artefact(prod.getName(),""));
//            }
//            if(prod.getShape() !=null &&  prod.getShape().equals("hexagon")){
//                gameState.getStoreroom().addFurniture(new Furniture(prod.getName(),""));
//            }
//            if(prod.getShape() !=null &&  prod.getShape().equals("ellipse")){
//                gameState.getStoreroom().addCharacter(new Character(prod.getName(),""));
//            }
//
//        });
//
//    }

    private void mapEntityShape(TreeMap<String, Location> locationMap, List<Actions> actionEntity) {
        //map the shape
        HashMap<String, Actions> entityMap= new HashMap<>();
        for(int i =0;i<actionEntity.size();i++){

            entityMap.put(actionEntity.get(i).getName(),actionEntity.get(i));
        }

        ArrayList<String> locationList = new ArrayList<>(locationMap.keySet());
        for(int i =0;i<locationList.size();i++){
            Location location = locationMap.get(locationList.get(i));
            List<Furniture> furnitures = location.getFurnitures();
            List<Artefact> artefacts = location.getArefacts();
            List<Character> characters = location.getCharacters();
            List<Location> allLocations = new ArrayList<>(locationMap.keySet()).stream().map(key->locationMap.get(key)).collect(Collectors.toList());

            furnitures.forEach(furniture -> {
                if(entityMap.containsKey(furniture.getName())){
                    entityMap.get(furniture.getName()).setShape(furniture.getShape());
                }
            });


            artefacts.forEach(artefact -> {
                //  System.out.println("art==>"+artefact.getName());
                if(entityMap.containsKey(artefact.getName())){
                    entityMap.get(artefact.getName()).setShape(artefact.getShape());
                }
            });

            characters.forEach(character -> {
                if(entityMap.containsKey(character.getName())){
                    entityMap.get(character.getName()).setShape(character.getShape());
                }
            });

            allLocations.forEach(loc -> {
                if(entityMap.containsKey(loc.getName())){
                    entityMap.get(loc.getName()).setShape(loc.getShape());
                }
            });
        }


    }

    public void retriveSubjects(Element subjects, TreeMap<String, Location> locationMap){

        int count =0;
        //System.out.println(triggers.getChildNodes() + "<<");
        for(int ii =0;ii<subjects.getChildNodes().getLength();ii++){
            if(subjects.getChildNodes().item(ii).getNodeType()==1){
                count++;
            }
        }
        for(int i = 0 ;i<count;i++){
            String entity = subjects.getElementsByTagName("entity").item(i).getTextContent();
            Subject subject = new Subject(entity);
            this.subjects.add(subject);
        }

        this.mapEntityShape(locationMap,  this.subjects);
//        //map the shape
//        HashMap<String,Subject> subjectsMap= new HashMap<>();
//        for(int i =0;i<this.subjects.size();i++){
//            subjectsMap.put(this.subjects.get(i).getName(),(Subject) this.subjects.get(i));
//        }
//
//
//        ArrayList<String> locationList = new ArrayList<>(locationMap.keySet());
//        for(int i =0;i<locationList.size();i++){
//            Location location = locationMap.get(locationList.get(i));
//            List<Furniture> furnitures = location.getFurnitures();
//            List<Artefact> artefacts = location.getArefacts();
//            List<Character> characters = location.getCharacters();
//
//            furnitures.forEach(furniture -> {
//                if(subjectsMap.containsKey(furniture.getName())){
//                    subjectsMap.get(furniture.getName()).setShape(furniture.getShape());
//                }
//            });
//
//
//            artefacts.forEach(artefact -> {
//                if(subjectsMap.containsKey(artefact.getName())){
//                    subjectsMap.get(artefact.getName()).setShape(artefact.getShape());
//                }
//            });
//
//            characters.forEach(character -> {
//                if(subjectsMap.containsKey(character.getName())){
//                    subjectsMap.get(character.getName()).setShape(character.getShape());
//                }
//            });
//
//
//
//
//        }

    }

    public void retriveConsumed(Element consumed, TreeMap<String, Location> locationMap){

        int count =0;
        //System.out.println(triggers.getChildNodes() + "<<");
        for(int ii =0;ii<consumed.getChildNodes().getLength();ii++){
            if(consumed.getChildNodes().item(ii).getNodeType()==1){
                count++;
            }
        }
        for(int i = 0 ;i<count;i++){

            String entityName = consumed.getElementsByTagName("entity").item(i).getTextContent();
            //System.out.println("entity=" + entity);
            Consumed newConsumed = new Consumed(entityName);
            this.consumed.add(newConsumed);
        }
        this.mapEntityShape(locationMap,  this.consumed);

        // System.out.println("==============end of ret sub");
    }



    public void retriveNarration(Element narration){

            setNarration(narration.getTextContent());



        // System.out.println("==============end of ret sub");
    }

}
