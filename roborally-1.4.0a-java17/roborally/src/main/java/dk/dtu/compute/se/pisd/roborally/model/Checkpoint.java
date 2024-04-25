package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class Checkpoint extends Subject {
    private final int id;

    public Checkpoint(int id) {
        this.id = id;
    }
}
