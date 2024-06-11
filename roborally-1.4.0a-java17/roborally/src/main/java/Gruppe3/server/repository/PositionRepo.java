package Gruppe3.server.repository;

import Gruppe3.server.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepo extends JpaRepository<Position, Long> {
    Position findByGameIdAndPlayerId(Long gameId, Long playerId);
}
