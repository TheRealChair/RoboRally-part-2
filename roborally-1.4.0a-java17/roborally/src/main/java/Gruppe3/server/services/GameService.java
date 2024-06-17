package Gruppe3.server.services;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Player;
import Gruppe3.server.model.Position;
import Gruppe3.server.repository.GameRepo;
import Gruppe3.server.repository.PlayerRepo;
import Gruppe3.server.repository.PositionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    public static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private static PlayerRepo playerRepository;
    private static PositionRepo positionRepository;
    public GameService(PlayerRepo playerRepository, PositionRepo positionRepository){
        this.playerRepository = playerRepository;
        this.positionRepository = positionRepository;
    }

    public static void logPlayerPosition(Long game, Long playerId) {
        Player player = playerRepository.findByPlayerId(playerId);

        if(player == null){
            logger.error("Player {} not found", playerId);
        }

        Optional<Position> optionalPositions = positionRepository.findByGameIdAndPlayerId(game, playerId);

        if(optionalPositions.isEmpty()){
            logger.error("No Positions found by player {}", playerId);
        }
        Position position = optionalPositions.get();
        logger.info("Player {} is at position ({}, {}) heading {}",
                player.getPlayerId(), position.getPositionX(), position.getPositionY(), position.getHeading());

    }

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
