package dk.dtu.compute.se.pisd.roborally.model;

public class Laser {

    private final Heading direction;
    private final int x;
    private final int y;
    private final int strength;

    public Laser(int x, int y, Heading direction, int strength) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.strength = strength;
    }

    public Heading getDirection() {
        return direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getStrength() {
        return strength;
    }
}
