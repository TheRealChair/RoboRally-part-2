package Gruppe3.server.controller;

import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.model.Player;
import Gruppe3.server.repository.GameStateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/game-states")
public class GameStateController {

    private final GameStateRepo gameStateRepository;

    @Autowired
    public GameStateController(GameStateRepo gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

    @GetMapping
    public ResponseEntity<List<GameState>> getGameStates() {
        List<GameState> gameStateList = gameStateRepository.findAll();
        return ResponseEntity.ok(gameStateList);
    }

    @PostMapping
    public ResponseEntity<GameState> createGameState(@RequestBody GameState gameState) {
        GameState savedGameState = gameStateRepository.save(gameState);
        return ResponseEntity.ok(savedGameState);
    }

    @GetMapping("/{gameId}/{playerId}")
    public ResponseEntity<GameState> getGameStateById(@PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId) {
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<GameState> gameState = gameStateRepository.findByGameAndPlayer(game, player);
        return gameState.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{gameId}/{playerId}")
    public ResponseEntity<GameState> updateGameState(@PathVariable("gameId") Long gameId,
                                                     @PathVariable("playerId") Long playerId,
                                                     @RequestBody GameState gameStateDetails) {
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<GameState> optionalGameState = gameStateRepository.findByGameAndPlayer(game, player);
        if (optionalGameState.isPresent()) {
            GameState existingGameState = optionalGameState.get();
            existingGameState.setRegister(gameStateDetails.getRegister());
            existingGameState.setCard(gameStateDetails.getCard());

            GameState updatedGameState = gameStateRepository.save(existingGameState);
            return ResponseEntity.ok(updatedGameState);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{gameId}/{playerId}")
    public ResponseEntity<Void> deleteGameState(@PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId) {
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<GameState> optionalGameState = gameStateRepository.findByGameAndPlayer(game, player);
        if (optionalGameState.isPresent()) {
            gameStateRepository.delete(optionalGameState.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

