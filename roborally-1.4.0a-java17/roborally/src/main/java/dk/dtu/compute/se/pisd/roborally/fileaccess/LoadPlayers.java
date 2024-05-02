package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.model.*;
import java.util.List;

public class LoadPlayers {

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
