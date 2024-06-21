package Gruppe3.server.repository;

import Gruppe3.server.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepo extends JpaRepository<Game, Long> {
    Game findByGameId(Long gameId);
}