package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.model.Player;
import Gruppe3.server.repository.GameRepo;
import Gruppe3.server.repository.GameStateRepo;
import Gruppe3.server.repository.PlayerRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/game-states")
public class GameStateController {

    private final GameStateRepo gameStateRepository;
    private final GameRepo gameRepository;
    private final PlayerRepo playerRepository;

    public GameStateController(GameStateRepo gameStateRepository, GameRepo gameRepository, PlayerRepo playerRepository) {
        this.gameStateRepository = gameStateRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public ResponseEntity<List<GameState>> getGameStates() {
        List<GameState> gameStateList = gameStateRepository.findAll();
        return ResponseEntity.ok(gameStateList);
    }

    @GetMapping("/{gameId}/{gamePlayerId}")
    public ResponseEntity<GameState> getGameStateByGameAndPlayer(@PathVariable Long gameId, @PathVariable int gamePlayerId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isPresent()) {
            Optional<GameState> gameState = gameStateRepository.findByGameAndGamePlayerId(gameOptional.get(), gamePlayerId);
            return gameState.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{gameId}/{gamePlayerId}")
    public ResponseEntity<GameState> createGameState(@PathVariable Long gameId,
                                                     @PathVariable int gamePlayerId,
                                                     @RequestBody GameState gameState) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        Optional<Player> playerOptional = playerRepository.findById((long) gamePlayerId); // Fetch the player

        if (gameOptional.isPresent() && playerOptional.isPresent()) { // Check if both game and player exist
            gameState.setGame(gameOptional.get());
            gameState.setGamePlayerId(playerOptional.get().getGamePlayerID()); // Set gamePlayerID from the player

            GameState savedGameState = gameStateRepository.save(gameState);
            return ResponseEntity.ok(savedGameState);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{gameId}/{gamePlayerId}")
    public ResponseEntity<GameState> updateGameState(@PathVariable Long gameId,
                                                     @PathVariable int gamePlayerId,
                                                     @RequestBody GameState gameStateDetails) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isPresent()) {
            GameState existingGameState = gameStateRepository.findByGameAndGamePlayerId(gameOptional.get(), gamePlayerId)
                    .orElse(null);

            if (existingGameState != null) {
                existingGameState.setRegister(gameStateDetails.getRegister());
                existingGameState.setCard(gameStateDetails.getCard());

                GameState updatedGameState = gameStateRepository.save(existingGameState);
                return ResponseEntity.ok(updatedGameState);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{gameId}/{gamePlayerId}")
    public ResponseEntity<Void> deleteGameState(@PathVariable Long gameId, @PathVariable int gamePlayerId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isPresent()) {
            Optional<GameState> gameStateOptional = gameStateRepository.findByGameAndGamePlayerId(gameOptional.get(), gamePlayerId);
            if (gameStateOptional.isPresent()) {
                gameStateRepository.delete(gameStateOptional.get());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-game/{gameId}")
    public ResponseEntity<List<GameState>> getGameStatesByGame(@PathVariable Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isPresent()) {
            List<GameState> gameStateList = gameStateRepository.findByGame(gameOptional.get());
            return ResponseEntity.ok(gameStateList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{gameId}/reset-cards")
    public ResponseEntity<Void> resetAllCardsToNullByGame(@PathVariable Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isPresent()) {
            List<GameState> gameStates = gameStateRepository.findByGame(gameOptional.get());
            for (GameState gameState : gameStates) {
                gameState.setCard(null);
                gameState.setRegister(0);
                gameStateRepository.save(gameState);
            }
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
