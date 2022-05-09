package edu.uob.actions;

import edu.uob.Exception.GameException;
import edu.uob.GameAction;
import edu.uob.GameState;
import edu.uob.subEntities.*;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler {


    public List<String> checkTrigger(List<String> commands, Player player, GameState gameState) {
        List<String> intersection = findIntersection(commands, new ArrayList<>(gameState.getActionMap().keySet()));
        List<Subject> subjects = new ArrayList<>();
        List<Consumed> requiredConsume = new ArrayList<>();
        List<Subject> requiredSubjects = new ArrayList<>();
        List<Produced> producedList = new ArrayList<>();
        List<String> allLocations = new ArrayList<>(gameState.getActionMap().keySet());
        List<String> results = new ArrayList<>();


        if (intersection.size() == 0) {
            try {
                throw new Exception("something wrong1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String triggerword = intersection.get(0);

        System.out.println("triggerword===" + triggerword);
        HashSet<GameAction> targetActions = gameState.getActionMap().get(triggerword);


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

        //check required subjects (item)
        if (!checkRequiredSubjects(targetActions, requiredSubjects, player,gameState)) {
            System.out.println("Does not have required subjects");
            results.add("You do not have the require item");
            return results;
        }


        //handle consume
        handleConsume(targetActions,requiredConsume,player,gameState);


        //handle produced
        targetActions.forEach(targetAction -> {
            for (int i = 0; i < targetAction.getProduced().size(); i++) {
                producedList.add(new Produced(targetAction.getProduced().get(i)));
                System.out.println(targetAction.getNarration() + " =narr");
                results.add(targetAction.getNarration());
            }
        });

        producedList.forEach(prod->{
            String producedName =prod.getName();
            System.out.println("prod name="+producedName);
            String shape = gameState.getLocationMap().get(producedName).getShape();
            String description = gameState.getLocationMap().get(producedName).getDescription();
            System.out.println("desc="+description);

            if(shape.equalsIgnoreCase("none")){ //it must be a location
                Location location = gameState.getLocationMap().get(gameState.getCurrentLocation().getName());
                List<Path> pathsToUpdate = location.getPaths();
                pathsToUpdate.add(new Path(producedName,description));
                location.setPaths(pathsToUpdate);
                gameState.getLocationMap().put(location.getName(),location);
            }
        });

        //gameState.getLocationMap().get("cabin").getPaths().forEach(d-> System.out.println(d.getName() + "new possible"));



        //get narration

        String currentLocationName = gameState.getCurrentLocation().getName();
        // locationMap.get(currentLocationName).addPath();


        return results;

    }

    private void handleConsume(HashSet<GameAction> targetActions, List<Consumed> requiredConsume, Player player, GameState gameState) {

        targetActions.forEach(targetAction -> {
            targetAction.getConsumed().forEach(consume -> requiredConsume.add(consume));
        });

        HashMap<String,Consumed> consumeMap = new HashMap<>();

        requiredConsume.forEach(s-> consumeMap.put(s.getName(),s));


        for(int i =0;i<player.getInventory().size();i++){
            if(consumeMap.containsKey(player.getInventory().get(i).getName())){
                List<Artefact> newInventory = player.getInventory();
                Artefact tobeRemoved = player.getInventory().get(i);
                newInventory.remove(tobeRemoved);;
                player.setInventory(newInventory);
            }
        }






    }

    private boolean checkRequiredSubjects(HashSet<GameAction> targetActions, List<Subject> requiredSubjects, Player player, GameState gameState) {
        System.out.println("checkRequiredSubjects()");


        System.out.println("requiredSubjects=" + requiredSubjects);
        targetActions.forEach(targetAction -> {
            targetAction.getSubjects().forEach(c -> requiredSubjects.add(c));
        });

        for (int i = 0; i < requiredSubjects.size(); i++) {
            boolean existInFurnitrue = false;
            boolean existInArtefact=false;
            boolean existInCharacter=false;

            String subjectName = requiredSubjects.get(i).getName();
            String shape = requiredSubjects.get(i).getShape();
            System.out.println("required....>>>>>" + requiredSubjects.get(i).getName() +":"+requiredSubjects.get(i).getShape());

            if (shape.equalsIgnoreCase("hexagon")) { //furnitures
                 existInFurnitrue = gameState.getCurrentLocation().getFurnitures()
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;


            } else if (shape.equalsIgnoreCase("diamond")) {//artefact

                existInArtefact = player.getInventory()
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;

            } else if (shape.equalsIgnoreCase("ellipse")) {//characters

                existInCharacter = gameState.getCurrentLocation().getCharacters()
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;

            }
            System.out.println("existInFurnitrue:"+existInFurnitrue);
            System.out.println("existInArtefact:"+existInArtefact);
            System.out.println("existInCharacter:"+existInCharacter);

            if(!existInFurnitrue && !existInArtefact && !existInCharacter){
                return false;
            }


        }

        return true;


    }



    public String checkBasicCommand(List<String> commands, Player player, GameState gameState) throws GameException {
        System.out.println("checkBasicCommand");
        String firstWord = commands.get(0);
        StringBuilder stringBuilder;
        Location currentLocation;
        String name;
        String description;
        String locationDescription;


        switch (firstWord){
            case "inv":
            case "inventory":
                System.out.println("inventory...");
                System.out.println("ans "+ player.getInventory().toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("Your inventory has the following items:\n");
                player.getInventory().forEach(i->stringBuilder.append(i.getDescription()+"\n"));


                return stringBuilder.toString();

            case "get":

                String target = commands.get(1);
                List<Artefact> targetArtefect = gameState.getLocationMap().get(gameState.getCurrentLocation().getName()).getArefacts().stream().filter(artefact -> artefact.getName().equalsIgnoreCase(target)).collect(Collectors.toList());



                if(targetArtefect.size()>0){
                    System.out.println("found");
                    player.addToInventory(targetArtefect.get(0));
                }else{
                    System.out.println("no such");
                    throw new GameException("No such artefact in this location");
                }

                return "You picked up a " + targetArtefect.get(0).getName();
            case "drop":
                System.out.println("drop...");
                break;
            case "goto":
                System.out.println("goto...");
                stringBuilder = new StringBuilder();
                target = commands.get(1);
                System.out.println("target goto="+target);
                gameState.setCurrentLocation(gameState.getLocationMap().get(target));

                currentLocation = gameState.getCurrentLocation();
                description = gameState.getCurrentLocation().getDescription();
                locationDescription = "You are in " + description + " You can see:\n";
                stringBuilder.append(locationDescription);
                gameState.getCurrentLocation().getArefacts().forEach(d-> stringBuilder.append(d.getDescription()+"\n")    );
                gameState.getCurrentLocation().getFurnitures().forEach(d-> stringBuilder.append(d.getDescription()+"\n")    );

                if(currentLocation.getCharacters().size()>0){
                    currentLocation.getCharacters().forEach(character -> stringBuilder.append(character.getDescription()+"\n"));
                }

                stringBuilder.append("You can access from here:\n");
                gameState.getCurrentLocation().getPaths().forEach(p-> stringBuilder.append(p.getName()+"\n"));




                return stringBuilder.toString();

            case "look":
                System.out.println("look...");
                stringBuilder = new StringBuilder();
                currentLocation = gameState.getLocationMap().get(gameState.getCurrentLocation().getName());
                name = currentLocation.getName();
                description = currentLocation.getDescription();
                locationDescription = "You are in " + description + " You can see:\n";
                stringBuilder.append(locationDescription);

                System.out.println("currentLocation.getArefacts()="+currentLocation.getArefacts().size());




                if(currentLocation.getArefacts().size()>0){
                    System.out.println("has art");
                    currentLocation.getArefacts().forEach(artefact -> stringBuilder.append(artefact.getDescription()+"\n"));
                }

                if(currentLocation.getFurnitures().size()>0){
                    currentLocation.getFurnitures().forEach(furniture -> stringBuilder.append(furniture.getDescription()+"\n"));
                }

                if(currentLocation.getCharacters().size()>0){
                    currentLocation.getCharacters().forEach(character -> stringBuilder.append(character.getDescription()+"\n"));
                }






                stringBuilder.append("You can access from here:\n");
                currentLocation.getPaths().forEach(p-> stringBuilder.append(p.getName()+"\n"));




                return stringBuilder.toString();

            default:
                System.out.println("nothing match...");
                return null;
        }
        return null;

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
