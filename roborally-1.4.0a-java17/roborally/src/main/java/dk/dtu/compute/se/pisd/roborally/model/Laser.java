package dk.dtu.compute.se.pisd.roborally.model;

public class Laser {
    private int x;
    private int y;
    private Heading direction;
    private int strength;

    public Laser(int x, int y, Heading direction, int strength) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.strength = strength;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Heading getDirection() {
        return direction;
    }

    public int getStrength() {
        return strength;
    }
}
