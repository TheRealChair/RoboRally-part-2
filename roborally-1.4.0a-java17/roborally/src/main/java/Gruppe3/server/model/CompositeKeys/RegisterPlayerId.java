package Gruppe3.server.model.CompositeKeys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class RegisterPlayerId implements Serializable {
    private Long registerId;
    private Long playerId;

    // Default constructor
    public RegisterPlayerId() {}

    // Parameterized constructor
    public RegisterPlayerId(Long registerId, Long playerId) {
        this.registerId = registerId;
        this.playerId = playerId;
    }

    // Getters and setters
    public Long getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Long registerId) {
        this.registerId = registerId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    // hashCode and equals methods
    @Override
    public int hashCode() {
        return Objects.hash(registerId, playerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterPlayerId that = (RegisterPlayerId) o;
        return Objects.equals(registerId, that.registerId) &&
                Objects.equals(playerId, that.playerId);
    }
}
