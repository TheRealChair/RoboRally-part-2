package Gruppe3.server.repository;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Score;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import Gruppe3.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepo extends JpaRepository<Score, GameId_GamePlayerId> {
    Optional<Score> findByGameAndPlayer(Game game, Player player);
}