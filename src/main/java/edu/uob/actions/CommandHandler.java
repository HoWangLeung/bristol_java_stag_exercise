package edu.uob.actions;

import edu.uob.Exception.GameException;
import edu.uob.GameAction;
import edu.uob.GameState;
import edu.uob.subEntities.*;

import edu.uob.subEntities.Character;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler {


    public void checkTrigger(List<String> commands, GameState gameState) {
        List<String> intersection = findIntersection(commands, new ArrayList<>(gameState.getActionMap().keySet()));
        List<Subject> subjects = new ArrayList<>();
        List<Consumed> requiredConsume = new ArrayList<>();
        List<Subject> requiredSubjects = new ArrayList<>();
        List<Produced> producedList = new ArrayList<>();
        List<String> allLocations = new ArrayList<>(gameState.getLocationMap().keySet());
        List<String> results = new ArrayList<>();
        Player currentPlayer = gameState.getCurrentPlayer();


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
            // gameState.setResponse("some errors");
//            return;
        }

        //check required subjects (item)
        if (!checkRequiredSubjects(targetActions, requiredSubjects, currentPlayer, gameState)) {
            System.out.println("Does not have required subjects");
            results.add("You do not have the require item");
            gameState.setResponse(results.get(0));
            return;
        }


        //handle consume
        handleConsume(targetActions, requiredConsume, currentPlayer, gameState);


        //handle produced
        targetActions.forEach(targetAction -> {
            for (int i = 0; i < targetAction.getProduced().size(); i++) {
                producedList.add((Produced) targetAction.getProduced().get(i));
            }
            gameState.setResponse(targetAction.getNarration());
        });


        producedList.forEach(prod -> {


            String name = prod.getName();
            String shape = prod.getShape();

            System.out.println("produced name=" + name + ",shape=" + shape);

            if (name.equalsIgnoreCase("health")) { //
                if (currentPlayer.getHealth() < 3)
                    currentPlayer.setHealth(currentPlayer.getHealth() + 1);

            } else if (shape.equalsIgnoreCase("none")) { //it must be a location
                Location location = gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName());
                List<Path> pathsToUpdate = location.getPaths();
                pathsToUpdate.add(new Path(name, gameState.getLocationMap().get(name).getDescription()));
                location.setPaths(pathsToUpdate);
                gameState.getLocationMap().put(location.getName(), location);
            } else if (shape.equals("diamond")) { //
                Set<Artefact> artefacts = new HashSet<>();
                allLocations.forEach(location -> {
                    List<Artefact> targetArtefactList = gameState.getLocationMap()
                            .get(location).getArefacts()
                            .stream().filter(artefact -> artefact.getName().equals(name)).collect(Collectors.toList());

                    if (targetArtefactList.size() > 0) {
                        System.out.println("found any?");
                        artefacts.add(targetArtefactList.get(0));
                    }
                });
                artefacts.forEach(artefact -> currentPlayer.addToInventory(artefact));
            } else if (shape.equals("ellipse")) { //Character
                Set<Character> characters = new HashSet<>();
                allLocations.forEach(location -> {
                    List<Character> targetCharacterList = gameState.getLocationMap()
                            .get(location).getCharacters()
                            .stream().filter(character -> character.getName().equals(name)).collect(Collectors.toList());

                    if (targetCharacterList.size() > 0) {
                        System.out.println("found any?");
                        characters.add(targetCharacterList.get(0));
                    }
                });
                characters.forEach(character -> gameState.getLocationMap()
                        .get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getCharacters().add(character));


            }


        });


    }

    private void handleConsume(HashSet<GameAction> targetActions, List<Consumed> requiredConsume, Player player, GameState gameState) {
        Location currentLocation = gameState.getCurrentPlayer().getCurrentLocation();

        Player currentPlayer = gameState.getCurrentPlayer();

        targetActions.forEach(targetAction -> {
            targetAction.getConsumed().forEach(consume -> requiredConsume.add((Consumed) consume));

        });


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

            System.out.println("consumedconsumed=" + consumed.getName());
            if (consumed.getName().equals("health")) { //health

                System.out.println("do somthing");
                currentPlayer.setHealth(currentPlayer.getHealth() - 1);
                if (currentPlayer.getHealth() == 0) {
                    System.out.println("drop all items and return to starting location");
                    currentPlayer.getInventory().forEach(artefact -> {

                        gameState.getLocationMap().get(currentLocation.getName()).getArefacts().add(artefact);

                    });
                    currentPlayer.getInventory().clear();
                    gameState.setResponse("Your health is 0");
                    currentPlayer.setCurrentLocation(gameState.getStartingLocation());


                }


            } else if (consumed.getShape().equals("hexagon")) { //furniture
                Furniture tobeRemoved = gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getFurnitures()
                        .stream().filter(furniture -> furniture.getName().equals(consumed.getName()))
                        .collect(Collectors.toList()).get(0);
                gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getFurnitures().remove(tobeRemoved);
            }

//            if (consumed.getShape().equals("diamond")) { //artefact
//                System.out.println("shape=diamond??");
//
//                List<Artefact> artefactTobeRemovedList = gameState.getLocationMap().get(gameState.getCurrentLocation().getName()).getArefacts()
//                        .stream().filter(artefact -> artefact.getName().equals(consumed.getName()))
//                        .collect(Collectors.toList());
//
//                if(artefactTobeRemovedList.size()>0)
//                gameState.getLocationMap().get(gameState.getCurrentLocation().getName()).getArefacts().remove(artefactTobeRemovedList.get(0));
//
//            }


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
                existInFurnitrue = gameState.getCurrentPlayer().getCurrentLocation().getFurnitures()
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;


            } else if (shape.equalsIgnoreCase("diamond")) {//artefact

                existInArtefact = player.getInventory()
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;

            } else if (shape.equalsIgnoreCase("ellipse")) {//characters

                existInCharacter = gameState.getCurrentPlayer().getCurrentLocation().getCharacters()
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;

            }


            if (!existInFurnitrue && !existInArtefact && !existInCharacter) {
                return false;
            }


        }

        return true;


    }


    public void checkBasicCommand(List<String> commands, GameState gameState) throws GameException {
        System.out.println("checkBasicCommand");

        String firstWord = commands.get(0);

        Location currentLocation;
        String name;
        String description;
        String locationDescription;
        Player player = gameState.getCurrentPlayer();


        switch (firstWord) {
            case "inv":
            case "inventory":
                gameState.getCurrentPlayer().listInventory(gameState);
                break;
            case "get":
                gameState.getStoreroom().getTargetArtefact(gameState, commands);
                break;
            case "drop":
                String target = commands.get(1);
                gameState.getCurrentPlayer().dropItemFromInventory(gameState,target);
                break;
            case "goto":
                String targetLocation = commands.get(1);
                gameState.getCurrentPlayer().goToLocation(gameState,targetLocation);
                break;

            case "look":
                System.out.println("look...");
                if (commands.size() > 1) {
                    throw new GameException("Invalid command");
                }

                if (gameState.getPlayerList().size() == 1) {
                    gameState.setResponse(singplePlayerLook(gameState));


                } else if (gameState.getPlayerList().size() > 1) {
                    gameState.setResponse(multiplayerLook(gameState));

                }
                break;

            case "health":
                if (commands.size() > 1) {
                    throw new GameException("health is a built in command, which cannot be followed by any command");
                }
                gameState.getCurrentPlayer().getHealth();

                gameState.setResponse("Your current health is " + gameState.getCurrentPlayer().getHealth());

                break;
            default:
                gameState.setResponse(null);
        }


    }

    private String multiplayerLook(GameState gameState) {
        StringBuilder stringBuilder = new StringBuilder();

        gameState.getPlayerList().forEach(player -> {

            Location currentLocation = gameState.getLocationMap().get(player.getCurrentLocation().getName());
            String name = currentLocation.getName();
            String description = currentLocation.getDescription();
            String locationDescription = player.getName() + ": You are in " + description + " You can see:\n";
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


        });


        return stringBuilder.toString();
    }

    private String singplePlayerLook(GameState gameState) {
        StringBuilder stringBuilder = new StringBuilder();

        Location currentLocation = gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName());
        String name = currentLocation.getName();
        String description = currentLocation.getDescription();
        String locationDescription = "You are in " + description + " You can see:\n";
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
