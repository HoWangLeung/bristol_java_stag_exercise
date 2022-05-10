package edu.uob.subEntities;

import edu.uob.GameEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    public String toString() {
        return "Player{" +
                "inventory=" + Arrays.toString(inventory.toArray()) +
                '}';
    }
}
