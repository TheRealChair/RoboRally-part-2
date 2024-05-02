package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

/**
 * A checkpoint on the board.
 @Param id the id of the checkpoint
 @Author Balder Jacobsen

 */
public class Checkpoint extends Subject {
    private final int id;

    public Checkpoint(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
