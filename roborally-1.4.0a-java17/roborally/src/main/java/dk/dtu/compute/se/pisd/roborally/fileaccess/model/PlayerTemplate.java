package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import java.util.List;
import java.util.ArrayList;

public class PlayerTemplate {
    public String name;
    public String color;
    public int x;
    public int y;
    public String heading;
    public List<CommandCardFieldTemplate> cards = new ArrayList<>();


    public PlayerTemplate(Player player) {
        this.name = player.getName();
        this.color = player.getColor();
        this.x = player.getSpace().x;
        this.y = player.getSpace().y;
        this.heading = player.getHeading().toString();
        for (CommandCardField cardField : player.getCards()) {
            this.cards.add(new CommandCardFieldTemplate(cardField));
        }
    }
}

