package Gruppe3.server.repository;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import Gruppe3.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameStateRepo extends JpaRepository<GameState, GameId_GamePlayerId> {
    Optional<GameState> findByGameAndPlayer(Game game, Player player);
}
