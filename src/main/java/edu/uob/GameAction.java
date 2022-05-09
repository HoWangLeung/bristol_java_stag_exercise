package edu.uob;

import edu.uob.actions.Consumed;
import edu.uob.actions.Subject;
import edu.uob.subEntities.Artefact;
import edu.uob.subEntities.Character;
import edu.uob.subEntities.Furniture;
import edu.uob.subEntities.Location;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class GameAction
{
    private List<String> triggers = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();
    private List<Consumed> consumed = new ArrayList<>();
    private List<String> produced = new ArrayList<>();

    private String narration;


    public GameAction() {
    }


    public List<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }


    public List<Consumed> getConsumed() {
        return consumed;
    }



    public List<String> getProduced() {
        return produced;
    }

    public void setProduced(List<String> produced) {
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

        //map the shape
        HashMap<String,Subject> subjectsMap= new HashMap<>();
        for(int i =0;i<this.subjects.size();i++){
            subjectsMap.put(this.subjects.get(i).getName(),this.subjects.get(i));
        }


        ArrayList<String> locationList = new ArrayList<>(locationMap.keySet());
        for(int i =0;i<locationList.size();i++){
            Location location = locationMap.get(locationList.get(i));
            List<Furniture> furnitures = location.getFurnitures();
            List<Artefact> artefacts = location.getArefacts();
            List<Character> characters = location.getCharacters();

            furnitures.forEach(furniture -> {
                if(subjectsMap.containsKey(furniture.getName())){
                    subjectsMap.get(furniture.getName()).setShape(furniture.getShape());
                }
            });


            artefacts.forEach(artefact -> {
                if(subjectsMap.containsKey(artefact.getName())){
                    subjectsMap.get(artefact.getName()).setShape(artefact.getShape());
                }
            });

            characters.forEach(character -> {
                if(subjectsMap.containsKey(character.getName())){
                    subjectsMap.get(character.getName()).setShape(character.getShape());
                }
            });




        }
       // this.subjects.forEach(s-> System.out.println(s.getName() + ":" +s.getShape()));


        // System.out.println("==============end of ret sub");
    }

    public void retriveConsumed(Element consumed){

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


        // System.out.println("==============end of ret sub");
    }

    public void retriveProduced(Element produced){

        int count =0;
        //System.out.println(triggers.getChildNodes() + "<<");
        for(int ii =0;ii<produced.getChildNodes().getLength();ii++){
            if(produced.getChildNodes().item(ii).getNodeType()==1){
                count++;
            }
        }
        for(int i = 0 ;i<count;i++){

            String entity = produced.getElementsByTagName("entity").item(i).getTextContent();
            //System.out.println("entity=" + entity);
            this.produced.add(entity);
        }


        // System.out.println("==============end of ret sub");
    }

    public void retriveNarration(Element narration){

            setNarration(narration.getTextContent());



        // System.out.println("==============end of ret sub");
    }

}
