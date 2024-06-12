package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.model.Player;
import Gruppe3.server.model.Position;
import Gruppe3.server.repository.PositionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionRepo positionRepository;

    @Autowired
    public PositionController(PositionRepo positionRepository){
        this.positionRepository = positionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Position>> getPositions(){
        List<Position> positionList = positionRepository.findAll();
        return ResponseEntity.ok(positionList);
    }

    @PostMapping
    public ResponseEntity<Position> createPosition(@RequestBody Position position){
        Position savedPosition = positionRepository.save(position);
        return ResponseEntity.ok(savedPosition);
    }

    @GetMapping("/{game_id}/{player_id}")
    public ResponseEntity<Position> getPositionById(@PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId){
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<Position> position = positionRepository.findByGameIdAndPlayerId(game, player);
        return position.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{game_id}/{player_id}")
    public ResponseEntity<Position> updatePosition(@PathVariable("gameId") Long gameId,
                                                     @PathVariable("playerId") Long playerId,
                                                     @RequestBody Position positionDetails) {
        Game game = new Game();
        game.setGameId(gameId);

        Player player = new Player();
        player.setPlayerId(playerId);

        Optional<Position> optionalPosition = positionRepository.findByGameIdAndPlayerId(game, player);
        if (optionalPosition.isPresent()) {
            Position existingPosition = optionalPosition.get();
            existingPosition.setPositionX(positionDetails.getPositionX());
            existingPosition.setPositionY(positionDetails.getPositionY());
            existingPosition.setHeading(positionDetails.getHeading());

            Position updatedPosition = positionRepository.save(existingPosition);
            return ResponseEntity.ok(updatedPosition);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        Optional<Position> position = positionRepository.findById(id);
        if (position.isPresent()) {
            positionRepository.delete(position.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
