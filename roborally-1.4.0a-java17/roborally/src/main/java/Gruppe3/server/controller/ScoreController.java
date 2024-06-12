package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.Score;
import Gruppe3.server.model.Player;
import Gruppe3.server.repository.ScoreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    private final ScoreRepo scoreRepository;

    @Autowired
    public ScoreController(ScoreRepo scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @GetMapping
    public ResponseEntity<List<Score>> getscore() {
        List<Score> scoreList = scoreRepository.findAll();
        return ResponseEntity.ok(scoreList);
    }

    @PostMapping
    public ResponseEntity<Score> createScore(@RequestBody Score score) {
        Score savedScore = scoreRepository.save(score);
        return ResponseEntity.ok(savedScore);
    }

    @GetMapping("/{gameId}/{playerId}")
    public ResponseEntity<Score> getScoreById(@PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId) {
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<Score> score = scoreRepository.findByGameAndPlayer(game, player);
        return score.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{gameId}/{playerId}")
    public ResponseEntity<Score> updateScore(@PathVariable("gameId") Long gameId,
                                             @PathVariable("playerId") Long playerId,
                                             @RequestBody Score scoreDetails) {
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<Score> optionalScore = scoreRepository.findByGameAndPlayer(game, player);
        if (optionalScore.isPresent()) {
            Score existingScore = optionalScore.get();
            existingScore.setCheckpoints(scoreDetails.getCheckpoints());

            Score updatedScore = scoreRepository.save(existingScore);
            return ResponseEntity.ok(updatedScore);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{gameId}/{playerId}")
    public ResponseEntity<Void> deleteScore(@PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId) {
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<Score> optionalScore = scoreRepository.findByGameAndPlayer(game, player);
        if (optionalScore.isPresent()) {
            scoreRepository.delete(optionalScore.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
