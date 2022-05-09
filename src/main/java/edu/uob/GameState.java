package edu.uob;

import edu.uob.subEntities.Location;

public class GameState {

    public GameState() {

    }

    private Location currentLocation;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
