package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Player; // Import Player entity
import Gruppe3.server.repository.GameRepo;
import Gruppe3.server.repository.PlayerRepo; // Import PlayerRepo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameRepo gameRepository;
    private final PlayerRepo playerRepository; // Inject PlayerRepo

    @Autowired
    public GameController(GameRepo gameRepository, PlayerRepo playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
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

    // Get all players of a game
    @GetMapping("/{gameId}/players")
    public ResponseEntity<List<Player>> getPlayersByGameId(@PathVariable Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            List<Player> players = gameOptional.get().getPlayers();
            return ResponseEntity.ok(players);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Add a player to a game
    @PostMapping("/{gameId}/players")
    public ResponseEntity<Player> addPlayerToGame(@PathVariable Long gameId, @RequestBody Player player) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            player.setGame(game);
            Player savedPlayer = playerRepository.save(player);
            return ResponseEntity.ok(savedPlayer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a player in a game
    @PutMapping("/{gameId}/players/{playerId}")
    public ResponseEntity<Player> updatePlayerInGame(@PathVariable Long gameId, @PathVariable Long playerId, @RequestBody Player playerDetails) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Optional<Player> optionalPlayer = playerRepository.findById(playerId);
            if (optionalPlayer.isPresent()) {
                Player existingPlayer = optionalPlayer.get();
                existingPlayer.setPlayerName(playerDetails.getPlayerName());
                existingPlayer.setScore(playerDetails.getScore());
                Player updatedPlayer = playerRepository.save(existingPlayer);
                return ResponseEntity.ok(updatedPlayer);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a player from a game
    @DeleteMapping("/{gameId}/players/{playerId}")
    public ResponseEntity<Void> deletePlayerFromGame(@PathVariable Long gameId, @PathVariable Long playerId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Optional<Player> player = playerRepository.findById(playerId);
            if (player.isPresent()) {
                playerRepository.delete(player.get());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
