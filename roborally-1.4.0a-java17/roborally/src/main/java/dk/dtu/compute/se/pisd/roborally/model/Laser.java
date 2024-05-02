package dk.dtu.compute.se.pisd.roborally.model;

public class Laser {
    private int damage;

    public Laser(int damage) {
        this.damage = damage;
    }

    public void applyDamage(Player player) {
        player.takeDamage(damage);
    }
}
