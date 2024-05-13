package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.model.*;
import java.util.List;

/**
 * Provides functionality to load players into a game of RoboRally.
 * This class includes methods for initializing player instances on the board,
 * setting their positions, and loading their command and programming cards.
 * @Author: Karl
 */

public class LoadPlayers {

    /**
         * Loads a player onto a specified board using the details provided in a PlayerTemplate.
         * Initializes the player at the specified coordinates and adds them to the board if the position is valid.
         * Also, loads command and programming cards for the player.
         *
         * @param result the board where the player will be added
         * @param playerTemplate the template containing the player's details
         * @Author: Karl
         */
    public static void loadPlayer(Board result, PlayerTemplate playerTemplate) {
        Player player = new Player(result, playerTemplate.color, playerTemplate.name, false);
        int x = playerTemplate.x;
        int y = playerTemplate.y;
        if (x >= 0 && y >= 0 && x < result.width && y < result.height) {
            Space space = result.getSpace(x, y);
            if (space != null) {
                player.setSpace(space);
                // Add the player to the board after setting its space
                result.addPlayer(player);
            }
        }
        loadCommandCards(player, playerTemplate.commandCards);
        loadProgrammingCards(player, playerTemplate.programmingCards);
    }

    /**
         * Loads the command cards for the specified player based on a list of card templates.
         * Sets each card to a command card field on the player, if the card is valid.
         *
         * @param player the player whose command cards are to be set
         * @param commandCards the list of command card templates
         * @Author: Karl
         */
    private static void loadCommandCards(Player player, List<CommandCardFieldTemplate> commandCards){
        int numFields = Math.min(player.getCards().length, commandCards.size());
        for (int i = 0; i < numFields; i++) {
            CommandCardFieldTemplate cardTemplate = commandCards.get(i);
            if (cardTemplate == null || cardTemplate.card == null) continue;
            CommandCardField cardField = player.getCardField(i); // Get the existing CommandCardField
            try {
                Command command = Command.fromDisplayName(cardTemplate.card);
                cardField.setCard(new CommandCard(command)); // Set the card to the existing CommandCardField
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command card name: " + cardTemplate.card);
                continue;
            }
            cardField.setVisible(cardTemplate.visible); // Set the visibility to the existing CommandCardField
        }
    }


    /**
         * Loads the programming cards for the specified player based on a list of card templates.
         * Sets each card to a programming card field on the player, if the card is valid.
         *
         * @param player the player whose programming cards are to be set
         * @param programmingCards the list of programming card templates
         * @Author: Karl
         */
    private static void loadProgrammingCards(Player player, List<CommandCardFieldTemplate> programmingCards){
        int numFields = Math.min(player.getProgramFieldCount(), programmingCards.size());
        for (int i = 0; i < numFields; i++) {
            CommandCardFieldTemplate cardTemplate = programmingCards.get(i);
            if (cardTemplate == null || cardTemplate.card == null) continue;
            CommandCardField cardField = player.getProgramField(i); // Get the existing CommandCardField
            try {
                Command command = Command.fromDisplayName(cardTemplate.card);
                cardField.setCard(new CommandCard(command)); // Set the card to the existing CommandCardField
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command card name: " + cardTemplate.card);
                continue;
            }
            cardField.setVisible(cardTemplate.visible); // Set the visibility to the existing CommandCardField
        }
    }
}
