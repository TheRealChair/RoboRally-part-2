package Gruppe3.server.model.CompositeKeys;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Player;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class GameId_GamePlayerId implements Serializable {

    @ManyToOne
    private Game game;

    @ManyToOne
    private Player player;

    public GameId_GamePlayerId() {}

    public GameId_GamePlayerId(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameId_GamePlayerId that = (GameId_GamePlayerId) o;
        return Objects.equals(game, that.game) &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, player);
    }
}
