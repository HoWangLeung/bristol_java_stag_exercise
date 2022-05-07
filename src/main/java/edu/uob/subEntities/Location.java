package edu.uob.subEntities;

import edu.uob.GameEntity;

import java.util.ArrayList;
import java.util.List;

public class Location extends GameEntity {

    List<Character> characters = new ArrayList<>();
    List<Artefact> arefacts = new ArrayList<>();
    List<Furniture> furnitures = new ArrayList<>();
    List<Path> paths = new ArrayList<>();

    public Location(String name, String description) {
        super(name, description);
    }



    public List<Character> getCharacters() {
        return characters;
    }

    public void addCharacters(Character character) {
        this.characters.add(character);
    }

    public List<Artefact> getArefacts() {
        return arefacts;
    }

    public void addArefacts(Artefact arefact) {
        this.arefacts.add(arefact);
    }

    public List<Furniture> getFurnitures() {
        return furnitures;
    }

    public void addFurnitures(Furniture furniture) {
        this.furnitures.add(furniture);
    }




    public void addPath(Path path) {
        this.paths.add(path);
    }

    public List<Path> getPaths() {
        return paths;
    }
}
