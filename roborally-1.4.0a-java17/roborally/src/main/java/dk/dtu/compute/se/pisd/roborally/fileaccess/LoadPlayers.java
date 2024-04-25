package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.model.*;
import java.util.List;

public class LoadPlayers {

    public static void loadPlayer(Board result, PlayerTemplate playerTemplate) {
        Player player = new Player(result, playerTemplate.color, playerTemplate.name);
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
            CommandCardField cardField = new CommandCardField(player);
            try {
                Command command = Command.fromDisplayName(cardTemplate.card);
                cardField.setCard(new CommandCard(command));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command card name: " + cardTemplate.card);
                continue;
            }
            cardField.setVisible(cardTemplate.visible);
            player.getCardField(i).setCard(cardField.getCard());
        }
    }
    private static void loadProgrammingCards(Player player, List<CommandCardFieldTemplate> programmingCards){
        int numFields = Math.min(player.getProgramFieldCount(), programmingCards.size());
        for (int i = 0; i < numFields; i++) {
            CommandCardFieldTemplate cardTemplate = programmingCards.get(i);
            if (cardTemplate == null || cardTemplate.card == null) continue;
            CommandCardField cardField = new CommandCardField(player);
            try {
                Command command = Command.fromDisplayName(cardTemplate.card);
                cardField.setCard(new CommandCard(command));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command card name: " + cardTemplate.card);
                continue;
            }
            cardField.setVisible(cardTemplate.visible);
            player.getProgramField(i).setCard(cardField.getCard());
        }
    }
}
