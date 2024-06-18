package Gruppe3.server.model.CompositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class GameStateId implements Serializable {

    private Long game; // Corresponds to Game entity's gameId

    private int gamePlayerID; // Changed to int

    // Constructors, getters, setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStateId that = (GameStateId) o;
        return Objects.equals(game, that.game) &&
                Objects.equals(gamePlayerID, that.gamePlayerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, gamePlayerID);
    }
}