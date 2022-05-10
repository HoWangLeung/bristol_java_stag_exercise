package edu.uob.subEntities;

import java.util.ArrayList;
import java.util.List;

public class Storeroom {

    private List<Artefact> artefacts = new ArrayList<>();
    private List<Furniture> furnitures = new ArrayList<>();
    private List<Character> characters = new ArrayList<>();



    public Storeroom() {
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
