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

    @GetMapping("/{gameId}/{playerId}")
    public ResponseEntity<GameState> getGameStateByGameAndPlayer(@PathVariable Long gameId, @PathVariable Long playerId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        Optional<Player> playerOptional = playerRepository.findById(playerId);

        if (gameOptional.isPresent() && playerOptional.isPresent()) {
            Optional<GameState> gameState = gameStateRepository.findByGameAndPlayer(gameOptional.get(), playerOptional.get());
            return gameState.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{gameId}/{playerId}")
    public ResponseEntity<GameState> createGameState(@PathVariable Long gameId,
                                                     @PathVariable Long playerId,
                                                     @RequestBody GameState gameState) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        Optional<Player> playerOptional = playerRepository.findById(playerId);

        if (gameOptional.isPresent() && playerOptional.isPresent()) {
            Game game = gameOptional.get();
            Player player = playerOptional.get();

            Optional<GameState> existingGameState = gameStateRepository.findByGameAndPlayer(game, player);

            if (existingGameState.isPresent()) {
                return ResponseEntity.badRequest().build();
            } else {
                gameState.setGame(game);
                gameState.setPlayer(player);
                GameState savedGameState = gameStateRepository.save(gameState);
                return ResponseEntity.ok(savedGameState);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{gameId}/{playerId}")
    public ResponseEntity<GameState> updateGameState(@PathVariable Long gameId,
                                                     @PathVariable Long playerId,
                                                     @RequestBody GameState gameStateDetails) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        Optional<Player> playerOptional = playerRepository.findById(playerId);

        if (gameOptional.isPresent() && playerOptional.isPresent()) {
            GameState existingGameState = gameStateRepository.findByGameAndPlayer(gameOptional.get(), playerOptional.get())
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

    @DeleteMapping("/{gameId}/{playerId}")
    public ResponseEntity<Void> deleteGameState(@PathVariable Long gameId, @PathVariable Long playerId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        Optional<Player> playerOptional = playerRepository.findById(playerId);

        if (gameOptional.isPresent() && playerOptional.isPresent()) {
            Optional<GameState> gameStateOptional = gameStateRepository.findByGameAndPlayer(gameOptional.get(), playerOptional.get());
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
}
