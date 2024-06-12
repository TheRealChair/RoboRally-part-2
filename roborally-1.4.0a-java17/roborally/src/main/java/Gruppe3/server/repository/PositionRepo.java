package Gruppe3.server.repository;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Player;
import Gruppe3.server.model.Position;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PositionRepo extends JpaRepository<Position, Long> {
    Optional<Position> findByGameIdAndPlayerId(Game game, Player player);
}
