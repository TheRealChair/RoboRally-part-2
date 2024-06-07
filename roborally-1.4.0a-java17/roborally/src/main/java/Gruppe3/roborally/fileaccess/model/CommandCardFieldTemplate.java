package Gruppe3.roborally.fileaccess.model;

import Gruppe3.roborally.model.CommandCardField;

/**
 * Represents a template for serializing and deserializing command card field data.
 * This class is used to capture the state of a command card field within a player's control panel,
 * including the card name and its visibility.
 * @Author: Karl
 */
public class CommandCardFieldTemplate {
    public String card;
    public boolean visible;

    /**
         * Constructs a new CommandCardFieldTemplate from a given CommandCardField object.
         * This constructor captures the current state of the command card (if any) and its visibility,
         * facilitating easy serialization and deserialization.
         * @param cardField the command card field from which to construct the template
         * @Author: Karl
         */
    public CommandCardFieldTemplate(CommandCardField cardField) {
        if (cardField.getCard() != null) {
            this.card = cardField.getCard().getName();
        } else {
            this.card = null;
        }
        this.visible = cardField.isVisible();
    }
}
