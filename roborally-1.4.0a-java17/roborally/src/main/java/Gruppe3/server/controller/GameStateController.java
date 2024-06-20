package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.repository.GameStateRepo;
import Gruppe3.server.repository.GameRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/game-states")
public class GameStateController {

    private final GameStateRepo gameStateRepository;
    private final GameRepo gameRepository;

    public GameStateController(GameStateRepo gameStateRepository, GameRepo gameRepository) {
        this.gameStateRepository = gameStateRepository;
        this.gameRepository = gameRepository;
    }

    @GetMapping
    public ResponseEntity<List<GameState>> getAllGameStates() {
        List<GameState> gameStateList = gameStateRepository.findAll();
        return ResponseEntity.ok(gameStateList);
    }

    @GetMapping("/{gameId}/{gamePlayerId}")
    public ResponseEntity<List<GameState>> getGameStatesByPlayer(@PathVariable Long gameId, @PathVariable int gamePlayerId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        return gameOptional.map(game -> ResponseEntity.ok(gameStateRepository.findByGameAndGamePlayerId(game, gamePlayerId)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{gameId}/{gamePlayerId}/{register}")
    public ResponseEntity<GameState> getGameState(@PathVariable Long gameId, @PathVariable int gamePlayerId, @PathVariable int register) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        return gameOptional.map(game -> gameStateRepository.findByGameAndGamePlayerIdAndRegister(game, gamePlayerId, register)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{gameId}/{gamePlayerId}/{register}")
    public ResponseEntity<GameState> createOrUpdateGameState(@PathVariable Long gameId, @PathVariable int gamePlayerId, @PathVariable int register, @RequestBody GameState gameState) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (!gameOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        gameState.setGame(gameOptional.get());
        gameState.setGamePlayerId(gamePlayerId);
        gameState.setRegister(register); // Set register as it is part of the primary key

        GameState savedGameState = gameStateRepository.save(gameState);
        return ResponseEntity.ok(savedGameState);
    }

    @PutMapping("/{gameId}/{gamePlayerId}/{register}")
    public ResponseEntity<GameState> updateGameState(@PathVariable Long gameId, @PathVariable int gamePlayerId, @PathVariable int register, @RequestBody GameState gameStateDetails) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (!gameOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return gameStateRepository.findByGameAndGamePlayerIdAndRegister(gameOptional.get(), gamePlayerId, register)
                .map(existingGameState -> {
                    existingGameState.setCard(gameStateDetails.getCard());
                    existingGameState.setRegister(gameStateDetails.getRegister());
                    GameState updatedGameState = gameStateRepository.save(existingGameState);
                    return ResponseEntity.ok(updatedGameState);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{gameId}/{gamePlayerId}/{register}")
    public ResponseEntity<Void> deleteGameState(@PathVariable Long gameId, @PathVariable int gamePlayerId, @PathVariable int register) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (!gameOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        gameStateRepository.findByGameAndGamePlayerIdAndRegister(gameOptional.get(), gamePlayerId, register).ifPresent(gameStateRepository::delete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-game/{gameId}")
    public ResponseEntity<List<GameState>> getGameStatesByGame(@PathVariable Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        return gameOptional.map(game -> ResponseEntity.ok(gameStateRepository.findByGame(game)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-game/{gameId}/register/{register}")
    public ResponseEntity<List<GameState>> getGameStatesByGameAndRegister(@PathVariable Long gameId, @PathVariable int register) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        return gameOptional.map(game -> ResponseEntity.ok(gameStateRepository.findByGameAndRegister(game, register)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
