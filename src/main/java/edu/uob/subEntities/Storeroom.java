package edu.uob.subEntities;

import edu.uob.Exception.GameException;
import edu.uob.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Storeroom {

    private List<Artefact> artefacts = new ArrayList<>();
    private List<Furniture> furnitures = new ArrayList<>();
    private List<Character> characters = new ArrayList<>();



    public Storeroom() {
    }

    public void getTargetArtefact(GameState gameState, List<String> commands) throws GameException {
        String target = commands.get(1);
        String finalTarget1 = target;
        List<Artefact> targetArtefect = gameState.getLocationMap().get(gameState.getCurrentPlayer().getCurrentLocation().getName()).getArefacts().stream()
                .filter(artefact -> artefact.getName().equalsIgnoreCase(finalTarget1)).collect(Collectors.toList());


        if (targetArtefect.size() > 0) {
            System.out.println("found");
            gameState.getCurrentPlayer().addToInventory(targetArtefect.get(0));
            gameState.removeItemFromLocation(targetArtefect.get(0));

        } else {
            System.out.println("no such");
            throw new GameException("No such artefact in this location");
        }
        gameState.setResponse("You picked up a " + targetArtefect.get(0).getName());
    }






    public List<Artefact> getArtefacts() {
        return artefacts;
    }

    public void setArtefacts(List<Artefact> artefacts) {
        this.artefacts = artefacts;
    }

    public void addArtefact(Artefact artefact) {
        this.artefacts.add(artefact);
    }

    public List<Furniture> getFurnitures() {
        return furnitures;
    }

    public void setFurnitures(List<Furniture> furnitures) {
        this.furnitures = furnitures;
    }

    public void addFurniture(Furniture furniture) {
        this.furnitures.add(furniture);
    }
    public void addCharacter(Character character) {
        this.characters.add(character);
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

}
