package Gruppe3.server.repository;

import Gruppe3.server.model.GameState;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStateRepo extends JpaRepository<GameState, GameId_GamePlayerId> {
    GameState findByGameId_GamePlayerId(GameId_GamePlayerId id);
}