package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Player;

public class PlayerTemplate {
    public String name;
    public String color;
    public int x;
    public int y;
    public String heading;

    public PlayerTemplate(Player player) {
        this.name = player.getName();
        this.color = player.getColor();
        this.x = player.getSpace().x;
        this.y = player.getSpace().y;
        this.heading = player.getHeading().toString();
    }
}
