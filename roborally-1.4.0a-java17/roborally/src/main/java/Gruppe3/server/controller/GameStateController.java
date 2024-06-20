package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.repository.GameStateRepo;
import Gruppe3.server.repository.GameRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (!gameOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Optional<GameState> gameState = gameStateRepository.findByGameAndGamePlayerIdAndRegister(gameOptional.get(), gamePlayerId, register);
        if (gameState.isPresent()) {
            GameState gs = gameState.get();
            gs.setTimesPolled(gs.getTimesPolled()+1);  // Set the current time when the game state is polled
            gameStateRepository.save(gs);  // Save the updated game state

            // Optionally trigger resets based on conditions
            if (register == 4 && shouldResetGame(gameId)) {
                resetAllGameStatesByGame(gameId);
            }

            return ResponseEntity.ok(gs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean shouldResetGame(Long gameId) {
        List<GameState> gameStates = gameStateRepository.findByGame(gameRepository.getOne(gameId));
        // Check if all game states with register 4 have been polled at least once
        return gameStates.stream()
                .filter(gs -> gs.getRegister() == 4)
                .allMatch(gs -> gs.getTimesPolled() == 4);
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
                    existingGameState.setTimesPolled(gameStateDetails.getTimesPolled()+1); // Ensure lastPolled is also updated if needed
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
        System.out.println("Getting with GameId and Register");
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (!gameOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<GameState> gameStates = gameStateRepository.findByGameAndRegister(gameOptional.get(), register);
        gameStates.forEach(gs -> {
            gs.setTimesPolled(gs.getTimesPolled()+1);  // Set the current time when the game state is polled
            gameStateRepository.save(gs);  // Save the updated game state
        });


        if (register == 4 && gameStates.stream().allMatch(gs -> gs.getTimesPolled() == gameOptional.get().getNoOfPlayers())) {
            resetAllGameStatesByGame(gameId);
        }

        return ResponseEntity.ok(gameStates);
    }

    @PostMapping("/by-game/{gameId}/reset-all")
    public void resetAllGameStatesByGame(@PathVariable Long gameId) {
        System.out.println("Resetting all game states for game ID: " + gameId);
        List<GameState> gameStates = gameStateRepository.findByGame(gameRepository.getOne(gameId));
        if (gameStates.isEmpty()) {
            ResponseEntity.notFound().build();
            return;
        }

        // Manually delete each game state
        for (GameState gameState : gameStates) {
            gameStateRepository.delete(gameState);
        }

        // Create a new game state with null card for each player
        gameStates.stream()
                .collect(Collectors.groupingBy(GameState::getGamePlayerId))
                .forEach((playerId, list) -> {
                    GameState newGameState = new GameState();
                    Game game = new Game();  // Create a new game instance
                    game.setGameId(gameId);  // Set the gameId manually
                    newGameState.setGame(game);
                    newGameState.setGamePlayerId(playerId);
                    newGameState.setRegister(0);
                    newGameState.setCard(null);
                    newGameState.setTimesPolled(0);
                    gameStateRepository.save(newGameState);
                });

        ResponseEntity.noContent().build();
    }
}
