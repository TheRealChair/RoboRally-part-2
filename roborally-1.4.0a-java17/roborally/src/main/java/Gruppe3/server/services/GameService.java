package Gruppe3.server.services;

import Gruppe3.server.model.Game;
import Gruppe3.server.repository.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private GameRepo gameRepo;

    public List<Game> getAllGames() {
        return gameRepo.findAll();
    }

    public Optional<Game> getGameById(Long gameId) {
        return gameRepo.findById(gameId);
    }

    public Game createGame(Game game) {
        return gameRepo.save(game);
    }

    public void deleteGame(Long gameId) {
        gameRepo.deleteById(gameId);
    }

    public Game updateGame(Game game) {
        return gameRepo.save(game);
    }
}
