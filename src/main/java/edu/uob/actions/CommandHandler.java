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
        List<String> allLocations = new ArrayList<>(gameState.getLocationMap().keySet());
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
                    subjects.add((Subject) targetAction.getSubjects().get(i));
                }
            }
        });

        if (subjects.size() == 0) {
            System.out.println("throw error");
        }

        //check required subjects (item)
        if (!checkRequiredSubjects(targetActions, requiredSubjects, player, gameState)) {
            System.out.println("Does not have required subjects");
            results.add("You do not have the require item");
            return results;
        }


        //handle consume
        handleConsume(targetActions, requiredConsume, player, gameState);


        //handle produced
        targetActions.forEach(targetAction -> {
            for (int i = 0; i < targetAction.getProduced().size(); i++) {
                producedList.add((Produced) targetAction.getProduced().get(i));
                results.add(targetAction.getNarration());
            }
        });


        producedList.forEach(prod -> {
//            //is it a location
//            boolean isLocation = allLocations.stream()
//                    .filter(location -> location.equals(prod.getName())).collect(Collectors.toList()).size() > 0;

            String name = prod.getName();
            String shape = prod.getShape();

            System.out.println("produced name=" + name + ",shape=" + shape);

            if (shape.equalsIgnoreCase("none")) { //it must be a location
                Location location = gameState.getLocationMap().get(gameState.getCurrentLocation().getName());
                List<Path> pathsToUpdate = location.getPaths();
                pathsToUpdate.add(new Path(name, gameState.getLocationMap().get(name).getDescription()));
                location.setPaths(pathsToUpdate);
                gameState.getLocationMap().put(location.getName(), location);
            }

            if (shape.equals("diamond")) { //
                Set<Artefact> artefacts = new HashSet<>();
                allLocations.forEach(location -> {
                    List<Artefact> targetArtefactList = gameState.getLocationMap()
                            .get(location).getArefacts()
                            .stream().filter(artefact -> artefact.getName().equals(name)).collect(Collectors.toList());
                    //    System.out.println("targetArtefactList size="+targetArtefactList.size());
                    if (targetArtefactList.size() > 0) {
                        System.out.println("found any?");
                        artefacts.add(targetArtefactList.get(0));
                    }
                });
                artefacts.forEach(artefact -> player.addToInventory(artefact));
            }

            if (shape.equals("ellipse")) { //Character
//                gameState.getLocationMap().get(gameState.getCurrentLocation().getName())


            }


        });


        return results;

    }

    private void handleConsume(HashSet<GameAction> targetActions, List<Consumed> requiredConsume, Player player, GameState gameState) {

        targetActions.forEach(targetAction -> {
            targetAction.getConsumed().forEach(consume -> requiredConsume.add((Consumed) consume));
        });
        requiredConsume.forEach(consumed -> System.out.println(consumed.getName() + "<required consume>" + consumed.getShape()));

        HashMap<String, Consumed> consumeMap = new HashMap<>();
        requiredConsume.forEach(s -> consumeMap.put(s.getName(), s));


        for (int i = 0; i < player.getInventory().size(); i++) {
            if (consumeMap.containsKey(player.getInventory().get(i).getName())) {
                List<Artefact> newInventory = player.getInventory();
                Artefact tobeRemoved = player.getInventory().get(i);
                newInventory.remove(tobeRemoved);
                ;
                player.setInventory(newInventory);
            }
        }

        requiredConsume.forEach(consumed -> {
            if (consumed.getShape().equals("hexagon")) { //furniture
                Furniture tobeRemoved = gameState.getLocationMap().get(gameState.getCurrentLocation().getName()).getFurnitures()
                        .stream().filter(furniture -> furniture.getName().equals(consumed.getName()))
                        .collect(Collectors.toList()).get(0);
                gameState.getLocationMap().get(gameState.getCurrentLocation().getName()).getFurnitures().remove(tobeRemoved);


            }
        });


    }

    private boolean checkRequiredSubjects(HashSet<GameAction> targetActions, List<Subject> requiredSubjects, Player player, GameState gameState) {
        System.out.println("checkRequiredSubjects()");


        System.out.println("requiredSubjects=" + requiredSubjects);
        targetActions.forEach(targetAction -> {
            targetAction.getSubjects().forEach(c -> requiredSubjects.add((Subject) c));
        });

        for (int i = 0; i < requiredSubjects.size(); i++) {
            boolean existInFurnitrue = false;
            boolean existInArtefact = false;
            boolean existInCharacter = false;

            String subjectName = requiredSubjects.get(i).getName();
            String shape = requiredSubjects.get(i).getShape();
            System.out.println("required....>>>>>" + requiredSubjects.get(i).getName() + ":" + requiredSubjects.get(i).getShape());

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
            System.out.println("existInFurnitrue:" + existInFurnitrue);
            System.out.println("existInArtefact:" + existInArtefact);
            System.out.println("existInCharacter:" + existInCharacter);

            if (!existInFurnitrue && !existInArtefact && !existInCharacter) {
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


        switch (firstWord) {
            case "inv":
            case "inventory":
                System.out.println("inventory...");
                System.out.println("ans " + player.getInventory().toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("Your inventory has the following items:\n");
                player.getInventory().forEach(i -> stringBuilder.append(i.getDescription() + "\n"));

                return stringBuilder.toString();

            case "get":

                String target = commands.get(1);
                List<Artefact> targetArtefect = gameState.getLocationMap().get(gameState.getCurrentLocation().getName()).getArefacts().stream().filter(artefact -> artefact.getName().equalsIgnoreCase(target)).collect(Collectors.toList());


                if (targetArtefect.size() > 0) {
                    System.out.println("found");
                    player.addToInventory(targetArtefect.get(0));
                    gameState.removeItemFromLocation(targetArtefect.get(0));

                } else {
                    System.out.println("no such");
                    throw new GameException("No such artefact in this location");
                }

                return "You picked up a " + targetArtefect.get(0).getName();
            case "drop":
                System.out.println("drop...");
                target = commands.get(1);

                List<Artefact> filtered = player.getInventory().stream().filter(inv -> inv.getName().equals(target)).collect(Collectors.toList());

                if (filtered.size() == 0) {
                    throw new GameException("Your inventory does not contain the item: " + target);
                }

                player.getInventory().remove(filtered.get(0));


                return "You have dropped ";
            case "goto":
                System.out.println("goto...");
                stringBuilder = new StringBuilder();
                target = commands.get(1);
                System.out.println("target goto=" + target);
                gameState.setCurrentLocation(gameState.getLocationMap().get(target));
                currentLocation = gameState.getCurrentLocation();
                description = gameState.getCurrentLocation().getDescription();
                locationDescription = "You are in " + description + " You can see:\n";
                stringBuilder.append(locationDescription);
                gameState.getCurrentLocation().getArefacts().forEach(d -> stringBuilder.append(d.getDescription() + "\n"));
                gameState.getCurrentLocation().getFurnitures().forEach(d -> stringBuilder.append(d.getDescription() + "\n"));

                if (currentLocation.getCharacters().size() > 0) {
                    currentLocation.getCharacters().forEach(character -> stringBuilder.append(character.getDescription() + "\n"));
                }

                stringBuilder.append("You can access from here:\n");
                gameState.getCurrentLocation().getPaths().forEach(p -> stringBuilder.append(p.getName() + "\n"));


                return stringBuilder.toString();

            case "look":
                System.out.println("look...");
                stringBuilder = new StringBuilder();
                currentLocation = gameState.getLocationMap().get(gameState.getCurrentLocation().getName());
                name = currentLocation.getName();
                description = currentLocation.getDescription();
                locationDescription = "You are in " + description + " You can see:\n";
                stringBuilder.append(locationDescription);

                System.out.println("currentLocation.getArefacts()=" + currentLocation.getArefacts().size());


                if (currentLocation.getArefacts().size() > 0) {
                    System.out.println("has art");
                    currentLocation.getArefacts().forEach(artefact -> stringBuilder.append(artefact.getDescription() + "\n"));
                }

                if (currentLocation.getFurnitures().size() > 0) {
                    currentLocation.getFurnitures().forEach(furniture -> stringBuilder.append(furniture.getDescription() + "\n"));
                }

                if (currentLocation.getCharacters().size() > 0) {
                    currentLocation.getCharacters().forEach(character -> stringBuilder.append(character.getDescription() + "\n"));
                }


                stringBuilder.append("You can access from here:\n");
                currentLocation.getPaths().forEach(p -> stringBuilder.append(p.getName() + "\n"));


                return stringBuilder.toString();

            default:
                System.out.println("nothing match...");
                return null;
        }


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
