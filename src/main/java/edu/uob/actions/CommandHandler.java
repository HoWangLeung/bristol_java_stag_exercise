package edu.uob.actions;

import edu.uob.GameAction;
import edu.uob.GameState;
import edu.uob.subEntities.Location;
import edu.uob.subEntities.Player;

import java.util.*;

public class CommandHandler {


    public List<String> checkTrigger(List<String> commands, Player player, TreeMap<String, Location> locationMap, TreeMap<String, HashSet<GameAction>> actionMap, GameState gameState) {
        List<String> intersection = findIntersection(commands, new ArrayList<>(actionMap.keySet()));
        List<Subject> subjects = new ArrayList<>();
        List<Consumed> requiredConsume = new ArrayList<>();
        List<Subject> requiredSubjects = new ArrayList<>();
        List<Produced> producedList= new ArrayList<>();
        List<String> allLocations = new ArrayList<>(locationMap.keySet());
        List<String> results =  new ArrayList<>();


        if (intersection.size() == 0) {
            try {
                throw new Exception("something wrong1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String triggerword = intersection.get(0);

        System.out.println("triggerword===" + triggerword);
        HashSet<GameAction> targetActions = actionMap.get(triggerword);


        targetActions.forEach(targetAction -> {
            for (int i = 0; i < targetAction.getSubjects().size(); i++) {
                if (commands.contains(targetAction.getSubjects().get(i))) {
                    System.out.println(targetAction.getSubjects().get(i) + "<<<<>>>>>");
                    subjects.add(targetAction.getSubjects().get(i));
                }
            }
        });

        if (subjects.size() == 0) {
            System.out.println("throw error");
        }

        //check required consume (item)
        if(!checkRequiredSubjects(targetActions,requiredSubjects,player)){
            System.out.println("throw error!");
            results.add("You do not have the require item");
            return results;
        }

        //check required consume (location)

        //handle produced
        targetActions.forEach(targetAction -> {
            for (int i = 0; i < targetAction.getProduced().size(); i++) {
                producedList.add(new Produced(targetAction.getProduced().get(i)));
                System.out.println(targetAction.getNarration()+ " =narr");
                results.add(targetAction.getNarration());
            }
        });
        System.out.println("allLocations="+allLocations);

        //determine the type of produced
        for(int i =0;i<producedList.size();i++){
            if(allLocations.contains(producedList.get(i).getName())){
                producedList.get(i).setType("location");
            }else{
                producedList.get(i).setType("others");
            }
        }
        producedList.forEach(d-> System.out.println(d.getName() + ":" + d.getType()));
        //get narration

       String currentLocationName = gameState.getCurrentLocation().getName();
      // locationMap.get(currentLocationName).addPath();



    return results;

    }

    private boolean checkRequiredSubjects(HashSet<GameAction> targetActions, List<Subject> requiredSubjects, Player player) {


        System.out.println("requiredSubjects="+requiredSubjects);
        targetActions.forEach(targetAction -> {
            targetAction.getSubjects().forEach(c->requiredSubjects.add(c));
        });
        System.out.println("requiredSubjects===>"+requiredSubjects);
        for(int i =0;i<requiredSubjects.size();i++){
            String subjectName =requiredSubjects.get(i).getName();


        }





        System.out.println("player inventory="+ player.getInventory());
        HashMap<String,Integer> requiredConsumeMap = new HashMap<>();
        requiredSubjects.forEach(rc->{
            requiredConsumeMap.put(rc.getName(),0);
        });
//        System.out.println("requiredConsumeMap="+requiredConsumeMap);

        player.getInventory().forEach(item->{
            if(requiredConsumeMap.get(item.getName())!=null ){
                requiredConsumeMap.put(item.getName(), requiredConsumeMap.get(item.getName())+1);
            }
        });
        System.out.println(requiredConsumeMap);

       for(Integer value:requiredConsumeMap.values()){
           if(value!=1){
               System.out.println("return false");
               return false;
           }
       }

        System.out.println("return true");
        return true;
    }

    public <T> List<T> findIntersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

}
