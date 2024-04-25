package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.model.*;

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
        loadPlayerCards(player, playerTemplate);
    }

    private static void loadPlayerCards(Player player, PlayerTemplate playerTemplate){
        for (int i = 0; i < playerTemplate.programmingCards.size(); i++) {
            CommandCardFieldTemplate cardTemplate = playerTemplate.programmingCards.get(i);
            if (cardTemplate == null) continue;
            CommandCardField cardField = new CommandCardField(player);
            cardField.setCard(new CommandCard(Command.valueOf(cardTemplate.card)));
            cardField.setVisible(cardTemplate.visible);
            player.getProgramField(i).setCard(cardField.getCard());
        }
    }
}
