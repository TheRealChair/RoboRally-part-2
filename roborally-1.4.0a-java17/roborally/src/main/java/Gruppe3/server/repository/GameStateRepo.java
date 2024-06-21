package Gruppe3.server.repository;

import Gruppe3.server.model.CompositeKeys.GameStateId;
import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import Gruppe3.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameStateRepo extends JpaRepository<GameState, GameStateId> {
    Optional<GameState> findByGameAndGamePlayerIdAndRegister(Game game, int gamePlayerId, int register);
    List<GameState> findByGameAndGamePlayerId(Game game, int gamePlayerId);
    List<GameState> findByGame(Game game);
    List<GameState> findByGameAndRegister(Game game, int register);

    @Query("SELECT gs FROM GameState gs WHERE gs.game.id = :gameId")
    List<GameState> findByGameId(Long gameId);
}

