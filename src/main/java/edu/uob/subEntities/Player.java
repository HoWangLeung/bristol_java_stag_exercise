package edu.uob.subEntities;

import edu.uob.Exception.GameException;
import edu.uob.GameEntity;
import edu.uob.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Player extends GameEntity {
    private List<Artefact> inventory = new ArrayList<>();
    private Location currentLocation;
    private int health=3;


    public Player(String name, String description) {
        super(name, description);
    }

    public List<Artefact> getInventory() {
        return inventory;
    }

    public void addToInventory(Artefact artefact) {
        this.inventory.add(artefact);
    }

    public void setInventory(List<Artefact> inventory) {
        this.inventory = inventory;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void listInventory(GameState gameState){
        StringBuilder inventoryResult = new StringBuilder();
        inventoryResult.append("Your inventory has the following items:\n");
        gameState.getCurrentPlayer().getInventory().forEach(i -> {

            inventoryResult.append(i.getDescription() + "\n");
        });
        gameState.setResponse(inventoryResult.toString());
    }

    public void dropItemFromInventory(GameState gameState, String target) throws GameException {
        Player player = gameState.getCurrentPlayer();

        String finalTarget = target;
        List<Artefact> filtered = player.getInventory().stream().filter(inv -> inv.getName().equals(finalTarget)).collect(Collectors.toList());

        if (filtered.size() == 0) {
            throw new GameException("Your inventory does not contain the item: " + target);
        }

        player.getInventory().remove(filtered.get(0));
        gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getArefacts().add(filtered.get(0));

        gameState.setResponse("You have dropped a " + target);
    }

    public void lookLocation(GameState gameState, List<String> commands) throws GameException {

        if (commands.size() > 1) {
            throw new GameException("Invalid command");
        }

        if (gameState.getPlayerList().size() == 1) {
            gameState.setResponse(singplePlayerLook(gameState));


        } else if (gameState.getPlayerList().size() > 1) {
            gameState.setResponse(multiplayerLook(gameState));

        }

    }

    private String singplePlayerLook(GameState gameState) {
        StringBuilder stringBuilder = new StringBuilder();

        Location currentLocation = gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName());
        String name = currentLocation.getName();
        String description = currentLocation.getDescription();
        String locationDescription = "You are in " + description + " You can see:\n";
        stringBuilder.append(locationDescription);



        if (currentLocation.getArefacts().size() > 0) {

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

    private String multiplayerLook(GameState gameState) {
        StringBuilder stringBuilder = new StringBuilder();

        gameState.getPlayerList().forEach(player -> {

            Location currentLocation = gameState.getLocationMap().get(player.getCurrentLocation().getName());
            String name = currentLocation.getName();
            String description = currentLocation.getDescription();
            String locationDescription = player.getName() + ": You are in " + description + " You can see:\n";
            stringBuilder.append(locationDescription);



            if (currentLocation.getArefacts().size() > 0) {

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



    @Override
    public String toString() {
        return "Player{" +
                "inventory=" + Arrays.toString(inventory.toArray()) +
                '}';
    }

    public void goToLocation(GameState gameState, String target) throws GameException {

        StringBuilder gotoResult = new StringBuilder();
        String curLocationName = gameState.getCurrentPlayer().getCurrentLocation().getName();
        Location currentLocation = gameState.getLocationMap().get(curLocationName);

        System.out.println("going");
        boolean isAccessable = currentLocation.getPaths().stream().filter(path -> path.getName().equals(target)).collect(Collectors.toList()).size()>0;

        if(!isAccessable){
            throw new GameException("You cannot access the place");
        }


        gameState.getCurrentPlayer().setCurrentLocation(gameState.getLocationMap().get(target));

        currentLocation = gameState.getCurrentPlayer().getCurrentLocation();
        String description = gameState.getCurrentPlayer().getCurrentLocation().getDescription();
        String locationDescription = "You are in " + description + " You can see:\n";
        gotoResult.append(locationDescription);
        gameState.getCurrentPlayer().getCurrentLocation().getArefacts().forEach(d -> gotoResult.append(d.getDescription() + "\n"));
        gameState.getCurrentPlayer().getCurrentLocation().getFurnitures().forEach(d -> gotoResult.append(d.getDescription() + "\n"));

        if (currentLocation.getCharacters().size() > 0) {
            currentLocation.getCharacters().forEach(character -> gotoResult.append(character.getDescription() + "\n"));
        }

        gotoResult.append("You can access from here:\n");
        gameState.getCurrentPlayer().getCurrentLocation().getPaths().forEach(p -> gotoResult.append(p.getName() + "\n"));

        gameState.setResponse(gotoResult.toString());
    }

    public void showHealth(){

    }


    public void handleDeath(GameState gameState) {
        if(gameState.getCurrentPlayer().getHealth()==0){
            gameState.setResponse("you died and lost all of your items, you must return to the start of the game");
            gameState.getCurrentPlayer().setHealth(3);
        }
    }
}
