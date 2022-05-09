package edu.uob.subEntities;

import edu.uob.GameEntity;

public class Furniture extends GameEntity {

    private String shape="hexagon";
    public Furniture(String name, String description) {
        super(name, description);
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
