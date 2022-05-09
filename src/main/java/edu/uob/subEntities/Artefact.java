package edu.uob.subEntities;

import edu.uob.GameEntity;

public class Artefact extends GameEntity {
    private String shape="diamond";
    public Artefact(String name, String description) {
        super(name, description);
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
