package edu.uob.actions;

import edu.uob.Exception.GameException;
import edu.uob.GameAction;
import edu.uob.GameState;
import edu.uob.Helper.Helper;
import edu.uob.subEntities.*;

import edu.uob.subEntities.Character;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler {

    private GameAction currentAction = new GameAction();
    private Helper helper = new Helper();


    public void checkTrigger(List<String> commands, GameState gameState) throws GameException {


        List<Subject> requiredSubjects = new ArrayList<>();
        Player currentPlayer = gameState.getCurrentPlayer();


        selectAction(gameState,commands,requiredSubjects);

        //check required subjects (item)
        if (!checkRequiredSubjects( requiredSubjects, currentPlayer, gameState)) {
            gameState.setResponse("You do not have the required item");
            return;
        }


        handleConsume(currentPlayer, gameState);
        handleProduced(gameState);
        currentPlayer.handleDeath(gameState);


    }

    private void selectAction(GameState gameState, List<String> commands, List<Subject> requiredSubjects) throws GameException {

        List<String> intersection = new Helper().findIntersection(commands, new ArrayList<>(gameState.getActionMap().keySet()));
        String triggerword = intersection.get(0);

        HashSet<GameAction> targetActions = gameState.getActionMap().get(triggerword);
        targetActions.forEach(eachAction -> {
            for (int i = 0; i < eachAction.getSubjects().size(); i++) {
                if (commands.contains(eachAction.getSubjects().get(i).getName())) {
                    requiredSubjects.add((Subject) eachAction.getSubjects().get(i));
                    setCurrentAction(eachAction);
                }
            }
        });

        if (requiredSubjects.size() == 0) {
            throw new GameException("Not enough info");
        }
    }

    private void handleProduced(GameState gameState) {
        Player currentPlayer = gameState.getCurrentPlayer();
        List<Produced> producedList = new ArrayList<>();
        List<Artefact> storeRoomArtefacts = gameState.getStoreroom().getArtefacts();
        String curLocationName = currentPlayer.getCurrentLocation().getName();

        for (int i = 0; i < getCurrentAction().getProduced().size(); i++) {
            producedList.add((Produced) getCurrentAction().getProduced().get(i));
        }
        gameState.setResponse(getCurrentAction().getNarration());

        producedList.forEach(prod -> {
            String name = prod.getName();
            String shape = prod.getShape();
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

                List<Artefact> targetArtefactList = storeRoomArtefacts.stream().filter(artefact -> artefact.getName().equals(name)).collect(Collectors.toList());
                if (targetArtefactList.size() > 0) {

                    artefacts.add(targetArtefactList.get(0));
                }

                artefacts.forEach(artefact -> gameState.getLocationMap().get(curLocationName).addArefacts(artefact));


            } else if (shape.equals("ellipse")) { //Character
                Set<Character> characters = new HashSet<>();

                    List<Character> targetCharacterList = gameState.getStoreroom().getCharacters()
                            .stream().filter(character -> character.getName().equals(name)).collect(Collectors.toList());

                    if (targetCharacterList.size() > 0) {
                        characters.add(targetCharacterList.get(0));
                    }

                characters.forEach(character -> gameState.getLocationMap()
                        .get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getCharacters().add(character));


            }


        });
    }

    private void handleConsume( Player player, GameState gameState) {
        List<Consumed> requiredConsume = new ArrayList<>();

        getCurrentAction().getConsumed().forEach(consume -> requiredConsume.add((Consumed) consume));



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
            if (consumed.getName().equals("health")) {   //health
               consumeHealth(gameState);
            } else if (consumed.getShape().equals("hexagon")) { //furniture
                Furniture tobeRemoved = gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getFurnitures()
                        .stream().filter(furniture -> furniture.getName().equals(consumed.getName()))
                        .collect(Collectors.toList()).get(0);
                gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getFurnitures().remove(tobeRemoved);

            } else if (consumed.getShape().equals("ellipse")) { //furniture
                Character tobeRemovedChar = gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getCharacters()
                        .stream().filter(character -> character.getName().equals(consumed.getName()))
                        .collect(Collectors.toList()).get(0);
                gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getCharacters().remove(tobeRemovedChar);
            }



        });


    }

    private void consumeHealth(GameState gameState) {
        Player currentPlayer = gameState.getCurrentPlayer();
        Location currentLocation = gameState.getCurrentPlayer().getCurrentLocation();

        currentPlayer.setHealth(currentPlayer.getHealth() - 1);
        if (currentPlayer.getHealth() == 0) {

            currentPlayer.getInventory().forEach(artefact -> {

                gameState.getLocationMap().get(currentLocation.getName()).getArefacts().add(artefact);

            });
            currentPlayer.getInventory().clear();

            currentPlayer.setCurrentLocation(gameState.getStartingLocation());
//            gameState.setResponse("you died and lost all of your items, you must return to the start of the game");

        }
    }

    private boolean checkRequiredSubjects( List<Subject> requiredSubjects, Player player, GameState gameState) throws GameException {


        this.currentAction.getSubjects().forEach(c -> requiredSubjects.add((Subject) c));


        for (int i = 0; i < requiredSubjects.size(); i++) {
            boolean existInFurnitrue = false;
            boolean existInArtefact = false;
            boolean existInCharacter = false;

            String subjectName = requiredSubjects.get(i).getName();
            String shape = requiredSubjects.get(i).getShape();


            if(shape==null){
                throw new GameException("The Game does not have this entity?");
            }

            if (shape.equalsIgnoreCase("hexagon")) { //furnitures

                List<Furniture> furnitures = gameState.getLocationMap().get(player.getCurrentLocation().getName()).getFurnitures();
                existInFurnitrue = furnitures
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;


            } else if (shape.equalsIgnoreCase("diamond")) {//artefact

                existInArtefact = player.getInventory()
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;

            } else if (shape.equalsIgnoreCase("ellipse")) {//characters

                List<Character> characters = gameState.getLocationMap().get(player.getCurrentLocation().getName()).getCharacters();

                existInCharacter = characters
                        .stream().filter(d -> d.getName().equals(subjectName)).collect(Collectors.toList()).size() > 0;

            }


            if (!existInFurnitrue && !existInArtefact && !existInCharacter) {
                return false;
            }


        }

        return true;


    }


    public void checkBasicCommand(List<String> commands, GameState gameState) throws GameException {
        String firstWord = commands.get(0);

        switch (firstWord) {
            case "inv":
            case "inventory":
                helper.checklengthMaxOne(commands);
                gameState.getCurrentPlayer().listInventory(gameState);
                break;
            case "get":
                helper.checklengthMaxTwo(commands);
                gameState.getStoreroom().getTargetArtefact(gameState, commands);
                break;
            case "drop":
                helper.checklengthMaxTwo(commands);
                String target = commands.get(1);
                gameState.getCurrentPlayer().dropItemFromInventory(gameState,target);
                break;
            case "goto":
                helper.checklengthMaxTwo(commands);
                String targetLocation = commands.get(1);
                gameState.getCurrentPlayer().goToLocation(gameState,targetLocation);
                break;

            case "look":
                helper.checklengthMaxOne(commands);
                gameState.getCurrentPlayer().lookLocation(gameState,commands);

                break;

            case "health":
                helper.checklengthMaxOne(commands);
                gameState.getCurrentPlayer().getHealth();

                gameState.setResponse("Your current health is " + gameState.getCurrentPlayer().getHealth());

                break;
            default:
                gameState.setResponse(null);
        }


    }

    public GameAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(GameAction currentAction) {
        this.currentAction = currentAction;
    }
}
