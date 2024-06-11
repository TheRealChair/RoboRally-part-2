package Gruppe3.server.controller;

import Gruppe3.server.model.GameState;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import Gruppe3.server.repository.GameStateRepo;
import Gruppe3.server.model.Player;
import Gruppe3.server.repository.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/registers")
public class GameStateController {

    private final GameStateRepo gameStateRepository;
    private final PlayerRepo playerRepository;

    @Autowired
    public GameStateController(GameStateRepo gameStateRepository, PlayerRepo playerRepository) {
        this.gameStateRepository = gameStateRepository;
        this.playerRepository = playerRepository;
    }

    // Get a single register by ID
    @GetMapping("/{gameId}/{gamePlayerId}")
    public ResponseEntity<GameState> getRegisterById(@PathVariable Long gameId, @PathVariable Long gamePlayerId) {
        GameId_GamePlayerId id = new GameId_GamePlayerId(gameId, gamePlayerId);
        Optional<GameState> register = gameStateRepository.findById(id);
        return register.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Assign a player to a register
    @PostMapping("/{gameId}/{gamePlayerId}/player/{playerId}")
    public ResponseEntity<GameState> assignPlayerToRegister(@PathVariable Long gameId, @PathVariable Long gamePlayerId, @PathVariable Long playerId) {
        GameId_GamePlayerId id = new GameId_GamePlayerId(gameId, gamePlayerId);
        Optional<GameState> optionalRegister = gameStateRepository.findById(id);
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);

        if (optionalRegister.isPresent() && optionalPlayer.isPresent()) {
            GameState gameState = optionalRegister.get();
            //gameState.setPlayer(optionalPlayer.get());
            GameState savedGameState = gameStateRepository.save(gameState);
            return ResponseEntity.ok(savedGameState);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
