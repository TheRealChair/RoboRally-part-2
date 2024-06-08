package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.repository.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameRepo gameRepository;

    @Autowired
    public GameController(GameRepo gameRepository) {
        this.gameRepository = gameRepository;
    }

    // Get all games
    @GetMapping
    public ResponseEntity<List<Game>> getGames() {
        List<Game> gameList = gameRepository.findAll();
        return ResponseEntity.ok(gameList);
    }

    // Create a new game
    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game savedGame = gameRepository.save(game);
        return ResponseEntity.ok(savedGame);
    }

    // Get a single game by ID
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Optional<Game> game = gameRepository.findById(id);
        return game.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing game
    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody Game gameDetails) {
        Optional<Game> optionalGame = gameRepository.findById(id);
        if (optionalGame.isPresent()) {
            Game existingGame = optionalGame.get();
            existingGame.setTurn_id(gameDetails.getTurn_id());
            existingGame.setNumber_of_players(gameDetails.getNumber_of_players());
            Game updatedGame = gameRepository.save(existingGame);
            return ResponseEntity.ok(updatedGame);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a game
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        Optional<Game> game = gameRepository.findById(id);
        if (game.isPresent()) {
            gameRepository.delete(game.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
