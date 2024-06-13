package Gruppe3.server.repository;

import Gruppe3.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepo extends JpaRepository<Player, Long> {
    Player findByPlayerId(Long playerId);

    // count the number of players in a game
    int countByGame_GameId(Long gameId);
}
