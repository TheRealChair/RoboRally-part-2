package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Player;
import Gruppe3.server.repository.GameRepo;
import Gruppe3.server.repository.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * REST controller for managing player data.
 * Provides CRUD operations on players, facilitating the retrieval, creation, update, and deletion of player information
 * linked to specific games.
 *
 * @author Karl, Balder
 */
@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerRepo playerRepository;
    private final GameRepo gameRepository;

    @Autowired
    public PlayerController(PlayerRepo playerRepository, GameRepo gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    // Get all players
    @GetMapping
    public ResponseEntity<List<Player>> getPlayers() {
        List<Player> playerList = playerRepository.findAll();
        return ResponseEntity.ok(playerList);
    }

    // Get players by gameId
    @GetMapping("/games/{gameId}")
    public ResponseEntity<List<Player>> getPlayersByGameId(@PathVariable Long gameId) {
        List<Player> players = playerRepository.findByGame_GameId(gameId);
        return ResponseEntity.ok(players);
    }

    //Get gameId from player
    @GetMapping("/game/{playerId}")
    public ResponseEntity<Game> getGameByPlayerId(@PathVariable Long playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isPresent()) {
            return ResponseEntity.ok(player.get().getGame());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new player associated with a game
    @PostMapping("/games/{gameId}")
    public ResponseEntity<Player> createPlayer(@PathVariable Long gameId, @RequestBody Player player) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();

            // Get the current number of players in the game
            int numberOfPlayers = playerRepository.countByGame_GameId(gameId);
            player.setGamePlayerID(numberOfPlayers + 1); // Assign the next available gamePlayerId
            player.setGame(game); // Set the game association

            Player savedPlayer = playerRepository.save(player);

            return ResponseEntity.ok(savedPlayer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Get a single player by ID
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        Optional<Player> player = playerRepository.findById(id);
        return player.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing player
    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player playerDetails) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if (optionalPlayer.isPresent()) {
            Player existingPlayer = optionalPlayer.get();

            // Update fields from playerDetails
            if (playerDetails.getGamePlayerID() != 0) {
                existingPlayer.setGamePlayerID(playerDetails.getGamePlayerID());
            }

            // Save the updated player
            Player updatedPlayer = playerRepository.save(existingPlayer);

            return ResponseEntity.ok(updatedPlayer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Delete a player
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            playerRepository.delete(player.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
