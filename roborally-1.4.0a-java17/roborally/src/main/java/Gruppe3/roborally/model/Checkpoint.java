package Gruppe3.roborally.model;

import Gruppe3.designpatterns.observer.Subject;

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
