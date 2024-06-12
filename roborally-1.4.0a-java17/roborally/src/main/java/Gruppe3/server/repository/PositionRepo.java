package Gruppe3.server.repository;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Player;
import Gruppe3.server.model.Position;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PositionRepo extends JpaRepository<Position, GameId_GamePlayerId> {
    @Query("SELECT p FROM Position p WHERE p.game.gameId = :gameId AND p.player.playerId = :playerId")
    Optional<Position> findByGameIdAndPlayerId(@Param("gameId") Long gameId, @Param("playerId") Long playerId);
}
