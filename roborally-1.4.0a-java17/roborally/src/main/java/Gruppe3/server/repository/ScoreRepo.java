package Gruppe3.server.repository;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.score;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import Gruppe3.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepo extends JpaRepository<score, GameId_GamePlayerId> {
    Optional<score> findByGameAndPlayer(Game game, Player player);
}