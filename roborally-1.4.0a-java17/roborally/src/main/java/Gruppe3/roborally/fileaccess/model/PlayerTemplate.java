package Gruppe3.roborally.fileaccess.model;

import Gruppe3.roborally.model.Player;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a template for serializing and deserializing player data.
 * This class is used to facilitate the conversion of a Player object into a format
 * that can be easily written to and read from a file, specifically for setting up
 * game state or saving it.
 */

public class PlayerTemplate {
    public int gamePlayerID;
    public String color;
    public int x;
    public int y;
    public String heading;
    public List<CommandCardFieldTemplate> programmingCards = new ArrayList<>();
    public List<CommandCardFieldTemplate> commandCards = new ArrayList<>();

    public PlayerTemplate(Player player) {
        this.gamePlayerID = player.getGamePlayerID();
        this.color = player.getColor();
        this.x = player.getSpace().x;
        this.y = player.getSpace().y;
        this.heading = player.getHeading().toString();
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            this.programmingCards.add(new CommandCardFieldTemplate(player.getProgramField(i)));
        }
        for (int i = 0; i < Player.NO_CARDS; i++) {
            this.commandCards.add(new CommandCardFieldTemplate(player.getCardField(i)));
        }
    }
}

