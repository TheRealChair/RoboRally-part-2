package Gruppe3.server.model.CompositeKeys;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class GameId_GamePlayerId implements Serializable {
    private Long gameId;
    private Long gamePlayerId;

    // Default constructor
    public GameId_GamePlayerId() {}

    // Parameterized constructor
    public GameId_GamePlayerId(Long gameId, Long gamePlayerId) {
        this.gameId = gameId;
        this.gamePlayerId = gamePlayerId;
    }

    // Getters and setters
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getGamePlayerId() {
        return gamePlayerId;
    }

    public void setGamePlayerId(Long gamePlayerId) {
        this.gamePlayerId = gamePlayerId;
    }

    // hashCode and equals methods
    @Override
    public int hashCode() {
        return Objects.hash(gameId, gamePlayerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameId_GamePlayerId that = (GameId_GamePlayerId) o;
        return Objects.equals(gameId, that.gameId) &&
                Objects.equals(gamePlayerId, that.gamePlayerId);
    }
}
