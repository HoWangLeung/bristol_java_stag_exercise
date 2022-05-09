package edu.uob.subEntities;

import edu.uob.GameEntity;

public class Character extends GameEntity {
    private String shape="ellipse";

    public Character(String name, String description) {
        super(name, description);
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
