package Gruppe3.server.controller;

import Gruppe3.server.model.GameState;
import Gruppe3.server.repository.GameStateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gameStates")
public class GameStateController {

    private final GameStateRepo gameStateRepository;

    @Autowired
    public GameStateController(GameStateRepo gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

    // Get all game states
    @GetMapping
    public ResponseEntity<List<GameState>> getGameStates() {
        List<GameState> gameStateList = gameStateRepository.findAll();
        return ResponseEntity.ok(gameStateList);
    }

    // Create a new game state
    @PostMapping
    public ResponseEntity<GameState> createGameState(@RequestBody GameState gameState) {
        GameState savedGameState = gameStateRepository.save(gameState);
        return ResponseEntity.ok(savedGameState);
    }

    // Get a single game state by ID
    @GetMapping("/{id}")
    public ResponseEntity<GameState> getGameStateById(@PathVariable Long id) {
        Optional<GameState> gameState = gameStateRepository.findById(id);
        return gameState.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing game state
    @PutMapping("/{id}")
    public ResponseEntity<GameState> updateGameState(@PathVariable Long id, @RequestBody GameState gameStateDetails) {
        Optional<GameState> optionalGameState = gameStateRepository.findById(id);
        if (optionalGameState.isPresent()) {
            GameState existingGameState = optionalGameState.get();
            // Update fields accordingly
            existingGameState.setRegisterId(gameStateDetails.getRegisterId());
            existingGameState.setCard(gameStateDetails.getCard());
            existingGameState.setPlayer(gameStateDetails.getPlayer());
            // Ensure to set the game if necessary
            // existingGameState.setGame(gameStateDetails.getGame());
            GameState updatedGameState = gameStateRepository.save(existingGameState);
            return ResponseEntity.ok(updatedGameState);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a game state
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameState(@PathVariable Long id) {
        Optional<GameState> gameState = gameStateRepository.findById(id);
        if (gameState.isPresent()) {
            gameStateRepository.delete(gameState.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
