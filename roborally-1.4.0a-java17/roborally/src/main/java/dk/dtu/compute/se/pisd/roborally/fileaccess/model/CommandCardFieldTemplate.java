package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;

public class CommandCardFieldTemplate {
    public String card;
    public boolean visible;

    public CommandCardFieldTemplate(CommandCardField cardField) {
        if (cardField.getCard() != null) {
            this.card = cardField.getCard().getName();
        }
        this.visible = cardField.isVisible();
    }
}
