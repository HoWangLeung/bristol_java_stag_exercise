package edu.uob;

import edu.uob.actions.Consumed;
import edu.uob.actions.Subject;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

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

    public void retriveSubjects(Element subjects){

        int count =0;
        //System.out.println(triggers.getChildNodes() + "<<");
        for(int ii =0;ii<subjects.getChildNodes().getLength();ii++){
            if(subjects.getChildNodes().item(ii).getNodeType()==1){
                count++;
            }
        }
        for(int i = 0 ;i<count;i++){

            String entity = subjects.getElementsByTagName("entity").item(i).getTextContent();
            //System.out.println("entity=" + entity);
            this.subjects.add(new Subject(entity));
        }


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
